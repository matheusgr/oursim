package oursim.events;

import java.util.ArrayList;
import java.util.List;

import oursim.entities.Job;
import oursim.output.PrintOutput;
import oursim.policy.JobSchedulerPolicy;

public class FinishJobEvent extends TimedEvent {

    public static int amountOfFinishedJobs = 0;
    public static List<Job> finishedJobs = new ArrayList<Job>();

    FinishJobEvent(long time, Job job, JobSchedulerPolicy scheduler) {
	super(time, -job.getId());
	this.job = job;
	this.scheduler = scheduler;
	this.job.setFinishedJobEvent(this);
    }

    @Override
    protected final void doAction() {
	PrintOutput.getInstance().finishJob(time, job);
	this.job.finishJob(time);
	amountOfFinishedJobs++;
	this.scheduler.finishJob(job);
	finishedJobs.add(job);
    }

}
