package br.edu.ufcg.lsd.oursim.simulationevents;

public class ActiveEntityAbstract implements ActiveEntity {

	/**
	 * The event queue that will be processed.
	 */
	private EventQueue eventQueue;

	public final void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	public EventQueue getEventQueue() {
		return eventQueue;
	}

	@Override
	public long getCurrentTime() {
		return eventQueue.getCurrentTime();
	}

}
