package oursim.events;

import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class TimedEventAbstract<T> extends TimedEvent {

	protected T content;

	TimedEventAbstract(long time, int priority) {
		super(time, priority);
	}

	TimedEventAbstract(long time, T content) {
		super(time);
		this.content = content;
	}

	TimedEventAbstract(long time, int priority, T content) {
		super(time, priority);
		this.content = content;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("time", time).append("content", content).toString();
	}

}