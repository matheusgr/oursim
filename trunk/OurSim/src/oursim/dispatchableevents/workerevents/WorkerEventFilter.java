package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventFilter;

/**
 * 
 * The filter that determines which events related to workers the listener wants
 * to be notified.
 * 
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public interface WorkerEventFilter extends EventFilter<Event<String>> {

	/**
	 * A lenient WorkerEventFilter that accepts all events.
	 */
	WorkerEventFilter ACCEPT_ALL = new WorkerEventFilter() {

		@Override
		public boolean accept(Event<String> workerEvent) {
			return true;
		}

	};

	@Override
	public boolean accept(Event<String> workerEvent);

}