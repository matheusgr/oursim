package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.EventListenerAdapter;

public class JobEventListenerAdapter implements JobEventListener, EventListenerAdapter {

	public void jobSubmitted(JobEvent jobEvent) {}

	public void jobStarted(JobEvent jobEvent) {}

	public void jobFinished(JobEvent jobEvent) {}

	public void jobPreempted(JobEvent jobEvent) {}

}
