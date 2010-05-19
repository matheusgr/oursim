package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventDispatcher;
import oursim.entities.Task;

public class TaskEventDispatcher extends EventDispatcher<Task, TaskEventListener, TaskEventFilter> {

	private enum TYPE_OF_DISPATCHING {
		submitted, started, preempted, finished
	};

	private static TaskEventDispatcher instance = null;

	private TaskEventDispatcher() {
		super();
	}

	public static TaskEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new TaskEventDispatcher();
	}

	@Override
	public void addListener(TaskEventListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			this.listenerToFilter.put(listener, TaskEventFilter.ACCEPT_ALL);
		} else {
			assert false;
		}
	}

	@Override
	public void removeListener(TaskEventListener listener) {
		this.listeners.remove(listener);
	}

	public void dispatchTaskSubmitted(Task task) {
		dispatch(TYPE_OF_DISPATCHING.submitted, task);
	}

	public void dispatchTaskStarted(Task task) {
		dispatch(TYPE_OF_DISPATCHING.started, task);
	}

	public void dispatchTaskFinished(Task task) {
		dispatch(TYPE_OF_DISPATCHING.finished, task);
	}

	public void dispatchTaskPreempted(Task task) {
		dispatch(TYPE_OF_DISPATCHING.preempted, task);
	}

	private void dispatch(TYPE_OF_DISPATCHING type, Task task) {
		dispatch(type, new Event<Task>(task));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dispatch(Enum type, Event<Task> taskEvent) {
		for (TaskEventListener listener : listeners) {
			// submitted, started, preempted, finished
			if (listenerToFilter.get(listener).accept(taskEvent)) {
				if (type.equals(TYPE_OF_DISPATCHING.submitted)) {
					listener.taskSubmitted(taskEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.started)) {
					listener.taskStarted(taskEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.preempted)) {
					listener.taskPreempted(taskEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.finished)) {
					listener.taskFinished(taskEvent);
				} else {
					assert false;
				}
			}
		}
	}

}
