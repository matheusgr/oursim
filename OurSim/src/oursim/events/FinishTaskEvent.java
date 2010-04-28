package oursim.events;

import oursim.entities.Task;
import oursim.jobevents.TaskEventDispatcher;

public class FinishTaskEvent extends TaskTimedEvent {

	public static long amountOfFinishedTasks;

	FinishTaskEvent(long time, Task task) {
		super(time, 1,task);
	}

	@Override
	protected final void doAction() {
		amountOfFinishedTasks++;
		Task task = (Task)compElement;
		task.finish(time);
		TaskEventDispatcher.getInstance().dispatchTaskFinished(task);
	}

}
