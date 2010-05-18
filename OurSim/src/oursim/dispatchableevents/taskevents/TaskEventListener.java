package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.EventListener;

public interface TaskEventListener extends EventListener {

	void taskSubmitted(TaskEvent taskEvent);

	void taskStarted(TaskEvent taskEvent);

	void taskFinished(TaskEvent taskEvent);

	void taskPreempted(TaskEvent taskEvent);

}
