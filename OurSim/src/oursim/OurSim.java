package oursim;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.EventQueue;
import oursim.events.FinishJobEvent;
import oursim.input.SyntheticWorkload;
import oursim.input.Workload;
import oursim.output.OutputManager;
import oursim.output.PrintOutput;
import oursim.policy.JobSchedulerPolicy;
import oursim.policy.NoFSharingPolicy;
import oursim.policy.OurGridScheduler;

public class OurSim {

    private static List<Peer> prepareGrid(int numPeers, int numNodesByPeer) {

	ArrayList<Peer> peers = new ArrayList<Peer>(numPeers);

	NoFSharingPolicy sharingPolicy = NoFSharingPolicy.getInstance();

	for (int i = 0; i < numPeers; i++) {
	    peers.add(new Peer("P" + i, numNodesByPeer, sharingPolicy));
	}

	return peers;

    }

    /**
     * @param peers
     *                Vai ser usado para atribuir a origem dos jobs
     * @return
     */
    private static Workload prepareWorkload(List<Peer> peers) {

	int execTime = Parameters.EXEC_TIME;
	int execTimeVariance = Parameters.EXEC_TIME_VAR;
	int submissionInterval = Parameters.SUBMISSION_INTERVAL;
	int numJobs = Parameters.NUM_JOBS;

	Workload workload = new SyntheticWorkload(execTime, execTimeVariance, submissionInterval, numJobs, peers);

	return workload;

    }

    private static void scheduleEvents(EventQueue eq, Workload workload, JobSchedulerPolicy sp) {

	while (workload.peek() != null) {
	    Job job = workload.poll();
	    long time = job.getSubmissionTime();
	    eq.addSubmitJobEvent(time, job, sp);
	}

    }

    public static void run(EventQueue eq, JobSchedulerPolicy sp) {

	while (eq.peek() != null) {
	    long time = eq.peek().getTime();
	    while (eq.peek() != null && eq.peek().getTime() == time) {
		eq.poll().action();
	    }
	    sp.scheduleJobs();
	}

    }

    public static void main(String[] args) {

	StopWatch stopWatch = new StopWatch();
	stopWatch.start();

//	OutputManager.getInstance().addListener(new PrintOutput("oursim_trace.txt"));
//	OutputManager.getInstance().addListener(new PrintOutput());

	List<Peer> peers = prepareGrid(Parameters.NUM_PEERS, Parameters.PEER_SIZE);

	Workload workload = prepareWorkload(peers);

	EventQueue eq = new EventQueue();

	JobSchedulerPolicy sp = new OurGridScheduler(eq, peers);

	scheduleEvents(eq, workload, sp);

	run(eq, sp);

	System.out.println("# Total of  finished jobs: " + FinishJobEvent.amountOfFinishedJobs);
	System.out.println("# Total of preempted jobs: " + Job.numberOfPreemptionsForAllJobs);

	stopWatch.stop();
	System.out.println(stopWatch);

    }

}
