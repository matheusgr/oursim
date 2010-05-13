package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

public class WorkerRunningEvent extends WorkerTimedEvent {

	WorkerRunningEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerRunning(this.content, this.time);
	}

}