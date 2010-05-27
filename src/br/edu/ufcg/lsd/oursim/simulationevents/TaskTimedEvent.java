package br.edu.ufcg.lsd.oursim.simulationevents;

import br.edu.ufcg.lsd.oursim.entities.Task;

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
	public int compareTo(TimedEvent ev) {
		// TODO: Política
		int compareToFromSuper = super.compareTo(ev);
		// o super não foi conclusivo e o outro evento é do mesmo tipo deste?
		if (compareToFromSuper == 0 && ev instanceof TaskTimedEvent) {
			TaskTimedEvent o = (TaskTimedEvent) ev;
			// essa task já foi preemptada?
			if (this.content.getNumberOfpreemptions() > 0) {
				// TODO: definir qual é a política nesse caso
				// a outra também já foi preemptada?
				if (o.content.getNumberOfpreemptions() > 0) {
					// os numeros de preempcoes são diferentes?
					if (o.content.getNumberOfpreemptions() != this.content.getNumberOfpreemptions()) {
						// prioriza a que já foi preemptadas mais vezes
						return (int) (o.content.getNumberOfpreemptions() - this.content.getNumberOfpreemptions());
					} else {
						// se eh tudo igual, então desempata pelo id.
						return (int) (this.content.getId() - o.content.getId());
					}
				} else {
					return -1;
				}
			} else if (o.content.getNumberOfpreemptions() > 0) {
				// se esta não foi, a outra já foi?
				return 1;
			} else {
				// se eh tudo igual, então desempata pelo id.
				return (int) (this.content.getId() - o.content.getId());
			}
		} else {
			return compareToFromSuper;
		}
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
