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
		//XXX o id não é único para todas as tasks. A chave única deve considerar task e ID.
		idsOfFinishedTasks.add(taskEvent.getSource().getId());
	}

	@Override
	public final void taskPreempted(Event<Task> taskEvent) {
		this.numberOfPreemptionsForAllTasks++;
	}

	public final int getNumberOfFinishedTasks() {
		return numberOfFinishedTasks;
	}

	public final int getNumberOfPreemptionsForAllTasks() {
		return numberOfPreemptionsForAllTasks;
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
	}

}