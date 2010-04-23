package oursim.entities;

public interface ComputableElement extends Comparable<ComputableElement> {

	long getId();
	
	long getSubmissionTime();

	long getRunTimeDuration();

	long getStartTime();

	void setStartTime(long startTime);

	Peer getSourcePeer();

	Peer getTargetPeer();

	void setTargetPeer(Peer targetPeer);

	void preempt(long time);

	long getEstimatedFinishTime();

	long getFinishTime();

	int getNumberOfpreemptions();

}
