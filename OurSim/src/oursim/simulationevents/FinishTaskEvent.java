package oursim.simulationevents;

import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.entities.Task;

public class FinishTaskEvent extends TaskTimedEvent {

	FinishTaskEvent(long time, Task task) {
		super(time, 1, task);
	}

	@Override
	protected final void doAction() {
		Task task = (Task) content;
		task.finish(time);
		TaskEventDispatcher.getInstance().dispatchTaskFinished(task);
	}

}
