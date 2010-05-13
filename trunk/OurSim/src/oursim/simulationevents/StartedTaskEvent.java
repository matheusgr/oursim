package oursim.simulationevents;

import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.entities.Task;

public class StartedTaskEvent extends TaskTimedEvent {

	StartedTaskEvent(Task task) {
		super(task.getStartTime(), 3, task);
	}

	@Override
	protected void doAction() {
		TaskEventDispatcher.getInstance().dispatchTaskStarted((Task) content);
	}

}
