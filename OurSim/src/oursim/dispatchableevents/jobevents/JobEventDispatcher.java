package oursim.dispatchableevents.jobevents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oursim.dispatchableevents.EventDispatcher;
import oursim.entities.Job;

public class JobEventDispatcher implements EventDispatcher {

	private enum TYPE_OF_DISPATCHING {
		submitted, started, preempted, finished
	};

	private static JobEventDispatcher instance = null;

	private List<JobEventListener> listeners;

	private Map<JobEventListener, JobEventFilter> listenerToFilter;

	private JobEventDispatcher() {
		this.listeners = new ArrayList<JobEventListener>();
		this.listenerToFilter = new HashMap<JobEventListener, JobEventFilter>();
	}

	public static JobEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new JobEventDispatcher();
	}

	public void addListener(JobEventListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			this.listenerToFilter.put(listener, JobEventFilter.ACCEPT_ALL);
		}
	}

	public void addListener(JobEventListener listener, JobEventFilter workerEventFilter) {
		addListener(listener);
		this.listenerToFilter.put(listener, workerEventFilter);
	}

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

	public void dispatch(TYPE_OF_DISPATCHING type, Job job) {
		JobEvent jobEvent = new JobEvent(job);
		for (JobEventListener listener : listeners) {
			// submitted, started, preempted, finished
			if (listenerToFilter.get(listener).accept(jobEvent)) {
				switch (type) {
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
					break;
				}
			}
		}
	}

}
