package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.EventFilter;

public interface JobEventFilter extends EventFilter<JobEvent> {

	JobEventFilter ACCEPT_ALL = new JobEventFilter() {

		@Override
		public boolean accept(JobEvent jobEvent) {
			return true;
		}

	};

	public boolean accept(JobEvent jobEvent);

}