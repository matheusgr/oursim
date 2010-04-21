package oursim.entities;

public class Job implements Comparable<Job> {

    private final long id;

    private final long submissionTime;
    private long startTime;
    private long finishTime;
    private long runTimeDuration;
    private int wastedTime;

    private final Peer sourcePeer;
    private Peer targetPeer;

    private int numberOfpreemptions;
    public static int numberOfPreemptionsForAllJobs = 0;

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
	this.finishTime = time;
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

    public void preempt(long time) {
	assert this.startTime != -1;
	this.numberOfpreemptions++;
	numberOfPreemptionsForAllJobs++;
	this.wastedTime += (time - this.startTime);
	this.startTime = -1;
	this.setTargetPeer(null);
    }

    public void setStartTime(long startTime) {
	this.startTime = startTime;
    }

    public int getWastedTime() {
	return wastedTime;
    }

    public long getWaitedTime() {
	return this.finishTime - (this.submissionTime + this.runTimeDuration);
    }

    public long getEstimatedFinishTime() {
	return finishTime = this.getStartTime() + this.getRunTimeDuration();
    }

    public long getFinishTime() {
	return finishTime;
    }

    public int getNumberOfpreemptions() {
	return numberOfpreemptions;
    }

    @Override
    public int compareTo(Job o) {
	long diffTime = this.submissionTime - o.getSubmissionTime();
	if (diffTime == 0) {
	    if (id > o.getId()) {
		return 2;
	    } else if (id == o.getId()) {
		return this.hashCode() == o.hashCode() ? 0 : (this.hashCode() > o.hashCode() ? 1 : -1);
	    } else {
		return -2;
	    }
	} else if (diffTime > 0) {
	    return 5;
	} else {
	    return -5;
	}
    }

}
