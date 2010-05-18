package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.EventListenerAdapter;

public class TaskEventListenerAdapter implements TaskEventListener, EventListenerAdapter {

	@Override
	public void taskFinished(TaskEvent taskEvent) {
	}

	@Override
	public void taskPreempted(TaskEvent taskEvent) {
	}

	@Override
	public void taskStarted(TaskEvent taskEvent) {
	}

	@Override
	public void taskSubmitted(TaskEvent taskEvent) {
	}

}
