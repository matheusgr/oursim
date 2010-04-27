package oursim.events;

import oursim.entities.Task;
import oursim.jobevents.ComputableElementEventDispatcher;

public class StartedTaskEvent extends TaskTimedEvent {

	StartedTaskEvent(Task task) {
		super(task.getStartTime(), 3, task);
	}

	@Override
	protected void doAction() {
		ComputableElementEventDispatcher.getInstance().dispatchStarted(compElement);
	}

}
