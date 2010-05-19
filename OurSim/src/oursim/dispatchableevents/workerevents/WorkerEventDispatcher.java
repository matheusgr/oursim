package oursim.dispatchableevents.workerevents;

import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.EventDispatcher;

public class WorkerEventDispatcher extends EventDispatcher<String, WorkerEventListener, WorkerEventFilter> {

	private enum TYPE_OF_DISPATCHING {
		up, down, available, unavailable, idle, running
	};

	private static WorkerEventDispatcher instance = null;

	private WorkerEventDispatcher() {
		super();
	}

	public static WorkerEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new WorkerEventDispatcher();
	}

	@Override
	public void addListener(WorkerEventListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			this.listenerToFilter.put(listener, WorkerEventFilter.ACCEPT_ALL);
		} else {
			assert false;
		}
		assert listenerToFilter.get(listener) != null;
	}

	@Override
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

	private void dispatch(TYPE_OF_DISPATCHING type, String machineName, long time) {
		dispatch(type, new Event<String>(time, machineName));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dispatch(Enum type, Event<String> workerEvent) {
		for (WorkerEventListener listener : listeners) {
			// up, down, available, unavailable, idle, running
			if (listenerToFilter.get(listener).accept(workerEvent)) {
				if (type.equals(TYPE_OF_DISPATCHING.up)) {
					listener.workerUp(workerEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.down)) {
					listener.workerDown(workerEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.available)) {
					listener.workerAvailable(workerEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.unavailable)) {
					listener.workerUnavailable(workerEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.idle)) {
					listener.workerIdle(workerEvent);
				} else if (type.equals(TYPE_OF_DISPATCHING.running)) {
					listener.workerRunning(workerEvent);
				} else {
					assert false;
				}
			}
		}
	}
}
