package oursim.simulationevents;

import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.entities.Job;

/**
 * 
 * Event indicating that a job was preempted. A job is considered preempted when
 * all its tasks have been preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class PreemptedJobEvent extends JobTimedEvent {

	public static final int PRIORITY = 2;

	PreemptedJobEvent(long time, Job job) {
		super(time, PRIORITY, job);
	}

	@Override
	protected void doAction() {
		Job job = (Job) content;
		JobEventDispatcher.getInstance().dispatchJobPreempted(job, time);
	}

}
