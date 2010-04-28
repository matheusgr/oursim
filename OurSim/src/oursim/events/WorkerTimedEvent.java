package oursim.events;


public abstract class WorkerTimedEvent extends TimedEventAbstract<String> {

	public WorkerTimedEvent(long time, int priority, String machineName) {
		super(time, priority, machineName);
	}

}
