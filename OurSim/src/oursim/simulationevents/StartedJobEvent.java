package oursim.simulationevents;

import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.entities.Job;

@Deprecated
public class StartedJobEvent extends JobTimedEvent {

	StartedJobEvent(Job job) {
		super(job.getStartTime(), 3, job);
	}

	@Override
	protected void doAction() {
		JobEventDispatcher.getInstance().dispatchJobStarted((Job) content);
	}

}
