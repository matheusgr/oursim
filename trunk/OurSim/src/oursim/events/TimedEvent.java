package oursim.events;

public abstract class TimedEvent implements Comparable<TimedEvent> {

    protected long time;
    private boolean cancel;
    private long id;

    public TimedEvent(long time, long id) {
	this.time = time;
	this.cancel = false;
	this.id = id;
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

    public void cancel() {
	this.cancel = true;
    }

    public abstract void doAction();

    public void action() {
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

}