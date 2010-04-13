package oursim.input;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import oursim.entities.Job;
import oursim.entities.Peer;

public class SyntheticWorkload implements Workload {

    LinkedList<Job> jobs = new LinkedList<Job>();

    public SyntheticWorkload(int runTime, int runTimeVar, int submissionInterval, int numJobs, List<Peer> peers) {
	
	int submissionTime = 0;
	Random r = new Random();
	
	for (int jobId = 0; jobId < numJobs; jobId++) {
	    
	    submissionTime += r.nextInt(submissionInterval);
	    
	    double magic = Math.abs(r.nextGaussian());
	    magic *= peers.size() / 3.0;
	    magic = magic > peers.size() ? peers.size() - 1 : magic;
	    
	    int randomPeer = (int) (magic);
	    int runTimeDuration = runTime + r.nextInt(runTimeVar);
	    Peer sourcePeer = peers.get(randomPeer);
	    
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
