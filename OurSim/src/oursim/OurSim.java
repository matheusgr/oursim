package oursim;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import oursim.entities.Peer;
import oursim.events.EventQueue;
import oursim.events.FinishJobEvent;
import oursim.events.FinishTaskEvent;
import oursim.input.SyntheticWorkload;
import oursim.input.Workload;
import oursim.policy.DefaultSharingPolicy;
import oursim.policy.NoFSharingPolicy;
import oursim.policy.OurGridScheduler;
import oursim.policy.ResourceSharingPolicy;

public class OurSim {

	private static List<Peer> prepareGrid(int numPeers, int numNodesByPeer,int nodeMIPSRating, boolean useNoF) {

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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return workload;

	}

	public static void main(String[] args) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// OutputManager.getInstance().addListener(new
		// PrintOutput("oursim_trace.txt"));
		// OutputManager.getInstance().addListener(new PrintOutput());

		List<Peer> peers = prepareGrid(Parameters.NUM_PEERS, Parameters.NUM_RESOURCES_BY_PEER,Parameters.NODE_MIPS_RATING, Parameters.USE_NOF);
		Workload workload = prepareWorkload(peers);

		System.out.println("Starting Simulation...");

		OurSimAPI.run(peers, workload);

		System.out.println("# Total of  finished  jobs: " + FinishJobEvent.amountOfFinishedJobs);
		System.out.println("# Total of preempted  jobs: " + OurGridScheduler.numberOfPreemptionsForAllJobs);
		System.out.println("# Total of  finished tasks: " + FinishTaskEvent.amountOfFinishedTasks);
		System.out.println("# Total of preempted tasks: " + OurGridScheduler.numberOfPreemptionsForAllTasks);
		System.out.println("# Total of          events: " + EventQueue.amountOfEvents);

		stopWatch.stop();
		System.out.println(stopWatch);

	}

	/*
	
	Starting Simulation...
	# Total of  finished  jobs: 100000
	# Total of preempted  jobs: 0
	# Total of  finished tasks: 200000
	# Total of preempted tasks: 0
	# Total of          events: 600000
	4:38:11.195
	
	*/
}
