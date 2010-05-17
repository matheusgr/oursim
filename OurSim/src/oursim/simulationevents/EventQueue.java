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

/**
 * 
 * The data structure responsible for deal with the simulation events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @author Matheus G. do Rêgo, matheusgr@lsd.ufcg.edu.br
 * @since 14/05/2010
 * 
 */
public class EventQueue {

	private long time = -1;

	private PriorityQueue<TimedEvent> pq;

	public static long totalNumberOfEvents = 0;

	/**
	 * For cache purpose: when a task or job has been preempted its respective
	 * {@link FinishTaskEvent} and {@link FinishJobEvent} must be canceled, and
	 * for this purpose there are these present cache.
	 */
	private Map<Job, FinishJobEvent> job2FinishJobEvent;
	private Map<Task, FinishTaskEvent> task2FinishTaskEvent;

	/**
	 * To trace the events added to this {@link EventQueue}.
	 */
	private BufferedWriter bw;

	private static EventQueue instance = null;

	private EventQueue() {
		pq = new PriorityQueue<TimedEvent>();
		job2FinishJobEvent = new HashMap<Job, FinishJobEvent>();
		task2FinishTaskEvent = new HashMap<Task, FinishTaskEvent>();
		if (Parameters.LOG) {
			try {
				bw = new BufferedWriter(new FileWriter("events_oursim.txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static EventQueue getInstance() {
		return instance = (instance != null) ? instance : new EventQueue();
	}

	public void clear() {
		pq = new PriorityQueue<TimedEvent>();
		job2FinishJobEvent = new HashMap<Job, FinishJobEvent>();
		task2FinishTaskEvent = new HashMap<Task, FinishTaskEvent>();
		time = -1;
		totalNumberOfEvents = 0;
	}

	private void addEvent(TimedEvent event) {
		assert event.getTime() >= time : event.getTime() + ">=" + time;
		totalNumberOfEvents++;
		// TODO: Verificar a necessidade desse método
		if (Parameters.LOG) {
			try {
				bw.append(event.toString()).append("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		assert finishTime >= this.getCurrentTime();
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
		assert finishTime > this.getCurrentTime();

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

	public TimedEvent peek() {
		return pq.peek();
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

	public long getCurrentTime() {
		return this.time;
	}

	// TODO: Verificar a necessidade desse método
	public void close() {
		try {
			if (bw != null) {
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "TimeQueue [pq=" + pq.size() + ", time=" + time + "]";
	}

}