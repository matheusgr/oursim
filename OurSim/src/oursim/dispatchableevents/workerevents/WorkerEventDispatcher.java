package oursim.dispatchableevents.workerevents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerEventDispatcher {

	private enum TYPE_OF_DISPATCHING {
		up, down, available, unavailable, idle, running
	};

	private static WorkerEventDispatcher instance = null;

	private List<WorkerEventListener> listeners;

	private Map<WorkerEventListener, WorkerEventFilter> listenerToFilter;

	private WorkerEventDispatcher() {
		this.listeners = new ArrayList<WorkerEventListener>();
		this.listenerToFilter = new HashMap<WorkerEventListener, WorkerEventFilter>();
	}

	public static WorkerEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new WorkerEventDispatcher();
	}

	public void addListener(WorkerEventListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			this.listenerToFilter.put(listener, WorkerEventFilter.ACCEPT_ALL);
		}
	}

	public void addListener(WorkerEventListener listener, WorkerEventFilter workerEventFilter) {
		addListener(listener);
		this.listenerToFilter.put(listener, workerEventFilter);
	}

	public void removeListener(WorkerEventListener listener) {
		this.listeners.remove(listener);
	}

	public void dispatchWorkerUp(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.up, machineName, time);
	}

	public void dispatchWorkerDown(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.down, machineName, time);
	}

	public void dispatchWorkerAvailable(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.available, machineName, time);
	}

	public void dispatchWorkerUnavailable(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.unavailable, machineName, time);
	}

	public void dispatchWorkerIdle(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.idle, machineName, time);
	}

	public void dispatchWorkerRunning(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.running, machineName, time);
	}

	public void dispatch(TYPE_OF_DISPATCHING type, String machineName, long time) {
		WorkerEvent workerEvent = new WorkerEvent(time, machineName);
		for (WorkerEventListener listener : listeners) {
			// up, down, available, unavailable, idle, running
			if (listenerToFilter.get(listener).accept(workerEvent)) {
				switch (type) {
				case up:
					listener.workerUp(workerEvent);
					break;
				case down:
					listener.workerDown(workerEvent);
					break;
				case available:
					listener.workerAvailable(workerEvent);
					break;
				case unavailable:
					listener.workerUnavailable(workerEvent);
					break;
				case idle:
					listener.workerIdle(workerEvent);
					break;
				case running:
					listener.workerRunning(workerEvent);
					break;
				default:
					break;
				}
			}
		}
	}

}
