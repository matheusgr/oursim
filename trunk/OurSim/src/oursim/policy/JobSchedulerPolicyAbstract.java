package oursim.policy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import oursim.dispatchableevents.Event;
import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.input.Workload;
import oursim.simulationevents.EventQueue;

/**
 * 
 * An mininal interface to define a grid Scheduler.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public abstract class JobSchedulerPolicyAbstract implements JobSchedulerPolicy {

	/**
	 * The event queue that will be processed by this scheduler.
	 */
	protected EventQueue eventQueue;

	/**
	 * The jobs that have been submitted to this scheduler.
	 */
	protected Set<Job> submittedJobs;

	/**
	 * The tasks of all jobs that have been submitted to this scheduler. The
	 * schedulling is effectively performed in this collection.
	 */
	protected Set<Task> submittedTasks;

	/**
	 * All the peers that participate of the grid.
	 */
	protected List<Peer> peers;

	/**
	 * The workload to be processed by this scheduler.
	 */
	private Workload workload;

	/**
	 * An ordinary constructor.
	 * 
	 * @param eventQueue
	 *            The queue with the events to be processed.
	 * @param peers
	 *            All the peers that compound of the grid.
	 */
	public JobSchedulerPolicyAbstract(EventQueue eventQueue, List<Peer> peers, Workload workload) {
		this.peers = peers;
		this.eventQueue = eventQueue;
		this.workload = workload;
		this.submittedJobs = new HashSet<Job>();
		this.submittedTasks = new TreeSet<Task>();
	}

	/**
	 * 
	 * Performs a rescheduling of the task. The task already has been schedulled
	 * but it was preempted by some reason.
	 * 
	 * @param task
	 *            The task to be rescheduled.
	 */
	protected void rescheduleTask(Task task) {
		eventQueue.addSubmitTaskEvent(eventQueue.getCurrentTime(), task);
	}

	@Override
	public void addJob(Job job) {
		assert !job.getTasks().isEmpty();
		this.submittedJobs.add(job);
		this.submittedTasks.addAll(job.getTasks());
	}

	@Override
	public final void schedule() {

		performScheduling();

		addFutureJobsToEventQueue();

	}

	@Override
	public void addWorkload(Workload workload) {
		this.workload.merge(workload);
	}

	/**
	 * The only mandatory method to be implemented by the subclasses. The
	 * semantic is the same of {@link JobSchedulerPolicy#schedule()}
	 */
	protected abstract void performScheduling();

	protected final void addFutureJobsToEventQueue() {
		long nextSubmissionTime = (workload.peek() != null) ? workload.peek().getSubmissionTime() : -1;
		while (workload.peek() != null) {
			Job nextJob = workload.peek();
			if (nextJob.getSubmissionTime() == nextSubmissionTime) {
				Job job = workload.poll();
				long time = job.getSubmissionTime();
				eventQueue.addSubmitJobEvent(time, job);
			}
		}
	}

	// B-- beginning of implementation of JobEventListener

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
		this.addJob(jobEvent.getSource());
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
	}

	// E-- end of implementation of JobEventListener

	// B-- beginning of implementation of TaskEventListener

	@Override
	public void taskStarted(Event<Task> taskEvent) {
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
	}

	// E-- end of implementation of TaskEventListener

	// B-- beginning of implementation of WorkerEventListener

	@Override
	public void workerAvailable(Event<String> workerEvent) {
	}

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
	}

	@Override
	public void workerUp(Event<String> workerEvent) {
	}

	@Override
	public void workerDown(Event<String> workerEvent) {
	}

	@Override
	public void workerIdle(Event<String> workerEvent) {
	}

	@Override
	public void workerRunning(Event<String> workerEvent) {
	}

	// E-- end of implementation of WorkerEventListener
}
