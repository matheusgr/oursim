package oursim.jobevents;

import java.util.EventListener;

public interface JobEventListener extends EventListener {

	void jobSubmitted(JobEvent jobEvent);

	void jobStarted(JobEvent jobEvent);

	// TODO: qual a semântica de JobFinished?
	void jobFinished(JobEvent jobEvent);

	void jobPreempted(JobEvent jobEvent);

}
