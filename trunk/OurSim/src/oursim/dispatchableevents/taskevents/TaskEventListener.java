package oursim.dispatchableevents.taskevents;

import java.util.EventListener;

public interface TaskEventListener extends EventListener {

	void taskSubmitted(TaskEvent taskEvent);

	void taskStarted(TaskEvent taskEvent);

	void taskFinished(TaskEvent taskEvent);

	void taskPreempted(TaskEvent taskEvent);

}
