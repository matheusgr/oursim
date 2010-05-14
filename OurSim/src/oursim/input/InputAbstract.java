package oursim.input;

import java.util.LinkedList;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class InputAbstract<T> implements Input<T> {

	protected LinkedList<T> inputs;

	public InputAbstract() {
		this.inputs = new LinkedList<T>();
		setUp();
	}

	protected abstract void setUp();

	public T peek() {
		return this.inputs.peek();
	}

	public T poll() {
		return this.inputs.poll();
	}

	public void close() {
		// nothing to do!
		throw new UnsupportedOperationException("Operantion not implemented yet");
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("inputs", inputs).toString();
	}

}
