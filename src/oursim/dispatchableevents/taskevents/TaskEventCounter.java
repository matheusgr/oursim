package oursim.dispatchableevents.taskevents;

import java.util.HashSet;
import java.util.Set;

import oursim.dispatchableevents.Event;
import oursim.entities.Task;

public class TaskEventCounter extends TaskEventListenerAdapter {

	private int numberOfFinishedTasks = 0;

	private int numberOfPreemptionsForAllTasks = 0;
	
	private Set<Long> idsOfFinishedTasks = new HashSet<Long>();

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		this.numberOfFinishedTasks++;
		idsOfFinishedTasks.add(taskEvent.getSource().getId());
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		this.numberOfPreemptionsForAllTasks++;
	}

	public int getNumberOfFinishedTasks() {
//		return numberOfFinishedTasks;
		return idsOfFinishedTasks.size();
	}

	public int getNumberOfPreemptionsForAllTasks() {
		return numberOfPreemptionsForAllTasks;
	}

}