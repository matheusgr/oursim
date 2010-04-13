package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.FinishJobEvent;
import oursim.events.SubmitJobEvent;
import oursim.events.TimeQueue;
import oursim.output.DefaultOutput;

public class OurGridScheduler implements SchedulerPolicy {

    private TimeQueue timeQueue;

    private TreeSet<Job> submittedJobs;

    private List<Peer> peers;
    private HashMap<String, Peer> peersMap;

    private RequestPolicy requestPolicy;

    public OurGridScheduler(TimeQueue timeQueue, List<Peer> peers) {
	this(timeQueue, peers, new TreeSet<Job>());
    }

    public OurGridScheduler(TimeQueue timeQueue, List<Peer> peers, TreeSet<Job> submittedJobs) {
	this.peers = peers;
	this.timeQueue = timeQueue;
	this.submittedJobs = submittedJobs;
	this.peersMap = new HashMap<String, Peer>();

	for (Peer p : this.peers) {
	    peersMap.put(p.getName(), p);
	}

	this.requestPolicy = new RequestPolicy();

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
	FinishJobEvent finishJobEvent = new FinishJobEvent(wastedTime, job, this);
	job.setFinishedJobEvent(finishJobEvent);
	job.finishJob(wastedTime);
	timeQueue.addEvent(finishJobEvent);
    }

    public void schedule(Job job) {
	timeQueue.addEvent(new SubmitJobEvent(timeQueue.currentTime(), job, this));
    }

    public void addJob(Job job) {
	this.submittedJobs.add(job);
    }

    public void finishJob(Job job) {
	job.getTargetPeer().finishJob(job, false);
    }

    public void scheduleNow() {
	HashMap<Peer, HashSet<Peer>> triedPeers = new HashMap<Peer, HashSet<Peer>>(peersMap.size());

	int originalSize = submittedJobs.size();

	Iterator<Job> it = submittedJobs.iterator();
	for (int i = 0; i < originalSize; i++) {

	    Job job = it.next();
	    Peer consumer = job.getSourcePeer();

	    requestPolicy.request(peers);
	    
	    for (Peer provider : peers) {
		HashSet<Peer> providersTried = triedPeers.get(consumer);
		if (providersTried != null && providersTried.contains(provider)) {
		    continue;
		}
		boolean runJob = provider.addJob(job, consumer, timeQueue.currentTime());
		if (runJob) {
		    DefaultOutput.getInstance().startJob(timeQueue.currentTime(), job);
		    job.setStartTime(timeQueue.currentTime());
		    job.setTargetPeer(provider);
		    long finishTime = job.getStartTime() + job.getRunTimeDuration();
		    assert finishTime > timeQueue.currentTime();
		    FinishJobEvent finishJobEvent = new FinishJobEvent(finishTime, job, this);
		    timeQueue.addEvent(finishJobEvent);
		    job.setFinishedJobEvent(finishJobEvent);
		    it.remove();
		    break;
		} else {
		    if (providersTried == null) {
			providersTried = new HashSet<Peer>(peers.size());
			triedPeers.put(consumer, providersTried);
		    }
		    providersTried.add(provider);
		}
	    }
	}

    }

}
