package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;
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

public class OurGridScheduler extends JobEventListenerAdapter implements JobSchedulerPolicy {

	private EventQueue eventQueue;

	private TreeSet<Job> submittedJobs;

	private TreeSet<Task> submittedTasks;

	private List<Peer> peers;
	private HashMap<String, Peer> peersMap;

	private ResourceRequestPolicy resourceRequestPolicy;

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

		this.resourceRequestPolicy = new ResourceRequestPolicy();

	}

	@Override
	public void rescheduleJob(Job job) {
		eventQueue.addSubmitJobEvent(eventQueue.currentTime(), job);
	}

	public void rescheduleTask(Task task) {
		eventQueue.addSubmitTaskEvent(eventQueue.currentTime(), task);
	}

	@Override
	public void addJob(Job job) {
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
	public void scheduleJobs() {

		HashMap<Peer, HashSet<Peer>> triedPeers = new HashMap<Peer, HashSet<Peer>>(peersMap.size());

		int originalSize = submittedTasks.size();

		Iterator<Task> it = submittedTasks.iterator();
		for (int i = 0; i < originalSize; i++) {

			Task task = it.next();

			Peer consumer = task.getSourcePeer();

			// efeito colateral: reordena os peers
			resourceRequestPolicy.request(consumer, peers);

			for (Peer provider : peers) {

				HashSet<Peer> providersTried = triedPeers.get(consumer);

				if (providersTried != null && providersTried.contains(provider)) {
					continue;
				}

				boolean isTaskRunning = provider.addTask(task, consumer);

				if (isTaskRunning) {
					assert task.getTaskExecution() != null;
					updateTaskState(task, provider, eventQueue.currentTime());
					it.remove();
					break;
				} else {
					if (providersTried == null) {
						providersTried = new HashSet<Peer>(peers.size());
						triedPeers.put(consumer, providersTried);
					}
					providersTried.add(provider);
				}

			}
		}

	}

	private void updateTaskState(Task task, Peer provider, long startTime) {
		task.setStartTime(startTime);
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
		task.getTargetPeer().finishTask(task, false);
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

}
