package oursim.input;

import java.util.List;

import oursim.Parameters;
import oursim.entities.Job;
import oursim.entities.Peer;

public class SyntheticWorkload extends WorkloadAbstract {

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

			inputs.add(job);

		}

	}

	@Override
	public void setUp() {
		// nothing to do
	}

}
