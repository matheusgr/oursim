package oursim.events;

import oursim.entities.Job;
import oursim.jobevents.JobEventDispatcher;

public class FinishJobEvent extends TimedEvent {

	public static int amountOfFinishedJobs = 0;

	FinishJobEvent(long time, Job job) {
		super(time, 1);
		this.job = job;
	}

	@Override
	protected final void doAction() {
		this.job.finishJob(time);
		amountOfFinishedJobs++;
		JobEventDispatcher.getInstance().dispatchJobFinished(job);
	}

}
