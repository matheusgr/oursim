package br.edu.ufcg.lsd.oursim;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import br.edu.ufcg.lsd.oursim.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.AvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.SyntheticWorkload;
import br.edu.ufcg.lsd.oursim.io.input.Workload;
import br.edu.ufcg.lsd.oursim.io.output.PrintOutput;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.policy.NoFSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.OurGridScheduler;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

public class OurSim {

	private static List<Peer> prepareGrid(int numPeers, int numNodesByPeer, int nodeMIPSRating, boolean useNoF) {

		ArrayList<Peer> peers = new ArrayList<Peer>(numPeers);

		ResourceSharingPolicy sharingPolicy = useNoF ? NoFSharingPolicy.getInstance() : ResourceSharingPolicy.DEFAULT_SHARING_POLICY;

		for (int i = 0; i < numPeers; i++) {
			peers.add(new Peer("P" + i, numNodesByPeer, nodeMIPSRating, sharingPolicy));
		}

		return peers;

	}

	/**
	 * @param peers
	 *            Vai ser usado para atribuir a origem dos jobs
	 * @return
	 */
	private static Workload prepareWorkload(List<Peer> peers) {

		int execTime = Parameters.EXEC_TIME;
		int execTimeVariance = Parameters.EXEC_TIME_VAR;
		int submissionInterval = Parameters.SUBMISSION_INTERVAL;
		int numJobs = Parameters.NUM_JOBS;
		int numTasksByJobs = Parameters.NUM_TASKS_BY_JOB;

		SyntheticWorkload workload = new SyntheticWorkload(execTime, execTimeVariance, submissionInterval, numJobs, numTasksByJobs, peers);

		return workload;

	}

	public static void main(String[] args) throws FileNotFoundException {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// OutputManager.getInstance().addListener(new
		// PrintOutput("oursim_trace.txt"));
		JobEventDispatcher.getInstance().addListener(new PrintOutput());

		JobEventCounter jobEventCounter = new JobEventCounter();
		JobEventDispatcher.getInstance().addListener(jobEventCounter);

		TaskEventCounter taskEventCounter = new TaskEventCounter();
		TaskEventDispatcher.getInstance().addListener(taskEventCounter);

		List<Peer> peers = prepareGrid(Parameters.NUM_PEERS, Parameters.NUM_RESOURCES_BY_PEER, Parameters.NODE_MIPS_RATING, Parameters.USE_NOF);
		Workload workload = prepareWorkload(peers);

		Input<AvailabilityRecord> availability = Parameters.DEDICATED_RESOURCES ? new DedicatedResourcesAvailabilityCharacterization(peers)
				: new AvailabilityCharacterization(Parameters.AVAILABILITY_CHARACTERIZATION_FILE_PATH);

		System.out.println("Starting Simulation...");

		JobSchedulerPolicy jobScheduler = new OurGridScheduler(peers);

		new OurSimAPI(EventQueue.getInstance(), peers, jobScheduler, workload, availability).start();

		System.out.println("# Total of  finished  jobs: " + jobEventCounter.getNumberOfFinishedJobs());
		System.out.println("# Total of preempted  jobs: " + jobEventCounter.getNumberOfPreemptionsForAllJobs());
		System.out.println("# Total of  finished tasks: " + taskEventCounter.getNumberOfFinishedTasks());
		System.out.println("# Total of preempted tasks: " + taskEventCounter.getNumberOfPreemptionsForAllTasks());
		System.out.println("# Total of          events: " + EventQueue.totalNumberOfEvents);

		stopWatch.stop();
		System.out.println(stopWatch);

	}

}
