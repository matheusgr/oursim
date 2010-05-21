package oursim.policy;

import oursim.dispatchableevents.jobevents.JobEventListener;
import oursim.dispatchableevents.taskevents.TaskEventListener;
import oursim.dispatchableevents.workerevents.WorkerEventListener;
import oursim.entities.Job;
import oursim.input.Workload;

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
	 * Simply Adds the job to this scheduler. To This method, unlike
	 * {@link JobSchedulerPolicy#addWorkload(Workload)}, doesn't matter the
	 * time at which the job was originally submitted.
	 * 
	 * @param job
	 *            The job to be added.
	 */
	void addJob(Job job);

	/**
	 * Adds a workload o this scheduler. The original workload will be
	 * invalidated after the calling of this operation. Actually, this operation
	 * performs a merge of the given workload with the others that has already
	 * been included in this scheduler.
	 * 
	 * @param workload
	 *            the workload to be added.
	 * @see {@link Workload#merge(Workload)}
	 */
	void addWorkload(Workload workload);

	/**
	 * Performs efectivelly the scheduling of the jobs enqueued in this
	 * scheduler.
	 */
	void schedule();

}
