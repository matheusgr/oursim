package oursim.events;

import oursim.workerevents.WorkerEventDispatcher;

public class WorkerRunningEvent extends TimedEventAbstract<String> {

	public WorkerRunningEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerRunning(this.content, this.time);
	}

}