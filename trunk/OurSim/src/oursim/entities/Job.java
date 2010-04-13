package oursim.entities;

import oursim.events.TimedEvent;

public class Job {

    private long id;

    private long submissionTime;
    private long startTime;
    private long runTimeDuration;
    private int wastedTime;

    private Peer sourcePeer;
    private Peer targetPeer;

    private int preemptions;
    static int globalPreemptions = 0;
    private TimedEvent finishJobEvent;

    public Job(long id, int submissionTime, int runTimeDuration, Peer sourcePeer) {

	this.id = id;
	this.submissionTime = submissionTime;
	this.runTimeDuration = runTimeDuration;
	this.sourcePeer = sourcePeer;

    }

    public long getSubmissionTime() {
	return submissionTime;
    }

    public long getId() {
	return id;
    }

    public void finishJob(long time) {
	throw new UnsupportedOperationException("Operaçao ainda não implementada.");
    }

    public long getRunTimeDuration() {
	return runTimeDuration;
    }

    public long getStartTime() {
	return startTime;
    }

    public Peer getSourcePeer() {
	return sourcePeer;
    }

    public Peer getTargetPeer() {
	return targetPeer;
    }

    public void setTargetPeer(Peer targetPeer) {
	this.targetPeer = targetPeer;
    }

    public void preempt(int time) {
	assert this.startTime != -1;
	this.preemptions += 1;
	globalPreemptions += 1;
	this.wastedTime += (time - this.startTime);
	this.startTime = -1;
	this.finishJobEvent.cancel();
    }

    public void setFinishedJobEvent(TimedEvent finishJobEvent) {
	this.finishJobEvent = finishJobEvent;
    }

}
