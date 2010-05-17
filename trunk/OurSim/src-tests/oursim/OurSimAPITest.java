package oursim;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import oursim.availability.AvailabilityRecord;
import oursim.dispatchableevents.jobevents.JobEventCounter;
import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.dispatchableevents.taskevents.TaskEventCounter;
import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.entities.Job;
import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.input.Input;
import oursim.input.InputAbstract;
import oursim.input.Workload;
import oursim.input.WorkloadAbstract;
import oursim.policy.DefaultSharingPolicy;
import oursim.simulationevents.EventQueue;
import oursim.simulationevents.FinishJobEvent;
import oursim.simulationevents.FinishTaskEvent;
import oursim.simulationevents.StartedTaskEvent;
import oursim.simulationevents.SubmitJobEvent;
import oursim.simulationevents.WorkerAvailableEvent;
import oursim.simulationevents.WorkerUnavailableEvent;

public class OurSimAPITest {

	OurSimAPI oursim;
	
	JobEventCounter jobEventCounter;

	TaskEventCounter taskEventCounter;

	@SuppressWarnings("unchecked")
	Set<Class> jobEvents;

	@SuppressWarnings("unchecked")
	Set<Class> taskEvents;

	@SuppressWarnings("unchecked")
	Set<Class> workerEvents;

	final int NUMBER_OF_JOBS_BY_PEER = 10;
	final int NUMBER_OF_TASKS_BY_JOB = 1;
	final int NUMBER_OF_PEERS = 10;
	final int NUMBER_OF_RESOURCES_BY_PEER = 10;
	final int RESOURCE_MIPS_RATING = 3000;
	final int TOTAL_OF_JOBS = NUMBER_OF_PEERS * NUMBER_OF_JOBS_BY_PEER;

	final long JOB_LENGTH = 100;
	final long JOB_SUBMISSION_TIME = 0;

	List<Job> jobs;
	List<Peer> peers;

	long nextJobId = 0;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {

		oursim = new OurSimAPI();
		
		jobEventCounter = new JobEventCounter();
		JobEventDispatcher.getInstance().addListener(jobEventCounter);

		taskEventCounter = new TaskEventCounter();
		TaskEventDispatcher.getInstance().addListener(taskEventCounter);

		jobEvents = new HashSet<Class>();
		jobEvents.add(SubmitJobEvent.class);
		jobEvents.add(FinishJobEvent.class);

		taskEvents = new HashSet<Class>();
		taskEvents.add(StartedTaskEvent.class);
		taskEvents.add(FinishTaskEvent.class);

		workerEvents = new HashSet<Class>();
		workerEvents.add(WorkerAvailableEvent.class);
		workerEvents.add(WorkerUnavailableEvent.class);

		jobs = new ArrayList<Job>(TOTAL_OF_JOBS);
		peers = new ArrayList<Peer>(NUMBER_OF_PEERS);

		for (int i = 0; i < NUMBER_OF_PEERS; i++) {
			peers.add(new Peer("p_" + i, NUMBER_OF_RESOURCES_BY_PEER, RESOURCE_MIPS_RATING, DefaultSharingPolicy.getInstance()));
		}

	}

	@After
	public void tearDown() throws Exception {

		EventQueue.getInstance().clear();
		JobEventDispatcher.getInstance().removeListener(jobEventCounter);
		TaskEventDispatcher.getInstance().removeListener(taskEventCounter);
		EventQueue.totalNumberOfEvents = 0;
		nextJobId = 0;

	}

