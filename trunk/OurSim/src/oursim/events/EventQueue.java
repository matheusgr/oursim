package oursim.events;

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

	private void addEvent(TimedEvent event) {
		assert event.getTime() >= time : event.getTime() + ">=" + time;
		amountOfEvents++;
		printEvent(event);
		pq.add(event);
	}

	public void addSubmitJobEvent(long submitTime, Job job) {
		this.addEvent(new SubmitJobEvent(submitTime, job));
	}

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
		FinishTaskEvent finishJobEvent = new FinishTaskEvent(finishTime, task);
		this.addEvent(finishJobEvent);
		this.task2FinishTaskEvent.put(task, finishJobEvent);
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

				ComputableElementTimedEvent event = (ComputableElementTimedEvent) ev;

				String type = event.getType();
				String time = Long.toString(event.getTime());
				String taskId = null;
				String jobId = null;
				String peer = event.compElement.getSourcePeer().getName();
				try {
					Task task = (Task) event.compElement;
					taskId = Long.toString(task.getId());
					jobId = Long.toString(task.getSourceJob().getId());
				} catch (ClassCastException e) {
					jobId = Long.toString(event.compElement.getId());
				}
				String makespan = event.compElement.getMakeSpan() + "";
				String runningTime = event.compElement.getRunningTime() + "";
				String queuingTime = event.compElement.getQueueingTime() + "";

				bw.append(type).append(" ")
				  .append(time).append(" ")
				  .append(taskId).append(" ")
				  .append(jobId).append(" ")
				  .append(peer).append(" ")
				  .append(makespan).append(" ")
				  .append(runningTime).append(" ")
				  .append(queuingTime).append("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "TimeQueue [pq=" + pq + ", time=" + time + "]";
	}

}