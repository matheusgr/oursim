package oursim.simulationevents;


public abstract class WorkerTimedEvent extends TimedEventAbstract<String> {

	WorkerTimedEvent(long time, int priority, String machineName) {
		super(time, priority, machineName);
	}

}