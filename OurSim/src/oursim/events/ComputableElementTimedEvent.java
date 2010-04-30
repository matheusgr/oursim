package oursim.events;

import oursim.entities.ComputableElement;

public abstract class ComputableElementTimedEvent extends TimedEventAbstract<ComputableElement> {

	public ComputableElementTimedEvent(long time, int priority, ComputableElement compElement) {
		super(time, priority);
		this.content = compElement;
	}

}
