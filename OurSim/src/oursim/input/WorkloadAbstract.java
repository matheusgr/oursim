package oursim.input;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import oursim.entities.Job;

public abstract class WorkloadAbstract extends InputAbstract<Job> implements Workload {

	@Override
	public boolean merge(Workload other) {
		assert other.peek() != null;
		while (other.peek() != null) {
			this.inputs.addLast(other.poll());
		}
		return true;
	}

	@Override
	public void save(String fileName) throws FileNotFoundException {
		PrintStream out = new PrintStream(fileName);
		for (Job job : inputs) {
			out.printf("%s %s %s %s\n", job.getId(), job.getSourcePeer().getName(), job.getDuration(), job.getSubmissionTime());
		}
		out.close();
	}
	
}
