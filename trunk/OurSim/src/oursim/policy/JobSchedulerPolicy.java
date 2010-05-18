package oursim.policy;

import oursim.dispatchableevents.jobevents.JobEventListener;
import oursim.dispatchableevents.taskevents.TaskEventListener;
import oursim.dispatchableevents.workerevents.WorkerEventListener;
import oursim.entities.Job;

/**
 * 
 * An mininal interface to define a grid Scheduler.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public interface JobSchedulerPolicy extends JobEventListener, TaskEventListener, WorkerEventListener {

	/**
	 * Simply Adds the job to this scheduler.
	 * 
	 * @param job
	 *            The job to be added.
	 */
	void addJob(Job job);

	/**
	 * Performs efectivelly the scheduling of the jobs enqueued in this
	 * scheduler.
	 */
	void schedule();

}
