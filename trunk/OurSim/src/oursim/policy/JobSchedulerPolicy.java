package oursim.policy;

import oursim.entities.Job;

public interface JobSchedulerPolicy {

    /**
     * Performs a rescheduling of the job. This job already has been schedulled
     * but it was preempted by some reason.
     * 
     * @param job
     *                The job to be rescheduled.
     */
    public void rescheduleJob(Job job);

    /**
     * Performs efectivelly the scheduling of the jobs enqueued in this
     * scheduler.
     */
    public void scheduleJobs();

    /**
     * Simply Adds the job to this scheduler.
     * 
     * @param job
     *                The job to be added.
     */
    public void addJob(Job job);

    /**
     * Finishs the job from this scheduler.
     * 
     * @param job
     *                The job to be finished.
     */
    public void finishJob(Job job);

}
