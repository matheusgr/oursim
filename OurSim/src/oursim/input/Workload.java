package oursim.input;

import oursim.entities.Job;

/**
 * 
 * An collection of jobs intended to be used to generated events of job's
 * submissions.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public interface Workload extends Input<Job> {

	/**
	 * Performs an merge of this workload with another one. The other workload
	 * will be invalidated after the calling of this operation.
	 * 
	 * @param other
	 *            the workload with which this is going to be merged.
	 * @return <code>true</code> if the merge was successfully performed,
	 *         <code>false</code> otherwise.
	 */
	boolean merge(Workload other);

}
