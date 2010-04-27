package oursim.events;

import oursim.entities.Job;
import oursim.jobevents.JobEventDispatcher;

public class SubmitJobEvent extends JobTimedEvent {

	SubmitJobEvent(long time, Job job) {
		super(time, 4, job);
	}

	@Override
	protected final void doAction() {
		JobEventDispatcher.getInstance().dispatchJobSubmitted((Job) compElement);
	}

}
