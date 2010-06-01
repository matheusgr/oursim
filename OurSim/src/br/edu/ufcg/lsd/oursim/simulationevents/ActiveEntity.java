package br.edu.ufcg.lsd.oursim.simulationevents;

/**
 * 
 * This is a convenient interface to state all the entity that might alter the
 * eventQueue or want to know what is the current time of the simulation. This
 * interface should be used carefully and only by entities that really need it.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 01/06/2010
 * 
 */
public interface ActiveEntity {

	/**
	 * Set in this entity the event queue. All the entitys must share the same
	 * instance of the eventqueue to maintain consistency.
	 * 
	 * @param eventQueue
	 *            the eventqueue to be shared.
	 */
	void setEventQueue(EventQueue eventQueue);

	/**
	 * @return the active eventqueue.
	 */
	EventQueue getEventQueue();

	long getCurrentTime();

}