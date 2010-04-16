package oursim.input;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import oursim.Parameters;
import oursim.entities.Job;
import oursim.entities.Peer;

public class SyntheticWorkload implements Workload {

    private LinkedList<Job> jobs = new LinkedList<Job>();

    public SyntheticWorkload(int runTime, int runTimeVar, int submissionInterval, int numJobs, List<Peer> peers) {
	
	int submissionTime = 0;
	
	for (int jobId = 0; jobId < numJobs; jobId++) {
	    
	    submissionTime += Parameters.RANDOM.nextInt(submissionInterval);
	    
	    double magic = Math.abs(Parameters.RANDOM.nextGaussian());
	    magic *= peers.size() / 3.0;
	    magic = magic > peers.size() ? peers.size() - 1 : magic;
	    
	    int randomPeer = (int) (magic);
	    int runTimeDuration = runTime + Parameters.RANDOM.nextInt(runTimeVar);
	    Peer sourcePeer = peers.get(randomPeer);
	    
	    jobs.add(new Job(jobId, submissionTime, runTimeDuration, sourcePeer));
	    
	}
	
    }

    @Override
    public void close() {
	try {
	    PrintStream out = new PrintStream("workload_oursim.txt");
	    for (Job job : jobs) {
		out.printf("%s %s %s %s\n",job.getId(), job.getSourcePeer().getName(), job.getRunTimeDuration(), job.getSubmissionTime());
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
