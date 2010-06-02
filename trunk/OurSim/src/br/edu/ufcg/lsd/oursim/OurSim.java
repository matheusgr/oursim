package br.edu.ufcg.lsd.oursim;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	public static final boolean LOG = false;

	public static final Random RANDOM = new Random(9354269l);

	static final String AVAILABILITY_CHARACTERIZATION_FILE_PATH = "trace_mutka_100-machines_10-hours.txt";

	static final boolean USE_NOF = false;

	static final boolean DEDICATED_RESOURCES = true;

	static final int NUMBER_OF_REPLIES = 3;

	private static final String ARGS_STRING =
	// execTime execTimeVar subInterval #Jobs #TasksByJob #Peers #nodesByPeer
	// nodeMIPSRating
	"       100         50            5    1000           50     10 		  100           3000";

	private static final String[] ARGS = ARGS_STRING.trim().split("\\s+");

	private static int ARGS_INDEX = 0;

	// tempo base de execução do job
	static int EXEC_TIME = Integer.parseInt(ARGS[ARGS_INDEX++]);

	// variância máxima do tempo base de execução (sempre positiva)
	static int EXEC_TIME_VAR = Integer.parseInt(ARGS[ARGS_INDEX++]);

	// intervalo de submissão entre jobs subsequentes
	static int SUBMISSION_INTERVAL = Integer.parseInt(ARGS[ARGS_INDEX++]);

	// quantidade total de jobs do workload
	static int NUM_JOBS = Integer.parseInt(ARGS[ARGS_INDEX++]);

	static int NUM_TASKS_BY_JOB = Integer.parseInt(ARGS[ARGS_INDEX++]);

	static int NUM_PEERS = Integer.parseInt(ARGS[ARGS_INDEX++]);

	// número de nodos do peer
	static int NUM_RESOURCES_BY_PEER = Integer.parseInt(ARGS[ARGS_INDEX++]);

	static int NODE_MIPS_RATING = Integer.parseInt(ARGS[ARGS_INDEX++]);

	private OurSim() {
	}

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

		int execTime = OurSim.EXEC_TIME;
		int execTimeVariance = OurSim.EXEC_TIME_VAR;
		int submissionInterval = OurSim.SUBMISSION_INTERVAL;
		int numJobs = OurSim.NUM_JOBS;
		int numTasksByJobs = OurSim.NUM_TASKS_BY_JOB;

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

		List<Peer> peers = prepareGrid(OurSim.NUM_PEERS, OurSim.NUM_RESOURCES_BY_PEER, OurSim.NODE_MIPS_RATING, OurSim.USE_NOF);
		Workload workload = prepareWorkload(peers);

		Input<AvailabilityRecord> availability = OurSim.DEDICATED_RESOURCES ? new DedicatedResourcesAvailabilityCharacterization(peers)
				: new AvailabilityCharacterization(OurSim.AVAILABILITY_CHARACTERIZATION_FILE_PATH);

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
