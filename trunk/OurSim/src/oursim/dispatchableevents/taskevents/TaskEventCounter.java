package oursim.dispatchableevents.taskevents;

public class TaskEventCounter extends TaskEventListenerAdapter {

	private int amountOfFinishedTasks = 0;

	private int numberOfPreemptionsForAllTasks = 0;

	@Override
	public void taskFinished(TaskEvent taskEvent) {
		this.amountOfFinishedTasks++;
	}

	@Override
	public void taskPreempted(TaskEvent jobEvent) {
		this.numberOfPreemptionsForAllTasks++;
	}

	public int getAmountOfFinishedTasks() {
		return amountOfFinishedTasks;
	}

	public int getNumberOfPreemptionsForAllTasks() {
		return numberOfPreemptionsForAllTasks;
	}

}