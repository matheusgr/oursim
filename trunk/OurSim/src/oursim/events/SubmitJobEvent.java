package oursim.events;

import oursim.entities.Job;
import oursim.output.PrintOutput;
import oursim.policy.SchedulerPolicy;

public class SubmitJobEvent extends TimedEvent {

    SubmitJobEvent(long time, Job job, SchedulerPolicy scheduler) {
	super(time, job.getId());
	this.job = job;
	this.scheduler = scheduler;
	this.job.setSubmitJobEvent(this);
    }

    @Override
    protected final void doAction() {
	PrintOutput.getInstance().submitJob(time, job);
	this.scheduler.addJob(job);
    }

    public void resubmit() {
	PrintOutput.getInstance().submitJob(time, job);
	this.scheduler.rescheduleJob(job);
    }
    
}
