package br.edu.ufcg.lsd.gridsim.input;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import br.edu.ufcg.lsd.gridsim.Configuration;
import br.edu.ufcg.lsd.gridsim.Job;

public class SyntheticWorkload implements Workload {

    LinkedList<Job> jobs = new LinkedList<Job>();

    public SyntheticWorkload(int runTime, int runTimeVar, int submissionInterval, int numJobs, HashSet<String> peers) {
	
	int submissionTime = 0;
	ArrayList<String> peersL = new ArrayList<String>(peers);
	
	for (int jobId = 0; jobId < numJobs; jobId++) {
	    
	    submissionTime += Configuration.r.nextInt(submissionInterval);
	    
	    double magic = Math.abs(Configuration.r.nextGaussian());
	    magic *= peersL.size() / 3.0;
	    magic = magic > peersL.size() ? peersL.size() - 1 : magic;
	    
	    int randomPeer = (int) (magic);
	    int runTimeDuration = runTime + Configuration.r.nextInt(runTimeVar);
	    String sourcePeer = peersL.get(randomPeer);
	    jobs.add(new Job(jobId, submissionTime, runTimeDuration, sourcePeer));
	    
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
