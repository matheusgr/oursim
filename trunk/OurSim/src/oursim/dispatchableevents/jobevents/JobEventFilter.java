package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventFilter;
import oursim.entities.Job;

public interface JobEventFilter extends EventFilter<Event<Job>> {

	JobEventFilter ACCEPT_ALL = new JobEventFilter() {

		@Override
		public boolean accept(Event<Job> jobEvent) {
			return true;
		}

	};

	public boolean accept(Event<Job> jobEvent);

}