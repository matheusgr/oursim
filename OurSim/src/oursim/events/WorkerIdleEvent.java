package oursim.events;

import oursim.workerevents.WorkerEventDispatcher;

public class WorkerIdleEvent extends TimedEventAbstract<String> {

	WorkerIdleEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerIdle(this.content, this.time);
	}

}