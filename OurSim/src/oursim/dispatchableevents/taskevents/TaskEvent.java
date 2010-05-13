package oursim.dispatchableevents.taskevents;

import java.util.EventObject;

import oursim.entities.Task;

public class TaskEvent extends EventObject {

	private static final long serialVersionUID = 481672425365120073L;

	private long time = -1;

	public TaskEvent(Task source) {
		super(source);
	}

	public TaskEvent(long time, Task source) {
		super(source);
		this.time = time;
	}

	public long getTime() {
		assert time >= 0;
		return time;
	}
	
}