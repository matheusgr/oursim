package oursim.dispatchableevents.jobevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventDispatcher;
import oursim.entities.Job;

public class JobEventDispatcher extends EventDispatcher<Job, JobEventListener, JobEventFilter> {

	private enum TYPE_OF_DISPATCHING {
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
	public void removeListener(JobEventListener listener) {
		this.listeners.remove(listener);
	}

	public void dispatchJobSubmitted(Job job) {
		dispatch(TYPE_OF_DISPATCHING.submitted, job);
	}

	public void dispatchJobStarted(Job job) {
		dispatch(TYPE_OF_DISPATCHING.started, job);
	}

	public void dispatchJobFinished(Job job) {
		dispatch(TYPE_OF_DISPATCHING.finished, job);
	}

	public void dispatchJobPreempted(Job job) {
		dispatch(TYPE_OF_DISPATCHING.preempted, job);
	}

	private void dispatch(TYPE_OF_DISPATCHING type, Job job) {
		dispatch(type, new Event<Job>(job));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dispatch(Enum type, Event<Job> jobEvent) {
		for (JobEventListener listener : listeners) {
			// submitted, started, preempted, finished
			if (listenerToFilter.get(listener).accept(jobEvent)) {
				if (type.equals(TYPE_OF_DISPATCHING.submitted)) {
					listener.jobSubmitted(jobEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.started)) {
					listener.jobStarted(jobEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.preempted)) {
					listener.jobPreempted(jobEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.finished)) {
					listener.jobFinished(jobEvent);
				} else {
					assert false;
				}
			}
		}
	}
}
