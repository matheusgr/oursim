package oursim.dispatchableevents.jobevents;

public interface JobEventFilter {

	JobEventFilter ACCEPT_ALL = new JobEventFilter() {

		@Override
		public boolean accept(JobEvent jobEvent) {
			return true;
		}

	};

	public boolean accept(JobEvent jobEvent);

}