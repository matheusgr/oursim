package oursim.input;

import java.util.Collection;

import oursim.entities.Job;

public interface Workload extends Input<Job> {

	boolean merge(Workload other);

	Collection<? extends Job> clone();

}
