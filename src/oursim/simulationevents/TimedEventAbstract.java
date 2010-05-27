package oursim.simulationevents;

import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * An convenient class to all events that hold some content.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 * @param <T>
 *            The type of the content holded by the event.
 */
public abstract class TimedEventAbstract<T> extends TimedEvent {

	/**
	 * the content holded by the event.
	 */
	protected T content;

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 */
	TimedEventAbstract(long time, int priority) {
		super(time, priority);
	}

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param content
	 *            the content holded by the event.
	 */
	TimedEventAbstract(long time, T content) {
		super(time);
		this.content = content;
	}

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 * @param content
	 *            the content holded by the event.
	 */
	TimedEventAbstract(long time, int priority, T content) {
		super(time, priority);
		this.content = content;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("time", time).append("content", content).toString();
	}

}