package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

/**
 * 
 * 
 * Event indicating that a worker becomes idle.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class WorkerIdleEvent extends WorkerTimedEvent {

	/**
	 * Creates an event indicating that a worker has become idle.
	 * 
	 * @param time
	 *            the time at which the machine has become idle.
	 * @param machineName
	 *            the name of the machine that has become idle.
	 */
	WorkerIdleEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerIdle(this.content, this.time);
	}

}