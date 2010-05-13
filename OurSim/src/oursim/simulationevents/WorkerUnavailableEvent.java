package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

public class WorkerUnavailableEvent extends WorkerTimedEvent {

	WorkerUnavailableEvent(long time, String machineName) {
		super(time, 2, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerUnavailable(this.content, this.time);
	}

}