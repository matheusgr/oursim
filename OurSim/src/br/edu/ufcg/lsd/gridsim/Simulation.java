package br.edu.ufcg.lsd.gridsim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

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
    	
        int execTime = 100;
		int execTimeVar = 50;
		int submissionInterval = 1;
		int numJobs = 500;
		int numberOfPeers = 10;
		peerNodeSize = 10;
		
		HashSet<String> peers = new HashSet<String>();
		for (int i = 0; i < numberOfPeers; i++) {
			peers.add("P" + i);
		}
		
		Workload workload = new SyntheticWorkload(execTime, execTimeVar, submissionInterval, numJobs, peers);

        System.out.println("# Peers   : " + peers.size());
        TimeQueue tq = new TimeQueue();
        TreeSet<Job> submittedJobs = new TreeSet<Job>();

        SchedulerOurGrid og = null;

        og = prepareOG(peers, peerNodeSize, tq, submittedJobs);

        GlobalScheduler.prepareGlobalScheduler(tq, og, submittedJobs);
        
        //DefaultOutput.configureInstance(new PrintOutput());
        while (workload.peek() != null  || tq.peek() != null) {
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

    }

    private static SchedulerOurGrid prepareOG(HashSet<String> peersNames, int peerNodeSize,
            TimeQueue tq, TreeSet<Job> submittedJobs) {
        ArrayList<Peer> peers = new ArrayList<Peer>(peersNames.size());

        for (String name : peersNames) {
            peers.add(new Peer(peerNodeSize, name));
        }

        return new SchedulerOurGrid(tq, peers, submittedJobs);
    }

}
