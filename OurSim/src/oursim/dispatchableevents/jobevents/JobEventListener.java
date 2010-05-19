package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListener;
import oursim.entities.Job;

public interface JobEventListener extends EventListener {

	void jobSubmitted(Event<Job> jobEvent);

	void jobStarted(Event<Job> jobEvent);

	// TODO: qual a semântica de JobFinished?
	void jobFinished(Event<Job> jobEvent);

	void jobPreempted(Event<Job> jobEvent);

}
