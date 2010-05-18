package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.Event;
import oursim.entities.Task;

public class TaskEvent extends Event {

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