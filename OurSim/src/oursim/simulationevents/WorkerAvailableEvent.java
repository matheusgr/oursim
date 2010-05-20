package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

/**
 * Event indicating that a worker becomes available.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class WorkerAvailableEvent extends WorkerTimedEvent {

	/**
	 * Creates an event indicating that a worker has become available.
	 * 
	 * @param time
	 *            the time at which the machine has become available.
	 * @param machineName
	 *            the name of the machine that has become available.
	 */
	WorkerAvailableEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerAvailable(this.content, this.time);
	}

}
