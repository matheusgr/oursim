package oursim.policy;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.FinishedJobEvent;
import oursim.events.SubmitJobEvent;
import oursim.events.TimeQueue;
import br.edu.ufcg.lsd.gridsim.Configuration;

public class OurGridScheduler implements SchedulerPolicy {

    private TreeSet<Job> submittedJobs;
    private List<Peer> peers;
    private TimeQueue timeQueue;
    private HashMap<String, Peer> peersMap;

    public OurGridScheduler(TimeQueue timeQueue, List<Peer> peers, TreeSet<Job> submittedJobs) {
	this.peers = peers;
	this.timeQueue = timeQueue;
	this.submittedJobs = submittedJobs;
	this.peersMap = new HashMap<String, Peer>();
	for (Peer p : this.peers) {
	    peersMap.put(p.toString(), p);
	}
    }

    public double getUtilization() {
	double result = 0;
	for (Peer p : peers) {
	    result += p.getUtilization();
	}
	return result;
    }

    public void queueFinishJob(Job job) {
	long wastedTime = job.getStartTime() + job.getRunTimeDuration();
	FinishedJobEvent finishedJobEvent = new FinishedJobEvent(wastedTime, job);
	job.setFinishedJobEvent(finishedJobEvent);
	job.finishJob(wastedTime);
	timeQueue.addEvent(finishedJobEvent);
    }    
    
    public void schedule(Job job) {
	timeQueue.addEvent(new SubmitJobEvent(timeQueue.currentTime(), job));
    }

    public void addJob(Job job) {
	this.submittedJobs.add(job);
    }
    
    public void finishJob(Job job) {
	throw new UnsupportedOperationException("Operaçao ainda não implementada.");
    }

}
