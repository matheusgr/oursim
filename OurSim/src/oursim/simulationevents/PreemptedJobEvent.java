package oursim.simulationevents;

import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.entities.Job;

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
