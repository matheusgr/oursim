package oursim.jobevents;

import oursim.entities.Job;

public class JobEvent extends CompElemEvent {

	private static final long serialVersionUID = 481672427365120073L;

	public JobEvent(Job source) {
		super(source);
	}

	public JobEvent(long time, Job source) {
		super(time, source);
	}

}
