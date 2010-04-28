package oursim.events;

import oursim.entities.Job;
import oursim.jobevents.JobEventDispatcher;

public class PreemptedJobEvent extends JobTimedEvent {

	PreemptedJobEvent(long time, Job job) {
		super(time, 2, job);
	}

	@Override
	protected void doAction() {
		Job job = (Job) content;
		job.preempt(time);
		JobEventDispatcher.getInstance().dispatchJobPreempted(job);
	}

}
