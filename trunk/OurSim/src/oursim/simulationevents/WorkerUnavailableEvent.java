package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

/**
 * 
 * Event indicating that a worker becomes unavailable.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class WorkerUnavailableEvent extends WorkerTimedEvent {

	/**
	 * Creates an event indicating that a worker has become unavailable.
	 * 
	 * @param time
	 *            the time at which the machine has become unavailable.
	 * @param machineName
	 *            the name of the machine that has become unavailable.
	 */
	WorkerUnavailableEvent(long time, String machineName) {
		super(time, 2, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerUnavailable(this.content, this.time);
	}

}