package oursim.workerevents;


public interface WorkerEventListener {

	void workerUp(WorkerEvent workerEvent);

	void workerDown(WorkerEvent workerEvent);

	void workerAvailable(WorkerEvent workerEvent);

	void workerUnavailable(WorkerEvent workerEvent);

	void workerIdle(WorkerEvent workerEvent);

	void workerRunning(WorkerEvent workerEvent);

}
