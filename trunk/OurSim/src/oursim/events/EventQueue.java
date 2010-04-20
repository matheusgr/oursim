package oursim.events;

import java.util.PriorityQueue;

import oursim.entities.Job;
import oursim.policy.JobSchedulerPolicy;

public class EventQueue {

    private PriorityQueue<TimedEvent> pq;
    private long time = -1;

    public EventQueue() {
	pq = new PriorityQueue<TimedEvent>();
    }

    public void addEvent(TimedEvent event) {
	assert event.getTime() >= time;
	pq.add(event);
    }

    public void addSubmitJobEvent(long submitTime, Job job, JobSchedulerPolicy sp) {
	this.addEvent(new SubmitJobEvent(submitTime, job, sp));
    }    
    
    public void addStartedJobEvent(Job job, JobSchedulerPolicy sp) {
	this.addEvent(new StartedJobEvent(job, sp));
	this.addFinishJobEvent(job.getEstimatedFinishTime(), job, sp);
    }   
    
    public void addFinishJobEvent(long finishTime, Job job, JobSchedulerPolicy sp) {
	assert finishTime > this.currentTime();
	this.addEvent(new FinishJobEvent(finishTime, job, sp));
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