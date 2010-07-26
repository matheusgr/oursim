package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * Event indicating that a task was preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class CancelledTaskEvent extends TaskTimedEvent {

	public static final int PRIORITY = 0;

	/**
	 * Creates an event indicating that a task has been cancelled.
	 * 
	 * @param cancellingTime
	 *            the time at which the task has been cancelled.
	 * @param task
	 *            the task that has been preempted.
	 */
	public CancelledTaskEvent(long cancellingTime, Task task) {
		super(cancellingTime, PRIORITY, task);
	}

	@Override
	protected void doAction() {
		Task task = this.source;
		task.cancel();
		TaskEventDispatcher.getInstance().dispatchTaskCancelled(task, this.time);
	}

}
