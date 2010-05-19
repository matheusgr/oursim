package oursim.dispatchableevents.taskevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventFilter;
import oursim.entities.Task;

public interface TaskEventFilter extends EventFilter<Event<Task>> {

	TaskEventFilter ACCEPT_ALL = new TaskEventFilter() {

		@Override
		public boolean accept(Event<Task> taskEvent) {
			return true;
		}

	};

	public boolean accept(Event<Task> taskEvent);

}