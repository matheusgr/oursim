package oursim.dispatchableevents;

public interface EventFilter<T extends Event> {

	public boolean accept(T event);

}
