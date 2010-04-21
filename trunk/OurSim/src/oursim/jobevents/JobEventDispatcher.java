package oursim.jobevents;

import java.util.ArrayList;
import java.util.List;

import oursim.entities.Job;

public class JobEventDispatcher {

    protected List<JobEventListener> listeners;

    private static JobEventDispatcher instance = null;

    private JobEventDispatcher() {
	this.listeners = new ArrayList<JobEventListener>();
    }

    public static JobEventDispatcher getInstance() {
	return instance = (instance != null) ? instance : new JobEventDispatcher();
    }

    public void addListener(JobEventListener listener) {
	this.listeners.add(listener);
    }

    public void removeListener(JobEventListener listener) {
	this.listeners.remove(listener);
    }

    public void dispatchJobFinished(Job job) {
	JobEvent jobEvent = new JobEvent(job);
	for (JobEventListener listener : listeners) {
	    listener.jobFinished(jobEvent);
	}
    }

    public void dispatchJobSubmitted(Job job) {
	JobEvent jobEvent = new JobEvent(job);
	for (JobEventListener listener : listeners) {
	    listener.jobSubmitted(jobEvent);
	}
    }

    public void dispatchJobStarted(Job job) {
	JobEvent jobEvent = new JobEvent(job);
	for (JobEventListener listener : listeners) {
	    listener.jobStarted(jobEvent);
	}
    }

    public void dispatchJobPreempted(Job job) {
	JobEvent jobEvent = new JobEvent(job);
	for (JobEventListener listener : listeners) {
	    listener.jobPreempted(jobEvent);
	}
    }

}
