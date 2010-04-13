package br.edu.ufcg.lsd.gridsim;

import br.edu.ufcg.lsd.gridsim.events.StartJobEvent;
import br.edu.ufcg.lsd.gridsim.events.TimedEvent;

public class Job implements Comparable<Job> {

    private int jobId;

    private int submitTime;
    private int startTime;
    private int runTime;
    private int wastedTime;

    private String site;
    private Peer peer;
    
    private int preemptions;
    static int globalPreemptions = 0;

    private int nProc;
    private int finishJob;

    private StartJobEvent startJobEvent;
    private TimedEvent finishJobEvent;


    public Job(int jobId, int submitTime, int runTime, String site) {
	this.preemptions = 0;
	this.jobId = jobId;
	this.submitTime = submitTime;
	this.runTime = runTime;
	this.nProc = 1;
	this.startTime = -1;
	this.wastedTime = 0;
	this.finishJob = 0;
	this.site = site;
    }

    public int getJobId() {
	return jobId;
    }

    public int getSubmitTime() {
	return submitTime;
    }

    public int getRunTime() {
	return runTime;
    }

    public int getNProc() {
	return nProc;
    }

    public void setStartTime(int startTime) {
	assert startTime >= submitTime : "ST: " + startTime + " - SUB: " + submitTime + " - JobID: " + this.jobId;
	this.startTime = startTime;
    }

    public int getStartTime() {
	return startTime;
    }

    public int getWaitedTime() {
	return this.finishJob - (this.submitTime + this.getRunTime());
    }

    @Override
    public int compareTo(Job o) {
	int diffTime = this.submitTime - o.submitTime;
	if (diffTime == 0) {
	    if (jobId > o.jobId) {
		return 2;
	    } else if (jobId == o.jobId) {
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

    public void preemptJob(int time) {
	assert this.startTime != -1;
	this.preemptions += 1;
	globalPreemptions += 1;
	this.wastedTime += (time - this.startTime);
	// assert (this.wastedTime == this.runTime);
	this.startTime = -1;
	this.finishJobEvent.cancel();
    }

    public void setFinishedJobEvent(TimedEvent finishJobEvent) {
	this.finishJobEvent = finishJobEvent;
    }

    public int getWastedTime() {
	return this.wastedTime;
    }

    public void finishJob(int time) {
	this.finishJob = time;
    }

    public String getOrigSite() {
	return this.site;
    }

    public Peer getPeer() {
	return this.peer;
    }

    public void setPeer(Peer peer) {
	assert peer == null || this.peer == null;
	this.peer = peer;
    }

    @Override
    public String toString() {
	return "Job [jobId=" + jobId + ", nProc=" + nProc + ", runTime=" + runTime + ", startTime=" + startTime + ", submitTime=" + submitTime + "]";
    }

    public void setStartJobEvent(StartJobEvent startJobEvent) {
	this.startJobEvent = startJobEvent;
    }

    public void cancelStart() {
	if (this.startJobEvent != null) {
	    startJobEvent.cancel();
	}
    }

    public int getPreemptions() {
	return preemptions;
    }

}