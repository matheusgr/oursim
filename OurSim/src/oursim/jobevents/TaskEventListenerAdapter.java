package oursim.jobevents;

import java.util.EventListener;

public class TaskEventListenerAdapter implements TaskEventListener {

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
