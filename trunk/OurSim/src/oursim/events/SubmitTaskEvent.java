package oursim.events;

import oursim.entities.Task;
import oursim.jobevents.ComputableElementEventDispatcher;

public class SubmitTaskEvent extends TaskTimedEvent {

	SubmitTaskEvent(long time, Task task) {
		super(time, 4, task);
	}

	@Override
	protected final void doAction() {
		ComputableElementEventDispatcher.getInstance().dispatchSubmitted((Task) compElement);
	}

}
