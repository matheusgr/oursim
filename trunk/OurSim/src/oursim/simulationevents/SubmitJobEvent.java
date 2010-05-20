package oursim.simulationevents;

import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.entities.Job;

/**
 * 
 * Event indicating that a job was submitted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class SubmitJobEvent extends JobTimedEvent {

	SubmitJobEvent(long time, Job job) {
		super(time, 4, job);
	}

	@Override
	protected final void doAction() {
		JobEventDispatcher.getInstance().dispatchJobSubmitted((Job) content);
	}

}
