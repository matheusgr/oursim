package oursim.events;

import oursim.entities.Job;
import oursim.jobevents.JobEventDispatcher;

public class StartedJobEvent extends TimedEvent {

    StartedJobEvent(Job job) {
	super(job.getStartTime(), 3);
	this.job = job;
    }

    @Override
    protected void doAction() {
	JobEventDispatcher.getInstance().dispatchJobStarted(job);
    }

}
