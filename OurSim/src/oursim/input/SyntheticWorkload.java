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

	public SyntheticWorkload(int runTime, int runTimeVar, int submissionInterval, int numJobs, int numTasksByJob, List<Peer> peers) {

		int submissionTime = 0;

		for (int jobId = 0; jobId < numJobs; jobId++) {

			submissionTime += Parameters.RANDOM.nextInt(submissionInterval);

			double peerIndexD = Math.abs(Parameters.RANDOM.nextGaussian());
			peerIndexD *= peers.size() / 3.0;
			peerIndexD = peerIndexD > peers.size() ? peers.size() - 1 : peerIndexD;

			int peerIndex = (int) (peerIndexD);
			int runTimeDuration = runTime + Parameters.RANDOM.nextInt(runTimeVar);
			Peer sourcePeer = peers.get(peerIndex);

			Job job = new Job(jobId, submissionTime, sourcePeer);

			for (int i = 0; i < numTasksByJob; i++) {
				job.addTask("", runTimeDuration);
			}

			jobs.add(job);

		}

	}

	@Override
	public void close() {
		// nothing to do
	}

	@Override
	public Job peek() {
		return jobs.peekFirst();
	}

	@Override
	public Job poll() {
		return jobs.pollFirst();
	}

	public void save(String fileName) throws FileNotFoundException {
		PrintStream out = new PrintStream(fileName);
		for (Job job : jobs) {
			out.printf("%s %s %s %s\n", job.getId(), job.getSourcePeer().getName(), job.getDuration(), job.getSubmissionTime());
		}
		out.close();
	}

}
