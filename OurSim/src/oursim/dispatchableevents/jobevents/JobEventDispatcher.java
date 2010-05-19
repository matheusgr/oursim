package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventDispatcher;
import oursim.entities.Job;

/**
 * 
 * A dispatcher to the job's related events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 * @see {@link JobEventListener}
 * @see {@link JobEventFilter}
 * 
 */
public class JobEventDispatcher extends EventDispatcher<Job, JobEventListener, JobEventFilter> {

	/**
	 * 
	 * An enumeration of all the types of the events that could be dispatched by
	 * this dispatcher. For each type there is an method responsible to dispatch
	 * it. For example, to {@link TYPE_OF_DISPATCHING#submitted} there is
	 * {@link JobEventDispatcher#dispatchJobSubmitted(oursim.entities.Job)}
	 * 
	 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
	 * @since 19/05/2010
	 * 
	 */
	protected enum TYPE_OF_DISPATCHING {
		submitted, started, preempted, finished
	};

	private static JobEventDispatcher instance = null;

	private JobEventDispatcher() {
		super();
	}

	public static JobEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new JobEventDispatcher();
	}

	@Override
	public void addListener(JobEventListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			this.listenerToFilter.put(listener, JobEventFilter.ACCEPT_ALL);
		} else {
			assert false;
		}
	}

	@Override
	public boolean removeListener(JobEventListener listener) {
		return this.listeners.remove(listener);
	}

	/**
	 * @see {@link JobEventListener#jobSubmitted(Event)
	 * @param job
	 */
	public void dispatchJobSubmitted(Job job) {
		dispatch(TYPE_OF_DISPATCHING.submitted, job);
	}

	/**
	 * @see {@link JobEventListener#jobStarted(Event)
	 * @param job
	 */
	public void dispatchJobStarted(Job job) {
		dispatch(TYPE_OF_DISPATCHING.started, job);
	}

	/**
	 * @see {@link JobEventListener#jobFinished(Event)
	 * @param job
	 */
	public void dispatchJobFinished(Job job) {
		dispatch(TYPE_OF_DISPATCHING.finished, job);
	}

	/**
	 * @see {@link JobEventListener#jobPreempted(Event)}
	 * @param job
	 */
	public void dispatchJobPreempted(Job job) {
		dispatch(TYPE_OF_DISPATCHING.preempted, job);
	}

	private void dispatch(TYPE_OF_DISPATCHING type, Job job) {
		dispatch(type, new Event<Job>(job));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void dispatch(Enum type, Event<Job> jobEvent) {
		for (JobEventListener listener : listeners) {
			// submitted, started, preempted, finished
			if (listenerToFilter.get(listener).accept(jobEvent)) {
				switch ((TYPE_OF_DISPATCHING) type) {
				case submitted:
					listener.jobSubmitted(jobEvent);
					break;
				case started:
					listener.jobStarted(jobEvent);
					break;
				case preempted:
					listener.jobPreempted(jobEvent);
					break;
				case finished:
					listener.jobFinished(jobEvent);
					break;
				default:
					assert false;
				}
			}
		}
	}
}
