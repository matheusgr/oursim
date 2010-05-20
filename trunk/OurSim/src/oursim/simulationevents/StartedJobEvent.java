package oursim.simulationevents;

import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.entities.Job;

/**
 * 
 * Event indicating that a job has been started.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
@Deprecated
public class StartedJobEvent extends JobTimedEvent {

	/**
	 * Creates an event indicating that a job has been finished.
	 * 
	 * @param job
	 *            the job that has been finished.
	 */
	StartedJobEvent(Job job) {
		super(job.getStartTime(), 3, job);
	}

	@Override
	protected void doAction() {
		JobEventDispatcher.getInstance().dispatchJobStarted((Job) content);
	}

}
