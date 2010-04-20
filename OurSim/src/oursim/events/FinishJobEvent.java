package oursim.events;

import oursim.entities.Job;
import oursim.output.OutputManager;
import oursim.policy.JobSchedulerPolicy;

public class FinishJobEvent extends TimedEvent {

    public static int amountOfFinishedJobs = 0;

    FinishJobEvent(long time, Job job, JobSchedulerPolicy scheduler) {
	super(time, -job.getId());
	this.job = job;
	this.scheduler = scheduler;
	this.job.setFinishedJobEvent(this);
    }

    @Override
    protected final void doAction() {
	OutputManager.getInstance().dispatchJobFinished(job);
	this.job.finishJob(time);
	amountOfFinishedJobs++;
	this.scheduler.finishJob(job);
    }

}
