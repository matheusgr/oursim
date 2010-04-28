package oursim.workerevents;

import java.util.ArrayList;
import java.util.List;

import oursim.events.WorkerAvailableEvent;

public class WorkerEventDispatcher {

	private static WorkerEventDispatcher instance = null;

	private List<WorkerEventListener> listeners;

	private WorkerEventDispatcher() {
		this.listeners = new ArrayList<WorkerEventListener>();
	}

	public static WorkerEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new WorkerEventDispatcher();
	}

	public void addListener(WorkerEventListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(WorkerEventListener listener) {
		this.listeners.remove(listener);
	}

	public void dispatchWorkerUp(String machineName, long time) {
		WorkerEvent workerjobEvent = new WorkerEvent(time, machineName);
		for (WorkerEventListener listener : listeners) {
			listener.workerUp(workerjobEvent);
		}
	}

	public void dispatchWorkerDown(String machineName, long time) {
		WorkerEvent workerEvent = new WorkerEvent(time, machineName);
		for (WorkerEventListener listener : listeners) {
			listener.workerDown(workerEvent);
		}
	}

	public void dispatchWorkerAvailable(String machineName, long time) {
		WorkerEvent workerEvent = new WorkerEvent(time, machineName);
		for (WorkerEventListener listener : listeners) {
			listener.workerAvailable(workerEvent);
		}
	}

	public void dispatchWorkerUnAvailable(String machineName, long time) {
		WorkerEvent workerEvent = new WorkerEvent(time, machineName);
		for (WorkerEventListener listener : listeners) {
			listener.workerUnavailable(workerEvent);
		}
	}

	public void dispatchWorkerIdle(String machineName, long time) {
		WorkerEvent workerEvent = new WorkerEvent(time, machineName);
		for (WorkerEventListener listener : listeners) {
			listener.workerIdle(workerEvent);
		}
	}

	public void dispatchWorkerRunning(String machineName, long time) {
		WorkerEvent workerEvent = new WorkerEvent(time, machineName);
		for (WorkerEventListener listener : listeners) {
			listener.workerRunning(workerEvent);
		}
	}

}
