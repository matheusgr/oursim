package oursim.simulationevents;

import oursim.entities.Task;

/**
 * 
 * The root class to all task's related events.
 * 
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public abstract class TaskTimedEvent extends TimedEventAbstract<Task> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 * @param task
	 *            the task this event relates to.
	 */
	TaskTimedEvent(long time, int priority, Task task) {
		super(time, priority, task);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String type = this.getType();
		String time = Long.toString(this.getTime());
		String peer = this.content.getSourcePeer().getName();
		String taskId = Long.toString(this.content.getId());
		String jobId = Long.toString(this.content.getSourceJob().getId());
		String makespan = this.content.getMakeSpan() + "";
		String runningTime = this.content.getRunningTime() + "";
		String queuingTime = this.content.getQueueingTime() + "";
		sb.append(type).append(" ").append(time).append(" ").append(taskId).append(" ").append(jobId).append(" ").append(peer).append(" ").append(makespan)
				.append(" ").append(runningTime).append(" ").append(queuingTime);
		return sb.toString();
	}

}
