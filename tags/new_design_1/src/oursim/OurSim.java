package oursim;

import java.util.ArrayList;
import java.util.List;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.FinishJobEvent;
import oursim.events.SubmitJobEvent;
import oursim.events.TimeQueue;
import oursim.input.SyntheticWorkload;
import oursim.input.Workload;
import oursim.policy.NoFSharingPolicy;
import oursim.policy.OurGridScheduler;
import oursim.policy.SchedulerPolicy;

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

	// quantidade total de jobs do workload
	int numJobs = 100;

	// tempo base de execução do job
	int execTime = 10;

	// variância máxima do tempo base de execução (sempre positiva)
	int execTimeVariance = 50;

	// intervalo de submissão entre jobs subsequentes
	int submissionInterval = 1;
	
	int i=0;
	
	execTime = Integer.parseInt(Parameters.args[i++]);
	execTimeVariance = Integer.parseInt(Parameters.args[i++]);
	submissionInterval = Integer.parseInt(Parameters.args[i++]);
	numJobs = Integer.parseInt(Parameters.args[i++]);
	
	Workload workload = new SyntheticWorkload(execTime, execTimeVariance, submissionInterval, numJobs, peers);

	return workload;

    }

    public static void run(Workload workload, List<Peer> peers) {
	TimeQueue timeQueue = new TimeQueue();

	SchedulerPolicy og = new OurGridScheduler(timeQueue, peers);

	while (workload.peek() != null || timeQueue.peek() != null) {
	    if (workload.peek() != null) {
		Job job = workload.poll();
		long lastTime = job.getSubmissionTime();
		timeQueue.addEvent(new SubmitJobEvent(lastTime, job, og));
	    }
	    if (timeQueue.peek() != null) {
		long time = timeQueue.peek().getTime();
		if (timeQueue.peek().getClass().isInstance(SubmitJobEvent.class)) {
		    og.scheduleNow();
		} else {
		    //TODO: Mas não pode haver um SubmitJobEvent nesse meio?
		    while (timeQueue.peek() != null && timeQueue.peek().getTime() == time) {
			timeQueue.poll().action();
		    }
		    og.scheduleNow();
		}
	    }
	}
	
	System.out.println("# Total Jobs ~ O: " + FinishJobEvent.amountOfFinishedJobs);
	System.out.println("# Preemptions: " + Job.numberOfPreemptionsForAllJobs);

    }

    public static void main(String[] args) {

	int i=4;
	
	int numOfPeers = Integer.parseInt(Parameters.args[i++]);
	int numOfNodesByPeer = Integer.parseInt(Parameters.args[i++]);

	List<Peer> peers = prepareGrid(numOfPeers, numOfNodesByPeer);

	Workload workload = prepareWorkload(peers);

	workload.close();
	
	run(workload, peers);

    }

}
