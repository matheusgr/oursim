package oursim;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
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
import oursim.policy.ResourceSharingPolicy;
import oursim.simulationevents.EventQueue;
import oursim.simulationevents.FinishJobEvent;
import oursim.simulationevents.FinishTaskEvent;
import oursim.simulationevents.StartedTaskEvent;
import oursim.simulationevents.SubmitJobEvent;
import oursim.simulationevents.SubmitTaskEvent;
import oursim.simulationevents.WorkerAvailableEvent;
import oursim.simulationevents.WorkerUnavailableEvent;

public class OurSimAPIVolatilityTest {

	OurSimAPI oursim;

	JobEventCounter jobEventCounter;

	TaskEventCounter taskEventCounter;

	@SuppressWarnings("unchecked")
	Set<Class> jobEvents;

	@SuppressWarnings("unchecked")
	Set<Class> taskEvents;

	@SuppressWarnings("unchecked")
	Set<Class> workerEvents;

	final int RESOURCE_MIPS_RATING = 3000;

	List<Job> jobs;
	List<Peer> peers;

	long nextJobId = 0;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {

		oursim = new OurSimAPI(EventQueue.getInstance());

		jobEventCounter = new JobEventCounter();
		JobEventDispatcher.getInstance().addListener(jobEventCounter);

		taskEventCounter = new TaskEventCounter();
		TaskEventDispatcher.getInstance().addListener(taskEventCounter);

		jobEvents = new HashSet<Class>();
		jobEvents.add(SubmitJobEvent.class);
		jobEvents.add(FinishJobEvent.class);

		taskEvents = new HashSet<Class>();
		taskEvents.add(SubmitTaskEvent.class);
		taskEvents.add(StartedTaskEvent.class);
		taskEvents.add(FinishTaskEvent.class);

		workerEvents = new HashSet<Class>();
		workerEvents.add(WorkerAvailableEvent.class);
		workerEvents.add(WorkerUnavailableEvent.class);

		jobs = new ArrayList<Job>();
		peers = new ArrayList<Peer>();

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
	 * Cenário de Teste: Recursos voláteis ao long da simulação.
	 * 
	 * Asserção: Espera-se que a simulação se dê de forma satisfatório mesmo na
	 * presença de recursos voláteis.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testRun_3() {

		final int numberOfPeers = 1;
		final int numberOfResources = 6;

		peers = new ArrayList<Peer>(numberOfPeers);

		final Peer peer = new Peer("the_peer", numberOfResources, RESOURCE_MIPS_RATING, ResourceSharingPolicy.DEFAULT_SHARING_POLICY);
		peers.add(peer);

		Input<AvailabilityRecord> availability = new InputAbstract<AvailabilityRecord>() {
			@Override
			protected void setUp() {
				int currentMachineIndex = 0;
				Machine currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 15, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 30, 25));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 65, 5));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 73, 4));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 80, 6));

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 20, 20));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 60, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 80, 20));

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 40, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 70, 10));

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 20));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 30, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 60, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 75, 10));

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 15, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 40, 20));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 80, 20));

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 20, 50));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 74, 2));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 80, 20));

			}
		};

		Workload workload = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				OurSimAPIVolatilityTest.addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);

				// aos 32 segundos (devido à volatidade) todos os jobs já terão
				// acabado. daí começa a
				// segunda rodada de submissões

				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 1, peer, this.inputs, jobs);// 5
				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 2, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);// 8
				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 20, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 10, peer, this.inputs, jobs);// 11
				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 12, peer, this.inputs, jobs);
				OurSimAPIVolatilityTest.addJob(nextJobId++, 32, 15, peer, this.inputs, jobs);
			}

		};

		oursim.run(peers, workload, availability);

		// umas dos jobs não vai ser completado por indisponiblidade de máquina.
		int numberOfFinishedJobs = jobs.size() - 1;
		int numberOfFinishedTasks = numberOfFinishedJobs;
		assertEquals(numberOfFinishedJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(11, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numberOfFinishedTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(11, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

	}

	public static void addJob(long jobId, long submissionTime, long duration, final Peer peer, Collection<Job>... collectionsOfJob) {
		Job job = new Job(jobId, submissionTime, duration, peer);
		for (Collection<Job> collection : collectionsOfJob) {
			collection.add(job);
		}
	}

}