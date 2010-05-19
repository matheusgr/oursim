package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListenerAdapter;
import oursim.entities.Task;

public class TaskEventListenerAdapter implements TaskEventListener, EventListenerAdapter {

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
