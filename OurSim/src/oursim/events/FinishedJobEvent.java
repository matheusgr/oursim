package oursim.events;

import oursim.entities.Job;

public class FinishedJobEvent extends TimedEvent {

    private Job job;

    public FinishedJobEvent(long time, Job job) {
	super(time, -job.getId());
	this.job = job;
    }

    @Override
    public void doAction() {
	this.job.finishJob(time);
    }

}
