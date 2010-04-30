package oursim.policy;

import oursim.entities.Job;
import oursim.jobevents.JobEventListener;
import oursim.jobevents.TaskEventListener;
import oursim.workerevents.WorkerEventListener;

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
	void scheduleTasks();

	/**
	 * Performs a rescheduling of the job. This job already has been schedulled
	 * but it was preempted by some reason.
	 * 
	 * @param job
	 *            The job to be rescheduled.
	 */
	void rescheduleJob(Job job);

	/**
	 * Finishs the job from this scheduler.
	 * 
	 * @param job
	 *            The job to be finished.
	 */
	void finishJob(Job job);

}
