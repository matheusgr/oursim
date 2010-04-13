package oursim.events;

import oursim.entities.Job;
import oursim.output.DefaultOutput;
import oursim.policy.SchedulerPolicy;

public class SubmitJobEvent extends TimedEvent {

    private Job job;

    private SchedulerPolicy scheduler;

    public SubmitJobEvent(long time, Job job, SchedulerPolicy scheduler) {
	super(time, job.getId());
	this.job = job;
	this.scheduler = scheduler;
	this.job.setSubmitJobEvent(this);
    }

    @Override
    public void doAction() {
	DefaultOutput.getInstance().submitJob(time, job);
	this.scheduler.addJob(job);
    }

    public void resubmit() {
	DefaultOutput.getInstance().submitJob(time, job);
	this.scheduler.schedule(job);
    }
    
}