	/**
	 * Cenário de Teste: Todos os peers possuem uma demanda que casa
	 * perfeitamente com seus recursos. Ninguém precisa recorrer aos recursos
	 * alheios.
	 * 
	 * Asserção: Espera-se que todos os jobs sejam concluídos no tempo mínimo
	 * necessário (i.e. a duração especificada para cada job) e que todos os
	 * jobs sejam executados no próprio peer de origem.
	 */
	@Test
	public void testRun_1() {

		// Define as demandas para cada peer
		Input<Job> workload = generateDefaultWorkload();

		// Define os eventos de disponibilidade para cada recurso de cada peer.
		// Nesse cenário os recursos ficarão disponíveis o tempo suficiente para
		// terminar as demandas de cada job.
		Input<AvailabilityRecord> availability = generateResourceAvailability(JOB_SUBMISSION_TIME, JOB_LENGTH);

		oursim.run(peers, workload, availability);

		int totalDeTasks = TOTAL_OF_JOBS * NUMBER_OF_TASKS_BY_JOB;
		int totalDeWorkers = NUMBER_OF_PEERS * NUMBER_OF_RESOURCES_BY_PEER;

		int totalDeJobEvents = TOTAL_OF_JOBS * jobEvents.size();
		int totalDeTaskEvents = totalDeTasks * taskEvents.size();
		int totalDeWorkerEvents = totalDeWorkers * workerEvents.size();

		int totalDeEventos = totalDeJobEvents + totalDeTaskEvents + totalDeWorkerEvents;

		assertEquals(TOTAL_OF_JOBS, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(0, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(totalDeTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(0, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());
		assertEquals(totalDeEventos, EventQueue.totalNumberOfEvents);

		for (Job job : jobs) {
			// Espera-se que todos os jobs sejam concluídos no tempo mínimo
			// esperado (i.e. a duração especificado para cada job) e que todos
			// os jobs sejam executados no próprio peer de origem.
			assertEquals((JOB_SUBMISSION_TIME + JOB_LENGTH), (long) job.getFinishTime());
			// Ninguém precisa recorrer aos recursos alheios.
			assertEquals(job.getSourcePeer(), job.getTargetPeers().get(0));
		}

	}

	/**
	 * Cenário de Teste: Metade dos peers possue uma demanda que casa
	 * perfeitamente com seus recursos. A outra metade possui uma demanda duas
	 * vezes maior do que sua quantidade de recursos.
	 * 
	 * Espera-se que todos os jobs de quem não depende de recursos alheios sejam
	 * concluídos no tempo mínimo esperado (i.e. a duração especificado para
	 * cada job) e que todos os jobs sejam executados no próprio peer de origem.
	 */
	@Test
	public void testRun_2() {

		final int NUMBER_OF_OVERLOADED_PEERS = NUMBER_OF_PEERS / 2;

		final ArrayList<Peer> overloadedPeers = new ArrayList<Peer>(NUMBER_OF_OVERLOADED_PEERS);

		Workload defaultWorkload = generateDefaultWorkload();

		// workload extra, responsável por gerar sobrecarga em alguns dos peers
		WorkloadAbstract workload = new WorkloadAbstract() {

			@Override
			protected void setUp() {
				// Atribuir à metade dos peers uma demanda duas vezes maior do
				// que sua quantidade de recursos.
				for (int k = 0; k < NUMBER_OF_OVERLOADED_PEERS; k++) {
					Peer peer = peers.get(k);
					overloadedPeers.add(peer);
					for (int i = 0; i < NUMBER_OF_JOBS_BY_PEER; i++) {
						Job job = new Job(nextJobId, JOB_SUBMISSION_TIME, peer);
						for (int j = 0; j < NUMBER_OF_TASKS_BY_JOB; j++) {
							job.addTask("", JOB_LENGTH);
						}
						this.inputs.add(job);
						nextJobId++;
						jobs.add(job);
					}
				}
			}

		};

		// workload final: default + o extra, que vai gerar sobrecarga e, por
		// isso, espera em fila.
		workload.merge(defaultWorkload);

		// Define os eventos de disponibilidade para cada recurso de cada peer.
		// Nesse cenário os recursos ficarão disponíveis o tempo suficiente para
		// terminar as demandas de cada job.
		Input<AvailabilityRecord> availability = generateResourceAvailability(0, JOB_LENGTH * 2);

		oursim.run(peers, workload, availability);

		int totalDeJobs = (int) (TOTAL_OF_JOBS * 1.5);
		int totalDeTasks = totalDeJobs * NUMBER_OF_TASKS_BY_JOB;
		int totalDeWorkers = NUMBER_OF_PEERS * NUMBER_OF_RESOURCES_BY_PEER;

		int totalDeJobEvents = totalDeJobs * jobEvents.size();
		int totalDeTaskEvents = totalDeTasks * taskEvents.size();
		int totalDeWorkerEvents = totalDeWorkers * workerEvents.size();

		int totalDeEventos = totalDeJobEvents + totalDeTaskEvents + totalDeWorkerEvents;

		assertEquals(totalDeJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(0, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(totalDeTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(0, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());
		assertEquals(totalDeEventos, EventQueue.totalNumberOfEvents);

		int numberOfJobsFromOverloadedPeersTimelyFinished = 0;
		int numberOfEnqueuedJobsFromOverloadedPeers = 0;
		for (Job job : jobs) {
			if (overloadedPeers.contains(job.getSourcePeer())) {
				// se teminou no tempo certo só pode ser porque executou no
				// próprio peer origem, visto que todos os outros estão cheios
				// com suas próprias demandas
				if (job.getFinishTime() == (JOB_SUBMISSION_TIME + JOB_LENGTH)) {
					assertEquals(job.getSourcePeer(), job.getTargetPeers().get(0));
					numberOfJobsFromOverloadedPeersTimelyFinished++;
				} else {
					numberOfEnqueuedJobsFromOverloadedPeers++;
				}
			} else {
				// Espera-se que esses jobs sejam concluídos no tempo mínimo
				// esperado (i.e. a duração especificado para cada job) e que
				// todos os jobs sejam executados no próprio peer de origem.
				assertEquals((JOB_SUBMISSION_TIME + JOB_LENGTH), (long) job.getFinishTime());
				// Nenhum desses jobs precisa recorrer a recursos alheios.
				assertEquals(job.getSourcePeer(), job.getTargetPeers().get(0));
			}
		}

		assertEquals(NUMBER_OF_OVERLOADED_PEERS * NUMBER_OF_JOBS_BY_PEER, numberOfJobsFromOverloadedPeersTimelyFinished);
		assertEquals(NUMBER_OF_OVERLOADED_PEERS * NUMBER_OF_JOBS_BY_PEER, numberOfEnqueuedJobsFromOverloadedPeers);

	}

	private Input<AvailabilityRecord> generateResourceAvailability(final long timestamp, final long duration) {
		Input<AvailabilityRecord> availability = new InputAbstract<AvailabilityRecord>() {
			@Override
			protected void setUp() {
				this.inputs = new LinkedList<AvailabilityRecord>();
				for (Peer peer : peers) {
					for (Machine machine : peer.getResources()) {
						this.inputs.add(new AvailabilityRecord(machine.getName(), timestamp, duration));
					}
				}

			}
		};
		return availability;
	}

	private Workload generateDefaultWorkload() {
		return generateWorkload(NUMBER_OF_JOBS_BY_PEER, NUMBER_OF_TASKS_BY_JOB, JOB_SUBMISSION_TIME, JOB_LENGTH);
	}

	private Workload generateWorkload(final int numberOfJobsByPeer, final int numberOfTasksByJob, final long jobsSubmissionTime, final long jobLength) {

		Workload allWorkloads = new WorkloadAbstract() {
			@Override
			protected void setUp() {
			}
		};

		for (final Peer peer : peers) {

			WorkloadAbstract workloadForPeer = new WorkloadAbstract() {
				@Override
				protected void setUp() {
					for (int i = 0; i < numberOfJobsByPeer; i++) {
						Job job = new Job(nextJobId, jobsSubmissionTime, peer);
						for (int j = 0; j < numberOfTasksByJob; j++) {
							job.addTask("", jobLength);
						}
						this.inputs.add(job);
						nextJobId++;
						jobs.add(job);
					}
				}
			};

			peer.setWorkload(workloadForPeer);
			allWorkloads.merge(workloadForPeer);
		}

		return allWorkloads;
	}
	
}