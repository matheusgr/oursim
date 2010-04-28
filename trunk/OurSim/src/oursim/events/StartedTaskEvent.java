package oursim.events;

import oursim.entities.Task;
import oursim.jobevents.TaskEventDispatcher;

public class StartedTaskEvent extends TaskTimedEvent {

	StartedTaskEvent(Task task) {
		super(task.getStartTime(), 3, task);
	}

	@Override
	protected void doAction() {
		TaskEventDispatcher.getInstance().dispatchTaskStarted((Task) compElement);
	}

}
