package oursim.events;

import oursim.workerevents.WorkerEventDispatcher;

public class WorkerUnavailableEvent extends TimedEventAbstract<String> {

	public WorkerUnavailableEvent(long time, String machineName) {
		super(time, 2, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerUnAvailable(this.content, this.time);
	}

}