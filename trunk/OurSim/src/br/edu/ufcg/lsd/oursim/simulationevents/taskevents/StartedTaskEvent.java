package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * Event indicating that a task has been started.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class StartedTaskEvent extends TaskTimedEvent {

	public static final int PRIORITY = 4;

	/**
	 * Creates an event indicating that a task has been started.
	 * 
	 * @param task
	 *            the task that has been started.
	 */
	public StartedTaskEvent(Task task) {
		super(task.getStartTime(), PRIORITY, task);
	}

	@Override
	protected void doAction() {
		Task task = (Task) source;
		if (!task.isCancelled()) {
			TaskEventDispatcher.getInstance().dispatchTaskStarted(task);
		}
	}

}
