package oursim.events;

import oursim.entities.Job;
import oursim.jobevents.JobEventDispatcher;

public class FinishJobEvent extends JobTimedEvent {

	public static int amountOfFinishedJobs = 0;

	FinishJobEvent(long time, Job job) {
		super(time, 1, job);
	}

	@Override
	protected final void doAction() {
		Job job = (Job) content;
		job.finish(time);
		amountOfFinishedJobs++;
		JobEventDispatcher.getInstance().dispatchJobFinished(job);
	}

}
