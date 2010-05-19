package oursim.dispatchableevents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EventDispatcher<S, L, F> {

	protected final List<L> listeners;

	protected final Map<L, F> listenerToFilter;

	protected EventDispatcher() {
		this.listeners = new ArrayList<L>();
		this.listenerToFilter = new HashMap<L, F>();
	}

	public final void addListener(L listener, F filter) {
		addListener(listener);
		F oldValue = this.listenerToFilter.put(listener, filter);
		assert oldValue != null;
	}

	public abstract void addListener(L listener);

	public abstract void removeListener(L listener);

	@SuppressWarnings("unchecked")
	public abstract void dispatch(Enum type, Event<S> event);

}
