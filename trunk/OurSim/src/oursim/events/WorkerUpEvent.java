package oursim.events;

import oursim.workerevents.WorkerEventDispatcher;

public class WorkerUpEvent extends TimedEventAbstract<String> {

	public WorkerUpEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerUp(this.content, this.time);
	}

}