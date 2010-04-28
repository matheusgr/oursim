package oursim.jobevents;

import java.util.ArrayList;
import java.util.List;

import oursim.entities.Task;

public class TaskEventDispatcher {

	private static TaskEventDispatcher instance = null;

	private List<TaskEventListener> listeners;	
	
	private TaskEventDispatcher() {
		this.listeners = new ArrayList<TaskEventListener>();
	}

	public static TaskEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new TaskEventDispatcher();
	}
	
	public void addListener(TaskEventListener listener) {
		this.listeners.add(listener);
	}	
	
	public void removeListener(TaskEventListener listener) {
		this.listeners.remove(listener);
	}

	public void dispatchTaskFinished(Task task) {
		TaskEvent taskEvent = new TaskEvent(task);
		for (TaskEventListener listener : listeners) {
			listener.taskFinished(taskEvent);
		}
	}

	public void dispatchTaskSubmitted(Task task) {
		TaskEvent taskEvent = new TaskEvent(task);
		for (TaskEventListener listener : listeners) {
			listener.taskSubmitted(taskEvent);
		}
	}

	public void dispatchTaskStarted(Task task) {
		TaskEvent taskEvent = new TaskEvent(task);
		for (TaskEventListener listener : listeners) {
			listener.taskStarted(taskEvent);
		}
	}

	public void dispatchTaskPreempted(Task task) {
		TaskEvent taskEvent = new TaskEvent(task);
		for (TaskEventListener listener : listeners) {
			listener.taskPreempted(taskEvent);
		}
	}

}
