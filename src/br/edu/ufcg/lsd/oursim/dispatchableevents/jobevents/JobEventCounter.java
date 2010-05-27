package br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;

public class JobEventCounter extends JobEventListenerAdapter {

	private int numberOfFinishedJobs = 0;

	private int numberOfPreemptionsForAllJobs = 0;

	@Override
	public void jobFinished(Event<Job> jobEvent) {
		this.numberOfFinishedJobs++;
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
		this.numberOfPreemptionsForAllJobs++;
	}

	public int getNumberOfFinishedJobs() {
		return numberOfFinishedJobs;
	}

	public int getNumberOfPreemptionsForAllJobs() {
		return numberOfPreemptionsForAllJobs;
	}

}