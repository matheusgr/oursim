package oursim.events;

import oursim.entities.Job;
import oursim.jobevents.JobEventDispatcher;

public class SubmitJobEvent extends TimedEvent {

	SubmitJobEvent(long time, Job job) {
		super(time, 4);
		this.job = job;
	}

	@Override
	protected final void doAction() {
		JobEventDispatcher.getInstance().dispatchJobSubmitted(job);
	}

}
