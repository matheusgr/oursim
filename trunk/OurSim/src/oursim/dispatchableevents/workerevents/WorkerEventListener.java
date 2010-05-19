package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventListener;

/**
 * The listener interface for receiving worker events. The class that is
 * interested in processing a worker event either implements this interface (and
 * all the methods it contains) or extends the abstract
 * {@link WorkerEventListenerAdapter} class (overriding only the methods of
 * interest). The listener object created from that class is then registered
 * with a {@link JobEventDispatcher} using the dispatcher's
 * {@link WorkerEventDispatcher#addListener(WorkerEventListener)} method. When
 * the worker's status changes by virtue of being up, down, available,
 * unavailable, idle or running, the relevant method in the listener object is
 * invoked, and the workerEvent is passed to it.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public interface WorkerEventListener extends EventListener {

	void workerUp(Event<String> workerEvent);

	void workerDown(Event<String> workerEvent);

	void workerAvailable(Event<String> workerEvent);

	void workerUnavailable(Event<String> workerEvent);

	void workerIdle(Event<String> workerEvent);

	void workerRunning(Event<String> workerEvent);

}
