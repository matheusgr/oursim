package br.edu.ufcg.lsd.gridsim.schedulers;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.edu.ufcg.lsd.gridsim.Configuration;
import br.edu.ufcg.lsd.gridsim.Job;
import br.edu.ufcg.lsd.gridsim.Peer;
import br.edu.ufcg.lsd.gridsim.events.FinishedJobEvent;
import br.edu.ufcg.lsd.gridsim.events.TimeQueue;
import br.edu.ufcg.lsd.gridsim.output.DefaultOutput;

public class NoFScheduler {

	public void schedule(TimeQueue timeQueue,
			List<Peer> peers, TreeSet<Job> jobs, HashMap<String, Peer> peersMap) {

		Iterator<Job> it = jobs.iterator();

		int originalSize = jobs.size();
		
		for (int i = 0; i < originalSize; i++) {
			final Job pqJob = it.next();
			if (pqJob.getJobId() == 618647) {
				System.err.println("SCHEDULLING!!!!!! " + pqJob.getRunTime());
			}
			// Never run MPI Jobs
			if (!pqJob.getType().equals("BOT") && !pqJob.getType().equals("SEQ")) {
				continue;
			}
			if (!Configuration.getInstance().useOGAsCluster() && !Configuration.getInstance().useGateway()
					&& pqJob.getSource().equals(Job.SOURCE_GLITE)) {
				continue;
			}
			String origSite = pqJob.getOrigSite();
			if (Configuration.getInstance().useOGAsCluster()) {
				origSite = "FAKE";
			}
			final Peer consumer = peersMap.get(origSite);

			// Getting best balance first
			Collections.sort(peers, new Comparator<Peer>() {
				@Override
				public int compare(Peer o1, Peer o2) {
					return o2.getBalance(consumer)
							- o1.getBalance(consumer);
				}

			});

			Set<Peer> consumers = new HashSet<Peer>();
			for (Job j : jobs) {
				origSite = j.getOrigSite();
				if (Configuration.getInstance().useOGAsCluster()) {
					origSite = "FAKE";
				}
				consumers.add(peersMap.get(origSite));
			}
			HashSet<Peer> newConsumers = new HashSet<Peer>(consumers);
			
			for (Peer provider : peers) {
				boolean runJob = provider.addOportunisticJob(pqJob, consumer, newConsumers, timeQueue.currentTime());
				if (runJob) {
		            DefaultOutput.getInstance().startJob(timeQueue.currentTime(), "OG", pqJob);
		            pqJob.setStartTime(timeQueue.currentTime());
		            pqJob.setPeer(provider);
		    		int wastedTime = pqJob.getStartTime() + pqJob.getRunTime();
		            if (Configuration.getInstance().checkpointEnabled()) {
		                wastedTime -= pqJob.getWastedTime();
		            }
		            if (wastedTime <= timeQueue.currentTime()) {
		            	System.err.println(wastedTime);
		            	System.err.println(pqJob);
		            	System.err.println(pqJob.getWastedTime());
		            	System.err.println(timeQueue.currentTime());
		            	System.err.println(pqJob.getPreemptions());
		            	assert false;
		            }
		            FinishedJobEvent finishedJobEvent = new FinishedJobEvent(
		                    wastedTime, pqJob);
		            timeQueue.addEvent(finishedJobEvent);
		            pqJob.setFinishedJobEvent(finishedJobEvent);
					it.remove();
					break;
				}
			}
		}
	}

}