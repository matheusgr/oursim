package oursim.dispatchableevents.taskevents;

public class TaskEventCounter extends TaskEventListenerAdapter {

	private int numberOfFinishedTasks = 0;

	private int numberOfPreemptionsForAllTasks = 0;

	@Override
	public void taskFinished(TaskEvent taskEvent) {
		this.numberOfFinishedTasks++;
	}

	@Override
	public void taskPreempted(TaskEvent jobEvent) {
		this.numberOfPreemptionsForAllTasks++;
	}

	public int getNumberOfFinishedTasks() {
		return numberOfFinishedTasks;
	}

	public int getNumberOfPreemptionsForAllTasks() {
		return numberOfPreemptionsForAllTasks;
	}

}