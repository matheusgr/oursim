package oursim.input;

import oursim.entities.Job;

public interface Workload extends Input<Job> {

	boolean merge(Workload other);

}
