package oursim.policy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import oursim.Parameters;
import oursim.entities.Job;
import oursim.entities.Peer;

//TODO coisa relacionada ao Peer
public class ResourceAllocationPolicy {

    private int availableResources;
    private Peer peer;

    private ResourceSharingPolicy resourceSharingPolicy;

    private HashSet<Job> runningJobs;
    private HashSet<Job> runningLocalJobs;

    // a quantidade de recursos que o peer remoto está consumindo neste site
    protected HashMap<Peer, Integer> remoteConsumingPeers;

    public ResourceAllocationPolicy(Peer peer, ResourceSharingPolicy resourceSharingPolicy) {

	this.peer = peer;

	this.availableResources = peer.getAmountOfResources();

	this.runningJobs = new HashSet<Job>();
	this.runningLocalJobs = new HashSet<Job>();

	this.remoteConsumingPeers = new HashMap<Peer, Integer>();

	this.resourceSharingPolicy = resourceSharingPolicy;

	this.resourceSharingPolicy.addPeer(peer);

    }

    public boolean allocateJob(Job job, final Peer consumer, long time) {

	// There is available resources.
	if (availableResources > 0) {
	    startJob(job);
	    return true;
	} else if (runningLocalJobs.size() == peer.getAmountOfResources()) {
	    // Peer is full of local jobs
	    return false;
	} else {

	    // This job may need preemption
	    HashMap<Peer, Integer> allowedResources = resourceSharingPolicy.calculateAllowedResources(peer, consumer, remoteConsumingPeers, runningJobs);

	    int usedResources = remoteConsumingPeers.containsKey(consumer) ? remoteConsumingPeers.get(consumer) : 0;

	    if (consumer == peer || allowedResources.get(consumer) > usedResources) {
		// Warning: Será que não pode ser preemptado um job do próprio
		// cara? (Não, não pode!)
		preemptOneJob(allowedResources, time);
		startJob(job);
		return true;
	    }
	    return false;
	}
    }

    /**
     * Only use resources that are busy by local jobs o total de recursos menos
     * o que está sendo doado.
     * 
     * @return
     */
    public long getAmountOfResourcesToShare() {
	return peer.getAmountOfResources() - runningLocalJobs.size();
    }

    public void finishJob(Job job, boolean preempted) {

	Peer sourcePeer = job.getSourcePeer();

	if (sourcePeer == peer) {
	    boolean removed = this.runningLocalJobs.remove(job);
	    assert removed;
	    // Don't compute own balance
	} else {
	    boolean removed = this.runningJobs.remove(job);
	    assert removed;

	    int value = this.remoteConsumingPeers.get(sourcePeer) - 1;
	    if (value == 0) {
		this.remoteConsumingPeers.remove(sourcePeer);
	    } else {
		this.remoteConsumingPeers.put(sourcePeer, value);
	    }
	    // compute balance
	    if (!preempted) {
		resourceSharingPolicy.setBalance(peer, sourcePeer, -job.getRunTimeDuration());
		// Não aparenta ser um comportamento autônomo. Está setando o
		// balance do peer origem.
		resourceSharingPolicy.setBalance(sourcePeer, peer, job.getRunTimeDuration());
	    }
	}

	this.availableResources++;

    }

    protected void preemptOneJob(HashMap<Peer, Integer> allowedResources, long time) {

	Peer choosen = null;

	LinkedList<Peer> peerList = new LinkedList<Peer>(remoteConsumingPeers.keySet());
	
	//TODO: matheus solicitou a retirada deste shuffle
	Collections.shuffle(peerList, Parameters.RANDOM);

	// pega o que estiver usando mais do que merece
	for (Peer p : peerList) {
	    int usedResources = remoteConsumingPeers.get(p);
	    if (usedResources > allowedResources.get(p)) {
		choosen = p;
		break;
	    }
	}

	assert choosen != null;

	// todos os jobs do escolhido que estão rodando
	List<Job> jobs = new LinkedList<Job>();
	for (Job j : runningJobs) {
	    if (j.getSourcePeer() == choosen) {
		jobs.add(j);
	    }
	}

	// get recently start job first
	Collections.sort(jobs, new Comparator<Job>() {

	    @Override
	    public int compare(Job o1, Job o2) {
		// TODO cast promíscuo
		return (int) (o2.getStartTime() - o1.getStartTime());
	    }
	});
	Job j = jobs.get(0);
	j.setTargetPeer(null);
	finishJob(j, true);
	j.preempt(time);
	rescheduleJob(j);
    }

    private void rescheduleJob(Job j) {
	// GlobalScheduler.getInstance().schedule(j);
	// TODO: Gerar um evento de preempção de job
	// throw new UnsupportedOperationException("Operaçao ainda não
	// implementada.");
    }

    private void startJob(Job job) {
	availableResources--;
	Peer sourcePeer = job.getSourcePeer();
	if (sourcePeer == peer) {
	    runningLocalJobs.add(job);
	} else {
	    runningJobs.add(job);
	}

	if (sourcePeer == peer) {
	    return;
	}

	int consumedResources = 0;
	if (this.remoteConsumingPeers.containsKey(sourcePeer)) {
	    consumedResources = this.remoteConsumingPeers.get(sourcePeer);
	}
	this.remoteConsumingPeers.put(sourcePeer, consumedResources + 1);
    }

    public int getAvailableResources() {
	return availableResources;
    }

}
