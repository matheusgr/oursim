package oursim.dispatchableevents.jobevents;

import java.util.EventObject;

import oursim.entities.Job;

public class JobEvent extends EventObject {

	private static final long serialVersionUID = 481672427365120073L;

	private long time = -1;

	public JobEvent(Job source) {
		super(source);
	}

	public JobEvent(long time, Job source) {
		super(source);
		this.time = time;
	}

	public long getTime() {
		assert time >= 0;
		return time;
	}

}
