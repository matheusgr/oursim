package oursim.dispatchableevents.jobevents;

public class JobEventCounter extends JobEventListenerAdapter {

	private int numberOfFinishedJobs = 0;

	private int numberOfPreemptionsForAllJobs = 0;

	@Override
	public void jobFinished(JobEvent jobEvent) {
		this.numberOfFinishedJobs++;
	}

	@Override
	public void jobPreempted(JobEvent jobEvent) {
		this.numberOfPreemptionsForAllJobs++;
	}

	public int getNumberOfFinishedJobs() {
		return numberOfFinishedJobs;
	}

	public int getNumberOfPreemptionsForAllJobs() {
		return numberOfPreemptionsForAllJobs;
	}

}