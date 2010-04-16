package oursim.events;

import oursim.entities.Job;
import oursim.policy.SchedulerPolicy;

public abstract class TimedEvent implements Comparable<TimedEvent> {

    private long id;
    private boolean cancel;

    protected long time;
    protected SchedulerPolicy scheduler;
    protected Job job;

    public TimedEvent(long time, long id) {
	this.time = time;
	this.cancel = false;
	this.id = id;
    }

    protected abstract void doAction();

    public void cancel() {
	this.cancel = true;
    }

    public final void action() {
	if (!cancel) {
	    doAction();
	}
    }

    public boolean isCancelled() {
	return this.cancel;
    }

    public long getTime() {
	return this.time;
    }

    @Override
    public int compareTo(TimedEvent o) {
	long diffTime = this.time - o.time;
	if (diffTime == 0) {
	    if (this.id >= o.id) {
		return 1;
	    } else {
		return -1;
	    }
	} else if (diffTime > 0) {
	    return 2;
	} else {
	    return -2;
	}
    }

}