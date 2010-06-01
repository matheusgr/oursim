package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

/**
 * 
 * Event indicating that a task was preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class PreemptedTaskEvent extends TaskTimedEvent {

	public static final int PRIORITY = 2;

	/**
	 * Creates an event indicating that a task has been preempted.
	 * 
	 * @param preemptionTime
	 *            the time at which the task has been preempted.
	 * @param task
	 *            the task that has been preempted.
	 */
	public PreemptedTaskEvent(long preemptionTime, Task task) {
		super(preemptionTime, PRIORITY, task);
	}

	@Override
	protected void doAction() {
		Task task = content;
		task.preempt(time);
		TaskEventDispatcher.getInstance().dispatchTaskPreempted(task, time);
		// TODO: se for um job de uma task só, avisa que o job foi preemptado
		if (task.getSourceJob().getTasks().size() == 1 && task.isAllRepliesFailed()) {
			EventQueue.getInstance().addPreemptedJobEvent(EventQueue.getInstance().getCurrentTime(), task.getSourceJob());
		}
	}

}
