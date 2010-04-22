package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.EventQueue;
import oursim.jobevents.JobEvent;
import oursim.jobevents.JobEventListenerAdapter;

public class OurGridScheduler extends JobEventListenerAdapter implements JobSchedulerPolicy {

    private EventQueue eventQueue;

    private TreeSet<Job> submittedJobs;

    private List<Peer> peers;
    private HashMap<String, Peer> peersMap;

    private ResourceRequestPolicy resourceRequestPolicy;

    public OurGridScheduler(EventQueue eventQueue, List<Peer> peers) {
	this(eventQueue, peers, new TreeSet<Job>());
    }

    public OurGridScheduler(EventQueue eventQueue, List<Peer> peers, TreeSet<Job> submittedJobs) {
	this.peers = peers;
	this.eventQueue = eventQueue;
	this.submittedJobs = submittedJobs;
	this.peersMap = new HashMap<String, Peer>();

	for (Peer p : this.peers) {
	    peersMap.put(p.getName(), p);
	}

	this.resourceRequestPolicy = new ResourceRequestPolicy();

    }

    @Override
    public void rescheduleJob(Job job) {
	eventQueue.addSubmitJobEvent(eventQueue.currentTime(), job);
    }

    @Override
    public void addJob(Job job) {
	this.submittedJobs.add(job);
    }

    @Override
    public void finishJob(Job job) {
	throw new RuntimeException("Método ainda não implementado.");
	// job.getTargetPeer().finishJob(job, false);
    }

    @Override
    public void scheduleJobs() {

	HashMap<Peer, HashSet<Peer>> triedPeers = new HashMap<Peer, HashSet<Peer>>(peersMap.size());

	int originalSize = submittedJobs.size();

	Iterator<Job> it = submittedJobs.iterator();
	for (int i = 0; i < originalSize; i++) {

	    Job job = it.next();
	    Peer consumer = job.getSourcePeer();

	    // efeito colateral: reordena os peers
	    resourceRequestPolicy.request(consumer, peers);

	    for (Peer provider : peers) {

		HashSet<Peer> providersTried = triedPeers.get(consumer);

		if (providersTried != null && providersTried.contains(provider)) {
		    continue;
		}

		boolean isJobRunning = provider.addJob(job, consumer);

		if (isJobRunning) {
		    updateJobState(job, provider, eventQueue.currentTime());
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

    private void updateJobState(Job job, Peer provider, long startTime) {
	job.setStartTime(startTime);
	job.setTargetPeer(provider);
	eventQueue.addStartedJobEvent(job);
    }

    @Override
    public void jobFinished(JobEvent jobEvent) {
	Job job = (Job) jobEvent.getSource();
	job.getTargetPeer().finishJob(job, false);
    }

    @Override
    public void jobSubmitted(JobEvent jobEvent) {
	Job job = (Job) jobEvent.getSource();
	this.addJob(job);
    }

    @Override
    public void jobPreempted(JobEvent jobEvent) {
	Job job = (Job) jobEvent.getSource();
	this.rescheduleJob(job);
    }

}
