package oursim.events;

import oursim.entities.Job;

public abstract class JobTimedEvent extends ComputableElementTimedEvent {

	JobTimedEvent(long time, int priority, Job job) {
		super(time, priority, job);

	}

}