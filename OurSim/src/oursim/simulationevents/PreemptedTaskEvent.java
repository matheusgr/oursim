package oursim.simulationevents;

import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.entities.Task;

/**
 *
 * Event indicating that a task was preempted.
 *
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 *
 */
public class PreemptedTaskEvent extends TaskTimedEvent {
	
	/**
	 * Creates an event indicating that a task has been preempted.
	 * 
	 * @param preemptionTime
	 *            the time at which the task has been preempted.
	 * @param task
	 *            the task that has been preempted.
	 */
	PreemptedTaskEvent(long preemptionTime, Task task) {
		super(preemptionTime, 2, task);
	}

	@Override
	protected void doAction() {
		Task task = content;
		task.preempt(time);
		TaskEventDispatcher.getInstance().dispatchTaskPreempted(task);
	}

}
