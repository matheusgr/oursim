package br.edu.ufcg.lsd.oursim.policy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.AbstractOurSimAPITest;
import br.edu.ufcg.lsd.oursim.OurSimAPI;
import br.edu.ufcg.lsd.oursim.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.input.Input;
import br.edu.ufcg.lsd.oursim.input.InputAbstract;
import br.edu.ufcg.lsd.oursim.input.Workload;
import br.edu.ufcg.lsd.oursim.input.WorkloadAbstract;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.policy.OurGridReplicationScheduler;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;


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

		JobSchedulerPolicy jobScheduler = new OurGridReplicationScheduler(peers, REPLICATION_LEVEL);

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