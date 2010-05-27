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
import oursim.policy.OurGridPersistentScheduler;
import oursim.policy.ResourceSharingPolicy;
import oursim.simulationevents.EventQueue;

public class OurSimAPIVolatilityTest extends AbstractOurSimAPITest {

	/**
	 * Cenário de Teste: Recursos voláteis ao longo da simulação.
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
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);

				// aos 32 segundos (devido à volatidade) todos os jobs já terão
				// acabado. daí começa a
				// segunda rodada de submissões

				addJob(nextJobId++, 32, 1, peer, this.inputs, jobs);// 5
				addJob(nextJobId++, 32, 2, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);// 8
				addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 20, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 10, peer, this.inputs, jobs);// 11
				addJob(nextJobId++, 32, 12, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 15, peer, this.inputs, jobs);
			}

		};

		JobSchedulerPolicy jobScheduler = new OurGridPersistentScheduler(peers);

		oursim = new OurSimAPI(EventQueue.getInstance(), peers, jobScheduler, workload, availability);
		oursim.start();

		// um dos jobs não vai ser completado por indisponiblidade de máquina.
		int numberOfFinishedJobs = jobs.size() - 1;
		int numberOfFinishedTasks = numberOfFinishedJobs;
		assertEquals(numberOfFinishedJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(11, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numberOfFinishedTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(11, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

	}

}