package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListenerAdapter;
import oursim.entities.Job;

/**
 * 
 * A default (empty) implementation of the listener.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public abstract class JobEventListenerAdapter implements JobEventListener, EventListenerAdapter {

	public void jobSubmitted(Event<Job> jobEvent) {
	}

	public void jobStarted(Event<Job> jobEvent) {
	}

	public void jobFinished(Event<Job> jobEvent) {
	}

	public void jobPreempted(Event<Job> jobEvent) {
	}

}
