package oursim.events;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import oursim.entities.Job;

public class EventQueue {

	private long time = -1;

	private PriorityQueue<TimedEvent> pq;

	private static EventQueue instance = null;

	// for cache purpose
	private Map<Job, FinishJobEvent> job2FinishJobEvent;

	private EventQueue() {
		pq = new PriorityQueue<TimedEvent>();
		job2FinishJobEvent = new HashMap<Job, FinishJobEvent>();
	}

	public static EventQueue getInstance() {
		return instance = (instance != null) ? instance : new EventQueue();
	}

	private void addEvent(TimedEvent event) {
		assert event.getTime() >= time : event.getTime() + ">=" + time;
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
		this.addEvent(new PreemptedJobEvent(job, time));
	}

	public void addFinishJobEvent(long finishTime, Job job) {
		assert finishTime > this.currentTime();
		FinishJobEvent finishJobEvent = new FinishJobEvent(finishTime, job);
		this.addEvent(finishJobEvent);
		this.job2FinishJobEvent.put(job, finishJobEvent);
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

	public long currentTime() {
		return this.time;
	}

	public TimedEvent peek() {
		return pq.peek();
	}

	public int size() {
		return pq.toArray().length;
	}

	@Override
	public String toString() {
		return "TimeQueue [pq=" + pq + ", time=" + time + "]";
	}

}