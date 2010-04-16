package br.edu.ufcg.lsd.gridsim;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import oursim.Parameters;
import br.edu.ufcg.lsd.gridsim.events.FinishedJobEvent;
import br.edu.ufcg.lsd.gridsim.events.SubmitJobEvent;
import br.edu.ufcg.lsd.gridsim.events.TimeQueue;
import br.edu.ufcg.lsd.gridsim.input.SyntheticWorkload;
import br.edu.ufcg.lsd.gridsim.input.Workload;

public class Simulation {

    static int peerNodeSize;
    static boolean useNoF;

    private static boolean checkpoint = true;
    private static boolean replicas = false;

    public static void main(String[] args) throws Exception {

	int startArg = 0;

	if (args.length == 0) {
	    // Default
	} else {
	    checkpoint = Boolean.parseBoolean(args[startArg]);
	    startArg++;
	    replicas = Boolean.parseBoolean(args[startArg]);
	    startArg++;
	    useNoF = Boolean.parseBoolean(args[startArg]);
	    startArg++;
	    peerNodeSize = Integer.parseInt(args[startArg]);
	    startArg++;
	}

	Configuration.getInstance().setCheckpoint(checkpoint);
	Configuration.getInstance().setReplication(replicas);
	Configuration.getInstance().setUseNoF(useNoF);

	Simulation.run();

    }

    public static void run() throws Exception {

//	int execTime = 100;
//	int execTimeVar = 50;
//	int submissionInterval = 1;
//	int numJobs = 100000;
//	int numberOfPeers = 100;
//	peerNodeSize = 10;

	int execTime = 10;//tempo base de execução do job
	int execTimeVar = 50;//variância máxima do tempo base de execução (sempre positiva)
	
	int submissionInterval = 1;//intervalo de submissão entre jobs subsequentes
	int numJobs = 100;//quantidade total de jobs do workload
	int numberOfPeers = 2;//Vai ser usado para atribuir a origem dos jobs
	peerNodeSize = 4;

	int c=0;
	
	execTime = Integer.parseInt(Parameters.args[c++]);
	execTimeVar = Integer.parseInt(Parameters.args[c++]);
	submissionInterval = Integer.parseInt(Parameters.args[c++]);
	numJobs = Integer.parseInt(Parameters.args[c++]);
	numberOfPeers = Integer.parseInt(Parameters.args[c++]);
	peerNodeSize = Integer.parseInt(Parameters.args[c++]);
	
	ArrayList<String> peers = new ArrayList<String>();
	for (int i = 0; i < numberOfPeers; i++) {
	    peers.add("P" + i);
	}

	Workload workload = new SyntheticWorkload(execTime, execTimeVar, submissionInterval, numJobs, peers);

	workload.close();
	
	System.out.println("# Peers   : " + peers.size());
	TimeQueue tq = new TimeQueue();
	TreeSet<Job> submittedJobs = new TreeSet<Job>();

	SchedulerOurGrid og = prepareOG(peers, peerNodeSize, tq, submittedJobs);

	GlobalScheduler.prepareGlobalScheduler(tq, og, submittedJobs);

	// DefaultOutput.configureInstance(new PrintOutput());
	while (workload.peek() != null || tq.peek() != null) {
	    if (workload.peek() != null) {
		Job job = workload.poll();
		int lastTime = job.getSubmitTime();
		tq.addEvent(new SubmitJobEvent(lastTime, job));
	    }
	    if (tq.peek() != null) {
		int time = tq.peek().getTime();
		if (tq.peek().getClass().isInstance(SubmitJobEvent.class)) {
		    GlobalScheduler.getInstance().scheduleNow();
		} else {
		    while (tq.peek() != null && tq.peek().getTime() == time) {
			tq.poll().action();
		    }
		    GlobalScheduler.getInstance().scheduleNow();
		}

	    }
	}
	System.out.println("# Total Jobs ~ O: " + FinishedJobEvent.o);
	System.out.println("# Preemptions: " + Job.globalPreemptions);
    }

    private static SchedulerOurGrid prepareOG(List<String> peersNames, int peerNodeSize, TimeQueue tq, TreeSet<Job> submittedJobs) {
	ArrayList<Peer> peers = new ArrayList<Peer>(peersNames.size());

	for (String name : peersNames) {
	    peers.add(new Peer(peerNodeSize, name));
	}

	return new SchedulerOurGrid(tq, peers, submittedJobs);
    }

}
