package oursim.simulationevents;

import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

/**
 * 
 * Event indicating that a worker becomes down.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class WorkerDownEvent extends WorkerTimedEvent {

	/**
	 * Creates an event indicating that a worker has become down.
	 * 
	 * @param time
	 *            the time at which the machine has become down.
	 * @param machineName
	 *            the name of the machine that has become down.
	 */
	WorkerDownEvent(long time, String machineName) {
		super(time, 1, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerDown(this.content, this.time);
	}

}