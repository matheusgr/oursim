package oursim.events;

import oursim.entities.Job;
import oursim.jobevents.JobEventDispatcher;

public class PreemptedJobEvent extends TimedEvent {

	PreemptedJobEvent(Job job, long time) {
		super(time, 2);
		this.job = job;
	}

	@Override
	protected void doAction() {
		this.job.preempt(time);
		JobEventDispatcher.getInstance().dispatchJobPreempted(this.job);
	}

}
