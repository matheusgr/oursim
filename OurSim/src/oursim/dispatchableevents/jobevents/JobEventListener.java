package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListener;
import oursim.entities.Job;

/**
 * 
 * The listener interface for receiving job events. The class that is interested
 * in processing a job event either implements this interface (and all the
 * methods it contains) or extends the abstract {@link JobEventListenerAdapter}
 * class (overriding only the methods of interest). The listener object created
 * from that class is then registered with a {@link JobEventDispatcher} using
 * the dispatcher's {@link JobEventDispatcher#addListener(JobEventListener)}
 * method. When the job's status changes by virtue of being submitted, started,
 * preempted or finished, the relevant method in the listener object is invoked,
 * and the jobEvent is passed to it.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public interface JobEventListener extends EventListener {

	void jobSubmitted(Event<Job> jobEvent);

	void jobStarted(Event<Job> jobEvent);

	// TODO: qual a semântica de JobFinished?
	void jobFinished(Event<Job> jobEvent);

	void jobPreempted(Event<Job> jobEvent);

}
