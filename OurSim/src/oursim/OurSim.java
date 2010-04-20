package oursim;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.EventQueue;
import oursim.events.FinishJobEvent;
import oursim.input.SyntheticWorkload;
import oursim.input.Workload;
import oursim.policy.NoFSharingPolicy;
import oursim.policy.OurGridScheduler;
import oursim.policy.JobSchedulerPolicy;

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

	int i = 0;

	int execTime = Integer.parseInt(Parameters.args[i++]);
	int execTimeVariance = Integer.parseInt(Parameters.args[i++]);
	int submissionInterval = Integer.parseInt(Parameters.args[i++]);
	int numJobs = Integer.parseInt(Parameters.args[i++]);

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

	int i = 4;

	int numOfPeers = Integer.parseInt(Parameters.args[i++]);
	int numOfNodesByPeer = Integer.parseInt(Parameters.args[i++]);

	List<Peer> peers = prepareGrid(numOfPeers, numOfNodesByPeer);

	Workload workload = prepareWorkload(peers);

	EventQueue eq = new EventQueue();

	JobSchedulerPolicy sp = new OurGridScheduler(eq, peers);

	scheduleEvents(eq, workload, sp);

	run(eq, sp);

	System.out.println("# Total of  finished jobs: " + FinishJobEvent.amountOfFinishedJobs);
	System.out.println("# Total of preempted jobs: " + Job.numberOfPreemptionsForAllJobs);

	try {
	    PrintStream out = new PrintStream("finished_jobs.txt");
	    out.print("id peer runtime submittime makespan \n");
	    for (Job job : FinishJobEvent.finishedJobs) {
		out.printf("%s %s %s %s %s\n", job.getId(), job.getSourcePeer().getName(), job.getRunTimeDuration(), job.getSubmissionTime(), job.getFinishTime()-job.getSubmissionTime());
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	stopWatch.stop();

	System.out.println(stopWatch);

    }

    /*
     * # Total Jobs ~ O: 100000 # Preemptions: 2043 0:00:14.353 # Total Jobs ~
     * O: 100000 # Preemptions: 2057 0:00:14.168 # Total Jobs ~ O: 100000 #
     * Preemptions: 2181 # Total Jobs ~ O: 10000 # Preemptions: 214 0:00:02.022 #
     * Total Jobs ~ O: 10000 # Preemptions: 235
     */

    // TODO: Mas não pode haver um SubmitJobEvent nesse meio?
    // Não, pois se tivesse ele teria sido ordenado primeiro e já
    // teria sido pego acima.
    // O tchan da história está em [ timeQueue.peek().getTime() ==
    // time ]
}
