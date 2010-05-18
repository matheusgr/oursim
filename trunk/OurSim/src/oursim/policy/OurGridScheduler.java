package oursim.policy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import oursim.dispatchableevents.jobevents.JobEvent;
import oursim.dispatchableevents.taskevents.TaskEvent;
import oursim.dispatchableevents.workerevents.WorkerEvent;
import oursim.dispatchableevents.workerevents.WorkerEventListenerAdapter;
import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.simulationevents.EventQueue;

/**
 * 
 * An reference implementation of a {@link JobSchedulerPolicy}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class OurGridScheduler extends WorkerEventListenerAdapter implements JobSchedulerPolicy {

	/**
	 * The event queue that will be processed by this scheduler.
	 */
	private EventQueue eventQueue;

	/**
	 * The jobs that have been submitted to this scheduler.
	 */
	private Set<Job> submittedJobs;

	/**
	 * The tasks of all jobs that have been submitted to this scheduler. The
	 * schedulling is effectively performed in this collection.
	 */
	private Set<Task> submittedTasks;

	/**
	 * All the peers that participate of the grid.
	 */
	private List<Peer> peers;

	/**
	 * An ordinary constructor.
	 * 
	 * @param eventQueue
	 *            The queue with the events to be processed.
	 * @param peers
	 *            All the peers that compound of the grid.
	 */
	public OurGridScheduler(EventQueue eventQueue, List<Peer> peers) {
		this.peers = peers;
		this.eventQueue = eventQueue;
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
	private void rescheduleTask(Task task) {
		eventQueue.addSubmitTaskEvent(eventQueue.getCurrentTime(), task);
	}

	@Override
	public void addJob(Job job) {
		assert !job.getTasks().isEmpty();
		this.submittedJobs.add(job);
		this.submittedTasks.addAll(job.getTasks());
	}

	@Override
	public void schedule() {

		for (Iterator<Task> iterator = submittedTasks.iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			task.getSourcePeer().prioritizePeersToConsume(peers);
			for (Peer provider : peers) {
				boolean isTaskRunning = provider.executeTask(task);
				if (isTaskRunning) {
					eventQueue.addStartedTaskEvent(task);
					iterator.remove();
					break;
				}
			}
		}

	}

	@Override
	public void jobSubmitted(JobEvent jobEvent) {
		Job job = (Job) jobEvent.getSource();
		this.addJob(job);
	}

	@Override
	public void jobPreempted(JobEvent jobEvent) {
		// nothing to do
	}

	@Override
	public void jobFinished(JobEvent jobEvent) {
		// nothing to do
	}

	@Override
	public void jobStarted(JobEvent jobEvent) {
		// nothing to do
	}

	@Override
	public void taskStarted(TaskEvent taskEvent) {
		// nothing to do
	}

	@Override
	public void taskFinished(TaskEvent taskEvent) {
		Task task = (Task) taskEvent.getSource();
		task.getTargetPeer().finishTask(task);
		if (task.getSourceJob().isFinished()) {
			eventQueue.addFinishJobEvent(eventQueue.getCurrentTime(), task.getSourceJob());
		}
	}

	@Override
	public void taskSubmitted(TaskEvent taskEvent) {
		Task task = (Task) taskEvent.getSource();
		this.submittedTasks.add(task);
	}

	@Override
	public void taskPreempted(TaskEvent taskEvent) {
		Task task = (Task) taskEvent.getSource();
		this.rescheduleTask(task);
	}

	@Override
	public void workerAvailable(WorkerEvent workerEvent) {
		this.schedule();
	}

}
