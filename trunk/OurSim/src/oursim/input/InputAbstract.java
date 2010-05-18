package oursim.input;

import java.util.LinkedList;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * An convenient Class to deal with generic imputs.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public abstract class InputAbstract<T> implements Input<T> {

	/**
	 * the memory managed collection of inputs.
	 */
	protected LinkedList<T> inputs;

	/**
	 * An ordinary constructor.
	 */
	public InputAbstract() {
		this.inputs = new LinkedList<T>();
		setUp();
	}

	/**
	 * An template method to be overrided by anonymous class. This method is
	 * always called when an InputAbstract is created.
	 */
	protected void setUp() {
		// to be overrided by inner class
	}

	@Override
	public T peek() {
		return this.inputs.peek();
	}

	@Override
	public T poll() {
		return this.inputs.poll();
	}

	@Override
	public void close() {
		// nothing to do!
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("inputs", inputs).toString();
	}

}
