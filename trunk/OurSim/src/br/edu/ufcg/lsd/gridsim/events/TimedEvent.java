package br.edu.ufcg.lsd.gridsim.events;

import br.edu.ufcg.lsd.gridsim.Job;

public abstract class TimedEvent implements Comparable<TimedEvent> {

    protected int time;
    private boolean cancel;
    private int id;
	private Job job;

    public TimedEvent(int time, int id, Job job) {
        this.time = time;
        this.cancel = false;
        this.id = id;
        this.job = job;
    }

    @Override
    public int compareTo(TimedEvent o) {
        int diffTime = this.time - o.time;
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

	@Override
	public String toString() {
		return "TimedEvent [job=" + job.getWastedTime() + ",type=" + this.getClass() + ", cancel=" + cancel + ", id=" + id + ", time=" + time
				+ "]";
	}

	public int getTime() {
		return this.time;
	}

    
    
}