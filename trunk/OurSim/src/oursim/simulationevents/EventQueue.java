package oursim.simulationevents;

import java.io.BufferedWriter;
import java.io.Closeable;
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
 * The data structure responsible for deal with the simulation events. To add an
 * event to this queue the parameters of the event must be passed to the
 * apropriate method must be called. There isn't another way to create and event
 * outside of this package.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @author Matheus G. do Rêgo, matheusgr@lsd.ufcg.edu.br
 * @since 14/05/2010
 * 
 */
public class EventQueue implements Closeable {

	/**
	 * the current simulation's time.
	 */
	private long currentTime = -1;

	/**
	 * The data structure that holds efectivelly the events.
	 */
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

	/**
	 * Cleans all the state of this EventQueue. Its behaviour is something like
	 * a new instantiation of the EventQueue.
	 */
	public void clear() {
		pq = new PriorityQueue<TimedEvent>();
		job2FinishJobEvent = new HashMap<Job, FinishJobEvent>();
		task2FinishTaskEvent = new HashMap<Task, FinishTaskEvent>();
		currentTime = -1;
		totalNumberOfEvents = 0;
	}

	/**
	 * Adds an generic event to this queue.
	 * 
	 * @param event
	 *            the event to be added.
	 */
	private void addEvent(TimedEvent event) {
		assert event.getTime() >= currentTime : event.getTime() + ">=" + currentTime;
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

	/**
	 * Remove an event of this queue.
	 * 
	 * @param event
	 *            the event to be removed.
	 */
	public void removeEvent(TimedEvent event) {
		pq.remove(event);
	}

	/**
	 * Adds an event indicating that a job was submitted.
	 * 
	 * @param submitTime
	 *            the time at which the job has been submitted.
	 * @param job
	 *            the job that has been submitted.
	 */
	public void addSubmitJobEvent(long submitTime, Job job) {
		assert submitTime >= currentTime;
		this.addEvent(new SubmitJobEvent(submitTime, job));
	}

	/**
	 * Adds an event indicating that a job has been started.
	 * 
	 * @param job
	 *            the job that has been started.
	 */
	@Deprecated
	public void addStartedJobEvent(Job job) {
		this.addEvent(new StartedJobEvent(job));
		this.addFinishJobEvent(job.getEstimatedFinishTime(), job);
	}

	/**
	 * Adds an event indicating that a job has been preempted.
	 * 
	 * @param preemptionTime
	 *            the time at which the job has been preempted.
	 * @param job
	 *            the job that has been preempted.
	 */
	public void addPreemptedJobEvent(long preemptionTime, Job job) {
		assert job2FinishJobEvent.containsKey(job);
		this.job2FinishJobEvent.remove(job).cancel();
		this.addEvent(new PreemptJobEvent(preemptionTime, job));
	}

	/**
	 * Adds an event indicating that a job has been finished.
	 * 
	 * @param finishTime
	 *            the time at which the job has been finished.
	 * @param job
	 *            the job that has been finished.
	 */
	public void addFinishJobEvent(long finishTime, Job job) {
		assert finishTime >= this.getCurrentTime();
		FinishJobEvent finishJobEvent = new FinishJobEvent(finishTime, job);
		this.addEvent(finishJobEvent);
		this.job2FinishJobEvent.put(job, finishJobEvent);
	}

	/**
	 * Adds an event indicating that a task was submitted.
	 * 
	 * @param submitTime
	 *            the time at which the job has been submitted.
	 * @param task
	 *            the task that has been submitted.
	 */
	public void addSubmitTaskEvent(long submitTime, Task task) {
		this.addEvent(new SubmitTaskEvent(submitTime, task));
	}

	/**
	 * Adds an event indicating that a task has been started.
	 * 
	 * @param task
	 *            the task that has been started.
	 */
	public void addStartedTaskEvent(Task task) {
		this.addEvent(new StartedTaskEvent(task));
		this.addFinishTaskEvent(task.getEstimatedFinishTime(), task);
	}

	/**
	 * Adds an event indicating that a task has been preempted.
	 * 
	 * @param preemptionTime
	 *            the time at which the task has been preempted.
	 * @param task
	 *            the task that has been preempted.
	 */
	public void addPreemptedTaskEvent(long preemptionTime, Task task) {
		assert task2FinishTaskEvent.containsKey(task);
		this.task2FinishTaskEvent.remove(task).cancel();
		this.addEvent(new PreemptedTaskEvent(preemptionTime, task));
	}

	/**
	 * Adds an event indicating that a task has been finished.
	 * 
	 * @param finishTime
	 *            the time at which the task has been finished.
	 * @param task
	 *            the task that has been finished.
	 */
	public void addFinishTaskEvent(long finishTime, Task task) {
		assert finishTime > this.getCurrentTime();

		FinishTaskEvent finishTaskEvent = new FinishTaskEvent(finishTime, task);
		this.addEvent(finishTaskEvent);

		if (task2FinishTaskEvent.containsKey(task)) {
			this.task2FinishTaskEvent.remove(task).cancel();
		}

		this.task2FinishTaskEvent.put(task, finishTaskEvent);
	}

	/**
	 * Adds an event indicating that a worker has become available. It's
	 * automatically added a future event indicating that the worker has become
	 * unavailable after the duration of the availability period.
	 * 
	 * @param time
	 *            the time at which the machine has become available.
	 * @param machineName
	 *            the name of the machine that has become available.
	 * @param duration
	 *            the duration of the availability period.
	 */
	public void addWorkerAvailableEvent(long time, String machineName, long duration) {
		assert duration > 0 && time >= 0;
		addEvent(new WorkerAvailableEvent(time, machineName));
		addEvent(new WorkerUnavailableEvent(time + duration, machineName));
	}

	/**
	 * Retrieves, but does not remove, the head (first element) of this
	 * eventqueue.
	 * 
	 * @return the head of this list, or <tt>null</tt> if this eventqueue is
	 *         empty.
	 */
	public TimedEvent peek() {
		return pq.peek();
	}

	/**
	 * /** Retrieves and removes the head (first element) of this eventqueue.
	 * 
	 * @return the head of this list, or <tt>null</tt> if this eventqueue is
	 *         empty
	 */
	public TimedEvent poll() {
		// checks if the next event is a valid one
		if (pq.peek() != null && !pq.peek().isCancelled()) {
			if (this.currentTime > pq.peek().getTime()) {
				System.err.println(this);
				System.err.println("Offending Event! " + pq.peek() + " CT: " + currentTime);
				throw new RuntimeException("Cannot go to the past :)");
			}
			this.currentTime = pq.peek().getTime();
		}
		return pq.poll();
	}

	/**
	 * Gets the current simulation's time.
	 * 
	 * @return the current simulation's time.
	 */
	public long getCurrentTime() {
		return this.currentTime;
	}

	@Override
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
		return "TimeQueue [pq=" + pq.size() + ", time=" + currentTime + "]";
	}

}