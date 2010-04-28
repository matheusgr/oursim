package oursim.events;

import oursim.workerevents.WorkerEventDispatcher;

public class WorkerDownEvent extends TimedEventAbstract<String> {

	public WorkerDownEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerDown(this.content, this.time);
	}

}