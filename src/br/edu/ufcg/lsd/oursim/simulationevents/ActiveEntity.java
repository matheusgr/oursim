package br.edu.ufcg.lsd.oursim.simulationevents;

public interface ActiveEntity {
	
	void setEventQueue(EventQueue eventQueue);

	EventQueue getEventQueue();
	
	long getCurrentTime();

}