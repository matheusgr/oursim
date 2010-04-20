package oursim.events;

import oursim.entities.Job;
import oursim.output.OutputManager;
import oursim.policy.JobSchedulerPolicy;

public class StartedJobEvent extends TimedEvent {

    StartedJobEvent(Job job, JobSchedulerPolicy sp) {
	super(job.getStartTime(), -job.getId());
	this.job = job;
    }

    @Override
    protected void doAction() {
	OutputManager.getInstance().dispatchJobStarted(job);
    }

}
