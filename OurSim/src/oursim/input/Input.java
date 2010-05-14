package oursim.input;

public interface Input<T> {

	T peek();

	T poll();

	void close();
	
}
