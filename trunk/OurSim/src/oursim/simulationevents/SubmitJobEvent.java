package oursim.simulationevents;

import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.entities.Job;

public class SubmitJobEvent extends JobTimedEvent {

	SubmitJobEvent(long time, Job job) {
		super(time, 4, job);
	}

	@Override
	protected final void doAction() {
		JobEventDispatcher.getInstance().dispatchJobSubmitted((Job) content);
	}

}
