package oursim.events;

import oursim.entities.Job;
import oursim.jobevents.JobEventDispatcher;

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
