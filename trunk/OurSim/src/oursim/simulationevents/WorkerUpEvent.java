package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

public class WorkerUpEvent extends WorkerTimedEvent {

	WorkerUpEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerUp(this.content, this.time);
	}

}