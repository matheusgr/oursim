package oursim.output;

import oursim.dispatchableevents.Event;
import oursim.entities.Job;
import oursim.entities.Task;

/**
 * 
 * A default (empty) implementation of {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public abstract class OutputAdapter implements Output {

	@Override
	public void jobFinished(Event<Job> jobEvent) {
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
	}

}
