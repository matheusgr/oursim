package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.Event;

public class WorkerEvent extends Event {

	private static final long serialVersionUID = 8645620849581846104L;

	private long time = -1;

	public WorkerEvent(long time, String machineName) {
		super(machineName);
		this.time = time;
	}

	public long getTime() {
		assert time >= 0;
		return time;
	}
}