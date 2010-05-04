package oursim.events;

import oursim.entities.Task;
import oursim.jobevents.TaskEventDispatcher;

/**
 * 
 * Utilizado quando uma task é preemptada.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 30/04/2010
 * 
 */
public class SubmitTaskEvent extends TaskTimedEvent {

	SubmitTaskEvent(long time, Task task) {
		super(time, 4, task);
	}

	@Override
	protected final void doAction() {
		TaskEventDispatcher.getInstance().dispatchTaskSubmitted((Task) content);
	}

}
