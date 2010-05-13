package oursim.simulationevents;

import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.entities.Task;

public class PreemptedTaskEvent extends TaskTimedEvent {

	PreemptedTaskEvent(Task task, long time) {
		super(time, 2, task);
	}

	@Override
	protected void doAction() {
		Task task = (Task) content;
		task.preempt(time);
		TaskEventDispatcher.getInstance().dispatchTaskPreempted(task);
	}

}
