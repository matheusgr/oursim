package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventFilter;

public interface WorkerEventFilter extends EventFilter<Event<String>> {

	WorkerEventFilter ACCEPT_ALL = new WorkerEventFilter() {

		@Override
		public boolean accept(Event<String> workerEvent) {
			return true;
		}

	};

	public boolean accept(Event<String> workerEvent);

}