package oursim;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import oursim.availability.AvailabilityRecord;
import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.input.Input;
import oursim.input.InputAbstract;
import oursim.input.Workload;
import oursim.input.WorkloadAbstract;
import oursim.policy.JobSchedulerPolicy;
import oursim.policy.OurGridReplicationScheduler;
import oursim.policy.ResourceSharingPolicy;
import oursim.simulationevents.EventQueue;

public class OurGridReplicationSchedulerTest extends AbstractOurSimAPITest {

	private static final int REPLICATION_LEVEL = 3;

	/**
	 * Cenário de Teste: Recursos voláteis ao longo da simulação e escalonador
	 * que replica tasks.
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
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 30, 2));

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 20, 12));

				currentMachine = peer.getResources().get(currentMachineIndex++);

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 20));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 30, 2));

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 15, 10));

				currentMachine = peer.getResources().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 20, 12));

			}
		};

		Workload workload = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
			}

		};

		JobSchedulerPolicy jobScheduler = new OurGridReplicationScheduler(EventQueue.getInstance(), peers, REPLICATION_LEVEL);

		oursim = new OurSimAPI(EventQueue.getInstance(), peers, jobScheduler, workload, availability);
		oursim.start();

		// um dos jobs não vai ser completado por indisponiblidade de máquina.
		int numberOfFinishedJobs = 4;
		int numberOfFinishedTasks = 4;
		assertEquals(numberOfFinishedJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(1, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numberOfFinishedTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(11, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

	}

}