package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListenerAdapter;

public class WorkerEventListenerAdapter implements WorkerEventListener, EventListenerAdapter {

	@Override
	public void workerAvailable(Event<String> workerEvent) {
	}

	@Override
	public void workerDown(Event<String> workerEvent) {
	}

	@Override
	public void workerIdle(Event<String> workerEvent) {
	}

	@Override
	public void workerRunning(Event<String> workerEvent) {
	}

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
	}

	@Override
	public void workerUp(Event<String> workerEvent) {
	}

}
