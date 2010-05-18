package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.EventFilter;

public interface TaskEventFilter extends EventFilter<TaskEvent> {

	TaskEventFilter ACCEPT_ALL = new TaskEventFilter() {

		@Override
		public boolean accept(TaskEvent taskEvent) {
			return true;
		}

	};

	public boolean accept(TaskEvent taskEvent);

}