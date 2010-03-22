package br.edu.ufcg.lsd.gridsim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import br.edu.ufcg.lsd.gridsim.WindowManager.Strategy;
import br.edu.ufcg.lsd.gridsim.events.FinishedJobEvent;
import br.edu.ufcg.lsd.gridsim.events.SubmitJobEvent;
import br.edu.ufcg.lsd.gridsim.events.TimeQueue;
import br.edu.ufcg.lsd.gridsim.input.GWAWorkload;
import br.edu.ufcg.lsd.gridsim.output.DatabaseOutput;
import br.edu.ufcg.lsd.gridsim.output.DefaultOutput;

public class Simulation {

    static int nodeSize = -1;
    static int peerNodeSize = -1;
    static String filenameSG = null;
    static String filenameOG = null;
    static String output = null;
    static boolean ogAsCluster;
    static boolean gLiteAsPeer;
    static boolean asGateway;
    static boolean useNoF;

	static Strategy strategy = Strategy.FCFS;
	private static boolean checkpoint;
	private static boolean replicas;
    
    public static void main(String[] args) throws Exception {

        int startArg = 0;

        if (args.length == 0) {
            System.out.println("Usage: checkpoint replicas useogAsCluster usegLiteAsPeer useGateway useNoF clusterNodeSize peerNodeSize filenameSGDB filenameOGDB outputDB");
            
            System.exit(1);
            return;
        }

        checkpoint = Boolean.parseBoolean(args[startArg]);
        startArg++;
        replicas = Boolean.parseBoolean(args[startArg]);
        startArg++;
        ogAsCluster = Boolean.parseBoolean(args[startArg]);
        startArg++;
        gLiteAsPeer = Boolean.parseBoolean(args[startArg]);
        startArg++;
        asGateway = Boolean.parseBoolean(args[startArg]);
        startArg++;
        useNoF = Boolean.parseBoolean(args[startArg]);
        startArg++;
        nodeSize = Integer.parseInt(args[startArg]);
        startArg++;
        peerNodeSize = Integer.parseInt(args[startArg]);
        startArg++;
        filenameSG = args[startArg];
        startArg++;
        filenameOG = args[startArg];
        startArg++;
        output = args[startArg];
        startArg++;

        Simulation.run();
        
        Configuration.getInstance().setCheckpoint(checkpoint);
        Configuration.getInstance().setReplication(replicas);
        Configuration.getInstance().setUseOGAsCluster(ogAsCluster);
        Configuration.getInstance().setUseGLiteAsPeer(gLiteAsPeer);
        Configuration.getInstance().setUseGateway(asGateway);
        Configuration.getInstance().setUseNoF(useNoF);
        
    }
    
    public static void execute(int nodeSize, int peerNodeSize,
    		String filenameSG, String filenameOG, String output) throws Exception {
        Simulation.nodeSize = nodeSize;
        Simulation.peerNodeSize = peerNodeSize;
        Simulation.filenameSG = filenameSG;
        Simulation.filenameOG = filenameOG;
        Simulation.output = output;
        Simulation.run();
    }
    
    public static void run() throws Exception {
    	
        GWAWorkload gLiteWorkload = new GWAWorkload(filenameSG, Job.SOURCE_GLITE);
        gLiteWorkload.setIntervalInclusive(0, Integer.MAX_VALUE, 0);
        HashSet<String> clusters = gLiteWorkload.getPeers(null);

        GWAWorkload ogWorkload = new GWAWorkload(filenameOG, Job.SOURCE_OG);
        ogWorkload.setIntervalInclusive(0, Integer.MAX_VALUE, 0);
        HashSet<String> peers = ogWorkload.getPeers("BOT", "SEQ");

        System.out.println("# Clusters: " + clusters.size());
        System.out.println("# Peers   : " + peers.size());

        gLiteWorkload.start();
        ogWorkload.start();

        TimeQueue tq = new TimeQueue();
        TreeSet<Job> submittedJobs = new TreeSet<Job>();

        SchedulerOurGrid og = null;

        if (Configuration.getInstance().useGateway()) {

            ArrayList<Peer> ps = new ArrayList<Peer>(peers.size());

            boolean first = true;
            for (String name : clusters) {
                if (first) {
                	WindowManager wm = new WindowManager(strategy, 64, tq);
                    first = false;
                } else {
                	WindowManager wm = new WindowManager(strategy, nodeSize, tq);
                }
            }

            for (String name : peers) {
                ps.add(new Peer(peerNodeSize, name));
            }

            og = new SchedulerOurGrid(tq, ps, submittedJobs);
        } else {
            og = prepareOG(peers, peerNodeSize, tq, submittedJobs);
        }

        DatabaseOutput dbOutput = new DatabaseOutput(output);
        DefaultOutput.configureInstance(dbOutput);

        //DefaultOutput.configureInstance(new PrintOutput());
        while (ogWorkload.peek() != null || gLiteWorkload.peek() != null || tq.peek() != null) {
            if (gLiteWorkload.peek() != null) {
                Job job = gLiteWorkload.poll();
                int lastTime = job.getSubmitTime();
                tq.addEvent(new SubmitJobEvent(lastTime, job));
            }
            if (ogWorkload.peek() != null) {
                Job job = ogWorkload.poll();
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
        System.out.println("# Total Jobs ~ O: " + FinishedJobEvent.o + " - S: " + FinishedJobEvent.s);
        dbOutput.close();

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
