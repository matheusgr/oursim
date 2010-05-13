package oursim.simulationevents;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import oursim.Parameters;
import oursim.entities.Job;
import oursim.entities.Task;

public class EventQueue {

	private long time = -1;

	private PriorityQueue<TimedEvent> pq;

	private static EventQueue instance = null;

	public static long amountOfEvents = 0;

	// for cache purpose
	private Map<Job, FinishJobEvent> job2FinishJobEvent;
	private Map<Task, FinishTaskEvent> task2FinishTaskEvent;

	private BufferedWriter bw;

	private EventQueue() {
		pq = new PriorityQueue<TimedEvent>();
		job2FinishJobEvent = new HashMap<Job, FinishJobEvent>();
		task2FinishTaskEvent = new HashMap<Task, FinishTaskEvent>();
	}

	public static EventQueue getInstance() {
		return instance = (instance != null) ? instance : new EventQueue();
	}

	public void clear() {
		pq = new PriorityQueue<TimedEvent>();
		job2FinishJobEvent = new HashMap<Job, FinishJobEvent>();
		task2FinishTaskEvent = new HashMap<Task, FinishTaskEvent>();
		time = -1;
		amountOfEvents = 0;
	}

	private void addEvent(TimedEvent event) {
		assert event.getTime() >= time : event.getTime() + ">=" + time;
		amountOfEvents++;
		// printEvent(event);
		pq.add(event);
	}

	public void addSubmitJobEvent(long submitTime, Job job) {
		this.addEvent(new SubmitJobEvent(submitTime, job));
	}

	@Deprecated
	public void addStartedJobEvent(Job job) {
		this.addEvent(new StartedJobEvent(job));
		this.addFinishJobEvent(job.getEstimatedFinishTime(), job);
	}

	public void addPreemptedJobEvent(Job job, long time) {
		assert job2FinishJobEvent.containsKey(job);
		this.job2FinishJobEvent.remove(job).cancel();
		this.addEvent(new PreemptedJobEvent(time, job));
	}

	public void addFinishJobEvent(long finishTime, Job job) {
		assert finishTime >= this.currentTime();
		FinishJobEvent finishJobEvent = new FinishJobEvent(finishTime, job);
		this.addEvent(finishJobEvent);
		this.job2FinishJobEvent.put(job, finishJobEvent);
	}

	public void addSubmitTaskEvent(long submitTime, Task task) {
		this.addEvent(new SubmitTaskEvent(submitTime, task));
	}

	public void addStartedTaskEvent(Task task) {
		this.addEvent(new StartedTaskEvent(task));
		this.addFinishTaskEvent(task.getEstimatedFinishTime(), task);
	}

	public void addPreemptedTaskEvent(Task task, long time) {
		assert task2FinishTaskEvent.containsKey(task);
		this.task2FinishTaskEvent.remove(task).cancel();
		this.addEvent(new PreemptedTaskEvent(task, time));
	}

	public void addFinishTaskEvent(long finishTime, Task task) {
		assert finishTime > this.currentTime();

		FinishTaskEvent finishTaskEvent = new FinishTaskEvent(finishTime, task);
		this.addEvent(finishTaskEvent);

		if (task2FinishTaskEvent.containsKey(task)) {
			this.task2FinishTaskEvent.remove(task).cancel();
		}

		this.task2FinishTaskEvent.put(task, finishTaskEvent);
	}

	public void addWorkerAvailableEvent(String machineName, long time, long duration) {
		assert duration > 0 && time >= 0;
		addEvent(new WorkerAvailableEvent(time, machineName));
		addEvent(new WorkerUnavailableEvent(time + duration, machineName));
	}

	public void removeEvent(TimedEvent event) {
		pq.remove(event);
	}

	public TimedEvent poll() {
		if (pq.peek() != null) {
			if (!pq.peek().isCancelled()) {
				if (this.time > pq.peek().time) {
					System.err.println(this);
					System.err.println("Offending Event! " + pq.peek() + " CT: " + time);
					throw new RuntimeException("Cannot go to the past :)");
				}
				this.time = pq.peek().time;
			}
		}
		return pq.poll();
	}

	public void close() {
		try {
			if (bw != null) {
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long currentTime() {
		return this.time;
	}

	public TimedEvent peek() {
		return pq.peek();
	}

	public int size() {
		return pq.toArray().length;
	}

	private void printEvent(TimedEvent ev) {

		if (Parameters.LOG) {
			try {
				if (bw == null) {
					bw = new BufferedWriter(new FileWriter("events_oursim.txt"));
				}

				String taskId = null;
				String jobId = null;
				if (ev instanceof TaskTimedEvent) {
					TaskTimedEvent event = (TaskTimedEvent) ev;
					String type = event.getType();
					String time = Long.toString(event.getTime());
					String peer = event.content.getSourcePeer().getName();
					taskId = Long.toString(event.content.getId());
					jobId = Long.toString(event.content.getSourceJob().getId());
					String makespan = event.content.getMakeSpan() + "";
					String runningTime = event.content.getRunningTime() + "";
					String queuingTime = event.content.getQueueingTime() + "";
					bw.append(type).append(" ").append(time).append(" ").append(taskId).append(" ").append(jobId).append(" ").append(peer).append(" ").append(
							makespan).append(" ").append(runningTime).append(" ").append(queuingTime).append("\n");
				} else if (ev instanceof JobTimedEvent) {
					JobTimedEvent event = (JobTimedEvent) ev;
					jobId = Long.toString(event.content.getId());
					String type = event.getType();
					String time = Long.toString(event.getTime());
					String peer = event.content.getSourcePeer().getName();
					String makespan = event.content.getMakeSpan() + "";
					String runningTime = event.content.getRunningTime() + "";
					String queuingTime = event.content.getQueueingTime() + "";
					bw.append(type).append(" ").append(time).append(" ").append(taskId).append(" ").append(jobId).append(" ").append(peer).append(" ").append(
							makespan).append(" ").append(runningTime).append(" ").append(queuingTime).append("\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "TimeQueue [pq=" + pq.size() + ", time=" + time + "]";
	}

}