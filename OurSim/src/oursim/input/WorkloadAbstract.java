package oursim.input;

import oursim.entities.Job;

/**
 * 
 * An convenient Class to deal with generic workloads.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public abstract class WorkloadAbstract extends InputAbstract<Job> implements Workload {

	@Override
	public boolean merge(Workload other) {
		assert other.peek() != null;
		while (other.peek() != null) {
			this.inputs.addLast(other.poll());
		}
		return true;
	}

}