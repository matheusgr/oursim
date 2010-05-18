package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.EventFilter;

public interface WorkerEventFilter extends EventFilter<WorkerEvent> {

	WorkerEventFilter ACCEPT_ALL = new WorkerEventFilter() {

		@Override
		public boolean accept(WorkerEvent workerEvent) {
			return true;
		}

	};

	public boolean accept(WorkerEvent workerEvent);

}