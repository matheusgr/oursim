package br.edu.ufcg.lsd.gridsim;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import br.edu.ufcg.lsd.gridsim.events.TimeQueue;
import br.edu.ufcg.lsd.gridsim.schedulers.NoFScheduler;

public class SchedulerOurGrid {

    private TreeSet<Job> submittedJobs;
    private NoFScheduler scheduler;
    private List<Peer> peers;
    private TimeQueue timeQueue;
	private HashMap<String, Peer> peersMap;
    
    public SchedulerOurGrid(TimeQueue timeQueue, List<Peer> peers, TreeSet<Job> submittedJobs) {
    	this.peers = peers;
        this.timeQueue = timeQueue;
        this.scheduler = new NoFScheduler();
        this.submittedJobs = submittedJobs;
		this.peersMap = new HashMap<String, Peer>();
		for (Peer p : this.peers) {
			peersMap.put(p.toString(), p);
		}
		for (Peer p : this.peers) {
			p.setPeersMap(peersMap);
		}
    }
    
    public double getUtilization() {
        double result = 0;
        for (Peer p : peers) {
            result += p.getUtilization();
        }
        return result;
    }

    public void schedule() {
        scheduler.schedule(timeQueue, peers, this.submittedJobs, peersMap);
    }

    public void finishJob(Job job) {
    	job.getPeer().finishOportunisticJob(job, false);
    }

}
