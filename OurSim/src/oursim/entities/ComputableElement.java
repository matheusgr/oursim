package oursim.entities;

import java.util.List;

public interface ComputableElement {

	long getId();
	
	long getSubmissionTime();

	long getDuration();

	Long getStartTime();
	
	void setStartTime(long startTime);

	Long getEstimatedFinishTime();

	Long getFinishTime();

	Peer getSourcePeer();

	//TODO: estranha essa assinatura
	List<Peer> getTargetPeers();

	void setTargetPeer(Peer targetPeer);

	void preempt(long time);

	void finish(long time);

	long getNumberOfpreemptions();

	boolean isRunning();

	boolean isFinished();
	
	Long getMakeSpan();

	Long getRunningTime();

	Long getQueueingTime();

}
