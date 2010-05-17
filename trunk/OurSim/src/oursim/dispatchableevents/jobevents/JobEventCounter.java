package oursim.dispatchableevents.jobevents;

public class JobEventCounter extends JobEventListenerAdapter {

	private int amountOfFinishedJobs = 0;

	private int numberOfPreemptionsForAllJobs = 0;

	@Override
	public void jobFinished(JobEvent jobEvent) {
		this.amountOfFinishedJobs++;
	}

	@Override
	public void jobPreempted(JobEvent jobEvent) {
		this.numberOfPreemptionsForAllJobs++;
	}

	public int getAmountOfFinishedJobs() {
		return amountOfFinishedJobs;
	}

	public int getNumberOfPreemptionsForAllJobs() {
		return numberOfPreemptionsForAllJobs;
	}

}