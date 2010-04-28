package oursim.events;

import oursim.entities.ComputableElement;

public abstract class ComputableElementTimedEvent extends TimedEventAbstract<ComputableElement> {

//	protected ComputableElement compElement;
//
//	public ComputableElementTimedEvent(long time, int priority) {
//		super(time, priority);
//	}
//
//	public ComputableElementTimedEvent(long time, ComputableElement compElement) {
//		super(time);
//		this.compElement = compElement;
//	}
//
	public ComputableElementTimedEvent(long time, int priority, ComputableElement compElement) {
		super(time, priority);
		this.content = compElement;
	}

}
