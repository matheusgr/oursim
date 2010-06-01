package br.edu.ufcg.lsd.oursim.simulationevents;

/**
 * 
 * A default, convenient implementation of an {@link ActiveEntity}
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 01/06/2010
 * 
 */
public class ActiveEntityAbstract implements ActiveEntity {

	/**
	 * The event queue that will be processed.
	 */
	private EventQueue eventQueue;

	@Override
	public final void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	@Override
	public EventQueue getEventQueue() {
		return eventQueue;
	}

	@Override
	public long getCurrentTime() {
		return eventQueue.getCurrentTime();
	}

}
