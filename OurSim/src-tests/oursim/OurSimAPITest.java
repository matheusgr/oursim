package oursim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import oursim.availability.AvailabilityRecord;
import oursim.entities.Job;
import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.events.EventQueue;
import oursim.events.FinishJobEvent;
import oursim.events.FinishTaskEvent;
import oursim.input.Input;
import oursim.input.InputAbstract;
import oursim.policy.DefaultSharingPolicy;
import oursim.policy.OurGridScheduler;

public class OurSimAPITest extends TestCase {

	OurSimAPI oursim;

	@Override
	protected void setUp() throws Exception {
		oursim = new OurSimAPI();
	}

	@Override
	protected void tearDown() throws Exception {
		EventQueue.getInstance().clear();
		FinishJobEvent.amountOfFinishedJobs = 0;
		OurGridScheduler.numberOfPreemptionsForAllJobs = 0;
		FinishTaskEvent.amountOfFinishedTasks = 0;
		OurGridScheduler.numberOfPreemptionsForAllTasks = 0;
		EventQueue.amountOfEvents = 0;
	}

	/**
	 * Cenário de Teste: Todos os peers possuem uma demanda que casa
	 * perfeitamente com seus recursos. Ninguém precisa recorrer aos recursos
	 * alheios.
	 * 
	 * Espera-se que todos os jobs sejam concluídos no tempo mínimo esperado
	 * (i.e. a duração especificado para cada job) e que todos os jobs sejam
	 * executados no próprio peer de origem.
	 */
	public void testRun_1() {
		final List<Job> jobs = new ArrayList<Job>();
		final long JOB_LENGTH = 100;
		final long JOB_SUBMISSION_TIME = 0;
		final int NUMBER_OF_JOBS_BY_PEER = 10;
		final int NUMBER_OF_TASKS_BY_JOB = 1;
		final int NUMBER_OF_PEERS = 10;
		final int NUMBER_OF_RESOURCES_BY_PEER = 10;
		final int RESOURCE_MIPS_RATING = 3000;
		final int TOTAL_OF_JOBS = NUMBER_OF_PEERS * NUMBER_OF_JOBS_BY_PEER;

		final ArrayList<Peer> peers = new ArrayList<Peer>(NUMBER_OF_PEERS);

		for (int i = 0; i < NUMBER_OF_PEERS; i++) {
			peers.add(new Peer("p_" + i, NUMBER_OF_RESOURCES_BY_PEER, RESOURCE_MIPS_RATING, DefaultSharingPolicy.getInstance()));
		}

		// Define as demandas para cada peer
		Input<Job> workload = new InputAbstract<Job>() {
			@Override
			protected void setUp() {
				long nextJobId = 0;
				for (Peer peer : peers) {
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

		// Define os eventos de disponibilidade para cada recurso de cada peer.
		// Nesse cenário os recursos ficarão disponíveis o tempo suficiente para
		// terminar as demandas de cada job.
		Input<AvailabilityRecord> availability = new InputAbstract<AvailabilityRecord>() {
			@Override
			protected void setUp() {
				this.inputs = new LinkedList<AvailabilityRecord>();
				for (Peer peer : peers) {
					for (Machine machine : peer.getResources()) {
						this.inputs.add(new AvailabilityRecord(machine.getName(), 0, JOB_LENGTH));
					}
				}

			}
		};

		oursim.run(peers, workload, availability);

		assertEquals(TOTAL_OF_JOBS, FinishJobEvent.amountOfFinishedJobs);
		assertEquals(0, OurGridScheduler.numberOfPreemptionsForAllJobs);
		assertEquals(100, FinishTaskEvent.amountOfFinishedTasks);
		assertEquals(0, OurGridScheduler.numberOfPreemptionsForAllTasks);
		assertEquals(600, EventQueue.amountOfEvents);

		for (Job job : jobs) {
			// Espera-se que todos os jobs sejam concluídos no tempo mínimo
			// esperado (i.e. a duração especificado para cada job) e que todos
			// os jobs sejam executados no próprio peer de origem.
			assertEquals(JOB_LENGTH, (long) job.getFinishTime());
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
	public void testRun_2() {

		final List<Job> jobs = new ArrayList<Job>();
		final long JOB_LENGTH = 100;
		final long JOB_SUBMISSION_TIME = 0;
		final int NUMBER_OF_JOBS_BY_PEER = 10;
		final int NUMBER_OF_TASKS_BY_JOB = 1;
		final int NUMBER_OF_PEERS = 10;
		final int NUMBER_OF_RESOURCES_BY_PEER = 10;
		final int RESOURCE_MIPS_RATING = 3000;
		final int TOTAL_OF_JOBS = NUMBER_OF_PEERS * NUMBER_OF_JOBS_BY_PEER;

		final ArrayList<Peer> peers = new ArrayList<Peer>(NUMBER_OF_PEERS);

		for (int i = 0; i < NUMBER_OF_PEERS; i++) {
			peers.add(new Peer("p_" + i, NUMBER_OF_RESOURCES_BY_PEER, RESOURCE_MIPS_RATING, DefaultSharingPolicy.getInstance()));
		}

		// Define as demandas para cada peer
		Input<Job> workload = new InputAbstract<Job>() {
			@Override
			protected void setUp() {
				int indexOfPeer = 0;
				long nextJobId = 0;
				for (Peer peer : peers) {
					indexOfPeer++;
					for (int i = 0; i < NUMBER_OF_JOBS_BY_PEER; i++) {
						Job job = new Job(nextJobId, JOB_SUBMISSION_TIME, peer);
						for (int j = 0; j < NUMBER_OF_TASKS_BY_JOB; j++) {
							job.addTask("", JOB_LENGTH);
						}
						this.inputs.add(job);
						nextJobId++;
						jobs.add(job);
					}

					// para os peers que possuem demanda maior do que sua
					// quantidade de recursos.
					if (indexOfPeer > NUMBER_OF_PEERS / 2) {
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
			}
		};

		// Define os eventos de disponibilidade para cada recurso de cada peer.
		// Nesse cenário os recursos ficarão disponíveis o tempo suficiente para
		// terminar as demandas de cada job.
		Input<AvailabilityRecord> availability = new InputAbstract<AvailabilityRecord>() {
			@Override
			protected void setUp() {
				this.inputs = new LinkedList<AvailabilityRecord>();
				for (Peer peer : peers) {
					for (Machine machine : peer.getResources()) {
						this.inputs.add(new AvailabilityRecord(machine.getName(), 0, 50 * JOB_LENGTH));
					}
				}

			}
		};

		oursim.run(peers, workload, availability);
		int totalDeJobs = (int) (TOTAL_OF_JOBS * 1.5);
		assertEquals(totalDeJobs, FinishJobEvent.amountOfFinishedJobs);
		assertEquals(0, OurGridScheduler.numberOfPreemptionsForAllJobs);
		assertEquals(totalDeJobs, FinishTaskEvent.amountOfFinishedTasks);
		assertEquals(0, OurGridScheduler.numberOfPreemptionsForAllTasks);
		assertEquals(600, EventQueue.amountOfEvents);

		for (Job job : jobs) {
			// Espera-se que todos os jobs sejam concluídos no tempo mínimo
			// esperado (i.e. a duração especificado para cada job) e que todos
			// os jobs sejam executados no próprio peer de origem.
			assertEquals(JOB_LENGTH, (long) job.getFinishTime());
			// Ninguém precisa recorrer aos recursos alheios.
			assertEquals(job.getSourcePeer(), job.getTargetPeers().get(0));
		}

	}

}