package oursim.events;

import oursim.entities.Job;
import oursim.output.PrintOutput;
import oursim.policy.SchedulerPolicy;

public class FinishJobEvent extends TimedEvent {

    public static int amountOfFinishedJobs = 0;

    public FinishJobEvent(long time, Job job, SchedulerPolicy scheduler) {
	super(time, -job.getId());
	this.job = job;
	this.scheduler = scheduler;
    }

    @Override
    protected final void doAction() {
	PrintOutput.getInstance().finishJob(time, job);
	this.job.finishJob(time);
	amountOfFinishedJobs++;
	this.scheduler.finishJob(job);
    }

}
