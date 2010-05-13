package oursim.dispatchableevents.workerevents;

import java.util.EventObject;

public class WorkerEvent extends EventObject {

	private static final long serialVersionUID = 8645620849581846104L;

	protected long time = -1;

	public WorkerEvent(long time, String machineName) {
		super(machineName);
		this.time = time;
	}

	public long getTime() {
		assert time >= 0;
		return time;
	}
}