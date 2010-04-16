package br.edu.ufcg.lsd.gridsim.input;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.gridsim.Configuration;
import br.edu.ufcg.lsd.gridsim.Job;

public class SyntheticWorkload implements Workload {

    LinkedList<Job> jobs = new LinkedList<Job>();

    public SyntheticWorkload(int runTime, int runTimeVar, int submissionInterval, int numJobs, List<String> peers) {
	
	int submissionTime = 0;
	
	for (int jobId = 0; jobId < numJobs; jobId++) {
	    
	    submissionTime += Configuration.r.nextInt(submissionInterval);
	    
	    double magic = Math.abs(Configuration.r.nextGaussian());
	    magic *= peers.size() / 3.0;
	    magic = magic > peers.size() ? peers.size() - 1 : magic;
	    
	    int randomPeer = (int) (magic);
	    int runTimeDuration = runTime + Configuration.r.nextInt(runTimeVar);
	    String sourcePeer = peers.get(randomPeer);
	    jobs.add(new Job(jobId, submissionTime, runTimeDuration, sourcePeer));
	    
	}
	
    }

    @Override
    public void close() {
	try {
	    PrintStream out = new PrintStream("workload_simulation.txt");
	    for (Job job : jobs) {
		out.printf("%s %s %s %s\n",job.getJobId(), job.getOrigSite(), job.getRunTime(), job.getSubmitTime());
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
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
