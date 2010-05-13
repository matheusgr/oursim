package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

public class WorkerIdleEvent extends WorkerTimedEvent {

	WorkerIdleEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerIdle(this.content, this.time);
	}

}