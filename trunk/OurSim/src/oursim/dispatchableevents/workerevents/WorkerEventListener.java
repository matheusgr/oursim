package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListener;

public interface WorkerEventListener extends EventListener {

	void workerUp(Event<String> workerEvent);

	void workerDown(Event<String> workerEvent);

	void workerAvailable(Event<String> workerEvent);

	void workerUnavailable(Event<String> workerEvent);

	void workerIdle(Event<String> workerEvent);

	void workerRunning(Event<String> workerEvent);

}
