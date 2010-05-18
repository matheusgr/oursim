package oursim;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import oursim.availability.AvailabilityRecord;
import oursim.dispatchableevents.jobevents.JobEventCounter;
import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.dispatchableevents.taskevents.TaskEventCounter;
import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.entities.Peer;
import oursim.input.AvailabilityCharacterization;
import oursim.input.Input;
import oursim.input.SyntheticWorkload;
import oursim.input.Workload;
import oursim.policy.DefaultSharingPolicy;
import oursim.policy.NoFSharingPolicy;
import oursim.policy.ResourceSharingPolicy;
import oursim.simulationevents.EventQueue;

public class OurSim {

	private static List<Peer> prepareGrid(int numPeers, int numNodesByPeer, int nodeMIPSRating, boolean useNoF) {

		ArrayList<Peer> peers = new ArrayList<Peer>(numPeers);

		ResourceSharingPolicy sharingPolicy = useNoF ? NoFSharingPolicy.getInstance() : DefaultSharingPolicy.getInstance();

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

		try {
			workload.save("workload_oursim.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return workload;

	}

	public static void main(String[] args) throws FileNotFoundException {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// OutputManager.getInstance().addListener(new
		// PrintOutput("oursim_trace.txt"));
		// OutputManager.getInstance().addListener(new PrintOutput());

		JobEventCounter jobEventCounter = new JobEventCounter();
		JobEventDispatcher.getInstance().addListener(jobEventCounter);

		TaskEventCounter taskEventCounter = new TaskEventCounter();
		TaskEventDispatcher.getInstance().addListener(taskEventCounter);

		List<Peer> peers = prepareGrid(Parameters.NUM_PEERS, Parameters.NUM_RESOURCES_BY_PEER, Parameters.NODE_MIPS_RATING, Parameters.USE_NOF);
		Workload workload = prepareWorkload(peers);
		Input<AvailabilityRecord> availability = new AvailabilityCharacterization("trace_mutka_100-machines_10-hours.txt");

		System.out.println("Starting Simulation...");

		new OurSimAPI().run(peers, workload, availability);

		System.out.println("# Total of  finished  jobs: " + jobEventCounter.getNumberOfFinishedJobs());
		System.out.println("# Total of preempted  jobs: " + jobEventCounter.getNumberOfPreemptionsForAllJobs());
		System.out.println("# Total of  finished tasks: " + taskEventCounter.getNumberOfFinishedTasks());
		System.out.println("# Total of preempted tasks: " + taskEventCounter.getNumberOfPreemptionsForAllTasks());
		System.out.println("# Total of          events: " + EventQueue.totalNumberOfEvents);

		stopWatch.stop();
		System.out.println(stopWatch);

	}

}
