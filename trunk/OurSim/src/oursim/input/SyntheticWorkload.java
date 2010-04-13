package oursim.input;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import oursim.entities.Job;
import oursim.entities.Peer;

public class SyntheticWorkload implements Workload {

    LinkedList<Job> jobs = new LinkedList<Job>();

    public SyntheticWorkload(int runTime, int runTimeVar, int submissionInterval, int numJobs, HashSet<Peer> peers) {
	
	int submissionTime = 0;
	Random r = new Random();
	ArrayList<Peer> peersL = new ArrayList<Peer>(peers);
	
	for (int jobId = 0; jobId < numJobs; jobId++) {
	    
	    submissionTime += r.nextInt(submissionInterval);
	    
	    double magic = Math.abs(r.nextGaussian());
	    magic *= peersL.size() / 3.0;
	    magic = magic > peersL.size() ? peersL.size() - 1 : magic;
	    
	    int randomPeer = (int) (magic);
	    int runTimeDuration = runTime + r.nextInt(runTimeVar);
	    Peer sourcePeer = peersL.get(randomPeer);
	    
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
