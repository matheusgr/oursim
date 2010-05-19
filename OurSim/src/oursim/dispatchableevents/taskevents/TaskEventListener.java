package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListener;
import oursim.entities.Task;

public interface TaskEventListener extends EventListener {

	void taskSubmitted(Event<Task> taskEvent);

	void taskStarted(Event<Task> taskEvent);

	void taskFinished(Event<Task> taskEvent);

	void taskPreempted(Event<Task> taskEvent);

}
