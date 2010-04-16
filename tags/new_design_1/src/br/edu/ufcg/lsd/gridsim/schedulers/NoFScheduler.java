package br.edu.ufcg.lsd.gridsim.schedulers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import br.edu.ufcg.lsd.gridsim.Configuration;
import br.edu.ufcg.lsd.gridsim.Job;
import br.edu.ufcg.lsd.gridsim.Peer;
import br.edu.ufcg.lsd.gridsim.events.FinishedJobEvent;
import br.edu.ufcg.lsd.gridsim.events.TimeQueue;
import br.edu.ufcg.lsd.gridsim.output.DefaultOutput;

public class NoFScheduler {

    public void schedule(TimeQueue timeQueue, List<Peer> peers, TreeSet<Job> jobs, HashMap<String, Peer> peersMap) {

	HashMap<Peer, HashSet<Peer>> triedPeers = new HashMap<Peer, HashSet<Peer>>(peersMap.size());

	Iterator<Job> it = jobs.iterator();

	int originalSize = jobs.size();

	for (int i = 0; i < originalSize; i++) {

	    final Job pqJob = it.next();
	    String origSite = pqJob.getOrigSite();

	    final Peer consumer = peersMap.get(origSite);

	    // TODO Request Policy

	    // // Getting best balance first
	    // Collections.sort(peers, new Comparator<Peer>() {
	    // @Override
	    // public int compare(Peer o1, Peer o2) {
	    // return o2.getBalance(consumer)
	    // - o1.getBalance(consumer);
	    // }
	    //
	    // });
	    Collections.shuffle(peers,Configuration.r);

	    for (Peer provider : peers) {
		HashSet<Peer> providersTried = triedPeers.get(consumer);
		if (providersTried != null && providersTried.contains(provider)) {
		    continue;
		}
		boolean runJob = provider.addOportunisticJob(pqJob, consumer, timeQueue.currentTime());
		if (runJob) {

		    // TODO Parte de responsabilidade do Scheduling Job. ALterar
		    // as
		    // estruturas do Job para dizer que está em execução.

		    DefaultOutput.getInstance().startJob(timeQueue.currentTime(), "OG", pqJob);
		    pqJob.setStartTime(timeQueue.currentTime());
		    pqJob.setPeer(provider);
		    int wastedTime = pqJob.getStartTime() + pqJob.getRunTime();
		    if (Configuration.getInstance().checkpointEnabled()) {
			wastedTime -= pqJob.getWastedTime();
		    }
		    assert wastedTime <= timeQueue.currentTime();
		    FinishedJobEvent finishedJobEvent = new FinishedJobEvent(wastedTime, pqJob);
		    timeQueue.addEvent(finishedJobEvent);
		    pqJob.setFinishedJobEvent(finishedJobEvent);
		    it.remove();
		    break;

		    // -----------------------------------

		} else {
		    if (providersTried == null) {
			providersTried = new HashSet<Peer>(peers.size());
			triedPeers.put(consumer, providersTried);
		    }
		    providersTried.add(provider);
		}
	    }
	}
    }

}