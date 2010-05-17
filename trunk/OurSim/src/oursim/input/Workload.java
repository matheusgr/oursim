package oursim.input;

import java.io.FileNotFoundException;

import oursim.entities.Job;

public interface Workload extends Input<Job> {

	boolean merge(Workload other);

	//TODO: verificar a necessidade desse método
	void save(String fileName) throws FileNotFoundException;

}
