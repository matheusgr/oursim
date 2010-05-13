package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

public class WorkerAvailableEvent extends WorkerTimedEvent {

	WorkerAvailableEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerAvailable(this.content, this.time);
	}

}
