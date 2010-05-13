package oursim.simulationevents;

import oursim.entities.Job;

public abstract class JobTimedEvent extends TimedEventAbstract<Job> {

	JobTimedEvent(long time, int priority, Job job) {
		super(time, priority, job);
		this.content = job;
	}

}
