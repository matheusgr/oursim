package oursim.policy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.events.EventQueue;
import oursim.jobevents.JobEvent;
import oursim.jobevents.JobEventListenerAdapter;
import oursim.jobevents.TaskEvent;
import oursim.workerevents.WorkerEvent;

public class OurGridScheduler extends JobEventListenerAdapter implements JobSchedulerPolicy {

	private EventQueue eventQueue;

	private TreeSet<Job> submittedJobs;

	private TreeSet<Task> submittedTasks;

	private List<Peer> peers;
	private HashMap<String, Peer> peersMap;

	public static int numberOfPreemptionsForAllJobs = 0;

	public static long numberOfPreemptionsForAllTasks = 0;

	public OurGridScheduler(EventQueue eventQueue, List<Peer> peers) {
		this(eventQueue, peers, new TreeSet<Job>());
	}

	public OurGridScheduler(EventQueue eventQueue, List<Peer> peers, TreeSet<Job> submittedJobs) {
		this.peers = peers;
		this.eventQueue = eventQueue;
		this.submittedJobs = submittedJobs;
		this.peersMap = new HashMap<String, Peer>();

		this.submittedTasks = new TreeSet<Task>();

		for (Job job : submittedJobs) {
			this.submittedTasks.addAll(job.getTasks());
		}

		for (Peer p : this.peers) {
			peersMap.put(p.getName(), p);
		}

	}

	@Override
	public void rescheduleJob(Job job) {
		// Utilizado quando um job é preemptada.
		eventQueue.addSubmitJobEvent(eventQueue.currentTime(), job);
	}

	private void rescheduleTask(Task task) {
		// Utilizado quando uma task é preemptada.
		eventQueue.addSubmitTaskEvent(eventQueue.currentTime(), task);
	}

	@Override
	public void addJob(Job job) {
		assert !job.getTasks().isEmpty();
		this.submittedJobs.add(job);
		this.submittedTasks.addAll(job.getTasks());
	}

	public void addTask(Task task) {
		this.submittedTasks.add(task);
	}

	@Override
	public void finishJob(Job job) {
		throw new RuntimeException("Método ainda não implementado.");
		// job.getTargetPeer().finishJob(job, false);
	}

	@Override
	public void scheduleTasks() {

		for (Iterator<Task> iterator = submittedTasks.iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			task.getSourcePeer().prioritizeResourcesToConsume(peers);
			for (Peer provider : peers) {
				boolean isTaskRunning = provider.addTask(task);
				if (isTaskRunning) {
					updateTaskState(task, provider);
					iterator.remove();
					break;
				}
			}
		}

	}

	private void updateTaskState(Task task, Peer provider) {
		assert task.getTargetPeer() == null;
		task.setStartTime(eventQueue.currentTime());
		task.setTargetPeer(provider);
		eventQueue.addStartedTaskEvent(task);
	}

	@Override
	public void jobSubmitted(JobEvent jobEvent) {
		Job job = (Job) jobEvent.getSource();
		this.addJob(job);
	}

	@Override
	public void jobPreempted(JobEvent jobEvent) {
		numberOfPreemptionsForAllJobs++;
		Job job = (Job) jobEvent.getSource();
		this.rescheduleJob(job);
	}

	@Override
	public void taskFinished(TaskEvent taskEvent) {
		Task task = (Task) taskEvent.getSource();
		task.getTargetPeer().finishTask(task);
		if (task.getSourceJob().isFinished()) {
			eventQueue.addFinishJobEvent(eventQueue.currentTime(), task.getSourceJob());
		}
	}

	@Override
	public void taskSubmitted(TaskEvent taskEvent) {
		Task task = (Task) taskEvent.getSource();
		this.addTask(task);
	}

	@Override
	public void taskPreempted(TaskEvent taskEvent) {
		numberOfPreemptionsForAllTasks++;
		Task task = (Task) taskEvent.getSource();
		this.rescheduleTask(task);
	}

	@Override
	public void taskStarted(TaskEvent taskEvent) {
		// nothing to do
	}

	@Override
	public void workerAvailable(WorkerEvent workerEvent) {
		scheduleTasks();
	}

	@Override
	public void workerDown(WorkerEvent workerEvent) {
	}

	@Override
	public void workerIdle(WorkerEvent workerEvent) {
	}

	@Override
	public void workerRunning(WorkerEvent workerEvent) {
	}

	@Override
	public void workerUnavailable(WorkerEvent workerEvent) {
	}

	@Override
	public void workerUp(WorkerEvent workerEvent) {
	}

}
