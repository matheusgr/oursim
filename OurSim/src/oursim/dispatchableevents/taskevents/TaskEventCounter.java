package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.Event;
import oursim.entities.Task;

public class TaskEventCounter extends TaskEventListenerAdapter {

	private int numberOfFinishedTasks = 0;

	private int numberOfPreemptionsForAllTasks = 0;

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		this.numberOfFinishedTasks++;
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		this.numberOfPreemptionsForAllTasks++;
	}

	public int getNumberOfFinishedTasks() {
		return numberOfFinishedTasks;
	}

	public int getNumberOfPreemptionsForAllTasks() {
		return numberOfPreemptionsForAllTasks;
	}

}