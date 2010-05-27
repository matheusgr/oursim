package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListenerAdapter;
import oursim.entities.Task;

/**
 * 
 * A default (empty) implementation of the listener
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public abstract class TaskEventListenerAdapter implements TaskEventListener, EventListenerAdapter {

	@Override
	public void taskFinished(Event<Task> taskEvent) {
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
	}

}
