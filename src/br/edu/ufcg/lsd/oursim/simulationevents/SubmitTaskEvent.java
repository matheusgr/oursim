package br.edu.ufcg.lsd.oursim.simulationevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * Event indicating that a task was submitted. Used when a task has been
 * preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 30/04/2010
 * 
 */
public class SubmitTaskEvent extends TaskTimedEvent {

	public static final int PRIORITY = 3;

	/**
	 * Creates an event indicating that a task was submitted.
	 * 
	 * @param submitTime
	 *            the time at which the job has been submitted.
	 * @param task
	 *            the task that has been submitted.
	 */
	SubmitTaskEvent(long submitTime, Task task) {
		super(submitTime, PRIORITY, task);
	}

	@Override
	protected final void doAction() {
		TaskEventDispatcher.getInstance().dispatchTaskSubmitted((Task) content);
	}

}
