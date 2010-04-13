package oursim.events;

import oursim.entities.Job;
import oursim.output.DefaultOutput;
import oursim.policy.SchedulerPolicy;

public class FinishJobEvent extends TimedEvent {

    private Job job;
    public static int o = 0;
    private SchedulerPolicy scheduler;

    public FinishJobEvent(long time, Job job, SchedulerPolicy scheduler) {
	super(time, -job.getId());
	this.job = job;
	this.scheduler = scheduler;
    }

    @Override
    public void doAction() {
	DefaultOutput.getInstance().finishJob(time, job);
	this.job.finishJob(time);
	o++;
	this.scheduler.finishJob(job);
    }

}
