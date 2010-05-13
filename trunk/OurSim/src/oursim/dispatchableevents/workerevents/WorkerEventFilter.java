package oursim.dispatchableevents.workerevents;

public interface WorkerEventFilter {

	WorkerEventFilter ACCEPT_ALL = new WorkerEventFilter() {

		@Override
		public boolean accept(WorkerEvent workerEvent) {
			return true;
		}

	};

	public boolean accept(WorkerEvent workerEvent);

}