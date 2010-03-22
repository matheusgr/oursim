package br.edu.ufcg.lsd.gridsim.events;

import java.util.PriorityQueue;

public class TimeQueue {

    private PriorityQueue<TimedEvent> pq;
    private int time = -1;

    public TimeQueue() {
        pq = new PriorityQueue<TimedEvent>();
    }

    public void addEvent(TimedEvent event) {
        pq.add(event);
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

    public int currentTime() {
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