package oursim.events;

import oursim.workerevents.WorkerEventDispatcher;

public class WorkerAvailableEvent extends TimedEventAbstract<String> {

	public WorkerAvailableEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerAvailable(this.content, this.time);
	}

}
