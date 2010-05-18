package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.EventListener;

public interface WorkerEventListener extends EventListener {

	void workerUp(WorkerEvent workerEvent);

	void workerDown(WorkerEvent workerEvent);

	void workerAvailable(WorkerEvent workerEvent);

	void workerUnavailable(WorkerEvent workerEvent);

	void workerIdle(WorkerEvent workerEvent);

	void workerRunning(WorkerEvent workerEvent);

}
