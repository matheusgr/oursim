package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListenerAdapter;
import oursim.entities.Job;

public class JobEventListenerAdapter implements JobEventListener, EventListenerAdapter {

	public void jobSubmitted(Event<Job> jobEvent) {
	}

	public void jobStarted(Event<Job> jobEvent) {
	}

	public void jobFinished(Event<Job> jobEvent) {
	}

	public void jobPreempted(Event<Job> jobEvent) {
	}

}
