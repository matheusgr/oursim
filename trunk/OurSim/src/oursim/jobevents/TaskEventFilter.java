package oursim.jobevents;

public interface TaskEventFilter {

	TaskEventFilter ACCEPT_ALL = new TaskEventFilter() {

		@Override
		public boolean accept(TaskEvent taskEvent) {
			return true;
		}

	};

	public boolean accept(TaskEvent taskEvent);

}