package oursim.output;

import java.io.Closeable;

import oursim.dispatchableevents.jobevents.JobEventListener;
import oursim.dispatchableevents.taskevents.TaskEventListener;

/**
 * 
 * The generic (Job based) output of a simulation.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public interface Output extends JobEventListener, TaskEventListener, Closeable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	void close();

}
