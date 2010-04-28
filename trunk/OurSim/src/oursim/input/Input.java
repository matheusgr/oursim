package oursim.input;

public interface Input<T> {

	public abstract T peek();

	public abstract T poll();

	public abstract void close();

}
