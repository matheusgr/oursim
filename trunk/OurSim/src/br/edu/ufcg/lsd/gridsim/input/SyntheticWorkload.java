package br.edu.ufcg.lsd.gridsim.input;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import br.edu.ufcg.lsd.gridsim.Job;

public class SyntheticWorkload implements Workload {
	
	LinkedList<Job> jobs = new LinkedList<Job>();
	
	public SyntheticWorkload(int execTime, int execTimeVar, int submissionInterval, int numJobs, HashSet<String> peers) {
		int last = 0;
		Random r = new Random();
		ArrayList<String> peersL = new ArrayList<String>(peers);
		for (int i = 0; i < numJobs; i++) {
			last += r.nextInt(submissionInterval);
			jobs.add(new Job(i, last, execTime + r.nextInt(execTimeVar), 1, peersL.get(r.nextInt(peersL.size()))));
		}
	}
	
	@Override
	public void close() {
		// Nothing to do
	}

	@Override
	public Job peek() {
		return jobs.peekFirst();
	}

	@Override
	public Job poll() {
		return jobs.pollFirst();
	}

}
