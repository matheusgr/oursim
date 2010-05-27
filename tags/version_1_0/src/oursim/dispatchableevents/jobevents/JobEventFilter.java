package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventFilter;
import oursim.entities.Job;

/**
 * 
 * The filter that determines which events related to jobs the listener wants to
 * be notified.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public interface JobEventFilter extends EventFilter<Event<Job>> {

	/**
	 * A lenient JobEventFilter that accepts all events.
	 */
	JobEventFilter ACCEPT_ALL = new JobEventFilter() {

		@Override
		public boolean accept(Event<Job> jobEvent) {
			return true;
		}

	};

	@Override
	public boolean accept(Event<Job> jobEvent);

}