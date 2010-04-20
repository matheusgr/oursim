package oursim.events;

import oursim.entities.Job;
import oursim.output.OutputManager;
import oursim.policy.JobSchedulerPolicy;

public class SubmitJobEvent extends TimedEvent {

    SubmitJobEvent(long time, Job job, JobSchedulerPolicy scheduler) {
	super(time, job.getId());
	this.job = job;
	this.scheduler = scheduler;
	this.job.setSubmitJobEvent(this);
    }

    @Override
    protected final void doAction() {
	OutputManager.getInstance().dispatchJobSubmitted(job);
	this.scheduler.addJob(job);
    }

    public void resubmit() {
	OutputManager.getInstance().dispatchJobSubmitted(job);
	this.scheduler.rescheduleJob(job);
    }

}
