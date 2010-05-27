package oursim.simulationevents;

import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.entities.Job;

/**
 * 
 * Event indicating that a job must be finished.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class FinishJobEvent extends JobTimedEvent {

	public static final int PRIORITY = 1;

	/**
	 * Creates an event indicating that a job has been finished.
	 * 
	 * @param finishTime
	 *            the time at which the job has been finished.
	 * @param job
	 *            the job that has been finished.
	 */
	FinishJobEvent(long finishTime, Job job) {
		super(finishTime, PRIORITY, job);
	}

	@Override
	protected final void doAction() {
		Job job = (Job) content;
		job.finish(time);
		JobEventDispatcher.getInstance().dispatchJobFinished(job);
	}

}
