package br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Task;

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
	public final void taskPreempted(Event<Task> taskEvent) {
		this.numberOfPreemptionsForAllTasks++;
	}

	public final int getNumberOfFinishedTasks() {
		assert numberOfFinishedTasks == idsOfFinishedTasks.size();
		return idsOfFinishedTasks.size();
	}

	public final int getNumberOfPreemptionsForAllTasks() {
		return numberOfPreemptionsForAllTasks;
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
	}

}