package oursim.simulationevents;

import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.entities.Task;

/**
 * 
 * Event indicating that a task must be finished.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class FinishTaskEvent extends TaskTimedEvent {

	public static final int PRIORITY = 1;

	/**
	 * Creates an event indicating that a task has been finished.
	 * 
	 * @param finishTime
	 *            the time at which the task has been finished.
	 * @param task
	 *            the task that has been finished.
	 */
	FinishTaskEvent(long finishTime, Task task) {
		super(finishTime, PRIORITY, task);
	}

	@Override
	protected final void doAction() {
		Task task = (Task) content;
		task.finish(time);
		if (task.getSourceJob().isFinished()) {
			EventQueue.getInstance().addFinishJobEvent(EventQueue.getInstance().getCurrentTime(), task.getSourceJob());
		}
		TaskEventDispatcher.getInstance().dispatchTaskFinished(task);
	}

}
