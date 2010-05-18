package oursim.dispatchableevents.taskevents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oursim.dispatchableevents.EventDispatcher;
import oursim.entities.Task;

public class TaskEventDispatcher implements EventDispatcher {

	private enum TYPE_OF_DISPATCHING {
		submitted, started, preempted, finished
	};

	private static TaskEventDispatcher instance = null;

	private List<TaskEventListener> listeners;

	private Map<TaskEventListener, TaskEventFilter> listenerToFilter;

	private TaskEventDispatcher() {
		this.listeners = new ArrayList<TaskEventListener>();
		this.listenerToFilter = new HashMap<TaskEventListener, TaskEventFilter>();
	}

	public static TaskEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new TaskEventDispatcher();
	}

	public void addListener(TaskEventListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			this.listenerToFilter.put(listener, TaskEventFilter.ACCEPT_ALL);
		}
	}

	public void addListener(TaskEventListener listener, TaskEventFilter workerEventFilter) {
		addListener(listener);
		this.listenerToFilter.put(listener, workerEventFilter);
	}

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

	public void dispatch(TYPE_OF_DISPATCHING type, Task task) {
		TaskEvent taskEvent = new TaskEvent(task);
		for (TaskEventListener listener : listeners) {
			// submitted, started, preempted, finished
			if (listenerToFilter.get(listener).accept(taskEvent)) {
				switch (type) {
				case submitted:
					listener.taskSubmitted(taskEvent);
					break;
				case started:
					listener.taskStarted(taskEvent);
					break;
				case preempted:
					listener.taskPreempted(taskEvent);
					break;
				case finished:
					listener.taskFinished(taskEvent);
					break;
				default:
					break;
				}
			}
		}
	}

}
