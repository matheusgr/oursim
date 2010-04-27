package oursim.jobevents;

import java.util.ArrayList;
import java.util.List;

import oursim.entities.ComputableElement;

public class ComputableElementEventDispatcher {

	private List<ComputableElementEventListener> listeners;

	private static ComputableElementEventDispatcher instance = null;

	private ComputableElementEventDispatcher() {
		this.listeners = new ArrayList<ComputableElementEventListener>();
	}

	public static ComputableElementEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new ComputableElementEventDispatcher();
	}

	public void addListener(ComputableElementEventListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(JobEventListener listener) {
		this.listeners.remove(listener);
	}

	public void dispatchFinished(ComputableElement computableElement) {
		ComputableElementEvent computableElementEvent = new ComputableElementEvent(computableElement);
		for (ComputableElementEventListener listener : listeners) {
			listener.finished(computableElementEvent);
		}
	}

	public void dispatchSubmitted(ComputableElement computableElement) {
		ComputableElementEvent computableElementEvent = new ComputableElementEvent(computableElement);
		for (ComputableElementEventListener listener : listeners) {
			listener.submitted(computableElementEvent);
		}
	}

	public void dispatchStarted(ComputableElement computableElement) {
		ComputableElementEvent computableElementEvent = new ComputableElementEvent(computableElement);
		for (ComputableElementEventListener listener : listeners) {
			listener.started(computableElementEvent);
		}
	}

	public void dispatchPreempted(ComputableElement computableElement) {
		ComputableElementEvent computableElementEvent = new ComputableElementEvent(computableElement);
		for (ComputableElementEventListener listener : listeners) {
			listener.preempted(computableElementEvent);
		}
	}

}
