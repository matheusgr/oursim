package oursim.simulationevents;

import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.entities.Job;

public class FinishJobEvent extends JobTimedEvent {

	public static int amountOfFinishedJobs = 0;

	FinishJobEvent(long time, Job job) {
		super(time, 1, job);
	}

	@Override
	protected final void doAction() {
		amountOfFinishedJobs++;
		Job job = (Job) content;
		job.finish(time);
		JobEventDispatcher.getInstance().dispatchJobFinished(job);
	}

}
