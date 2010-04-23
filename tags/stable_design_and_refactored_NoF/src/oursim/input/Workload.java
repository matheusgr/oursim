package oursim.input;

import oursim.entities.Job;

public interface Workload {

	public abstract Job peek();

	public abstract Job poll();

	public abstract void close();

}
