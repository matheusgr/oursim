package oursim.jobevents;

import oursim.entities.Task;

public class TaskEvent extends CompElemEvent {

	private static final long serialVersionUID = 481672427365120073L;

	public TaskEvent(Task source) {
		super(source);
	}

	public TaskEvent(long time, Task source) {
		super(time, source);
	}

}
