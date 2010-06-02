package br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;

public class JobEventCounter extends JobEventListenerAdapter {

	private int numberOfFinishedJobs = 0;

	private int numberOfPreemptionsForAllJobs = 0;

	private Set<Long> idsOfFinishedJobs = new HashSet<Long>();

	@Override
	public final void jobFinished(Event<Job> jobEvent) {
		this.numberOfFinishedJobs++;
		idsOfFinishedJobs.add(jobEvent.getSource().getId());
	}

	@Override
	public final void jobPreempted(Event<Job> jobEvent) {
		this.numberOfPreemptionsForAllJobs++;
	}

	public final int getNumberOfFinishedJobs() {
		assert numberOfFinishedJobs == idsOfFinishedJobs.size();
		return numberOfFinishedJobs;
	}

	public final int getNumberOfPreemptionsForAllJobs() {
		return numberOfPreemptionsForAllJobs;
	}

}