package oursim.events;

import oursim.entities.Task;
import oursim.jobevents.ComputableElementEventDispatcher;

public class PreemptedTaskEvent extends TaskTimedEvent {

	PreemptedTaskEvent(Task task, long time) {
		super(time, 2, task);
	}

	@Override
	protected void doAction() {
		Task task = (Task) compElement;
		task.preempt(time);
		ComputableElementEventDispatcher.getInstance().dispatchPreempted(task);
	}

}
