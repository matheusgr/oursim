package oursim.policy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.EventQueue;

public class ResourceAllocationPolicy {

	private int availableResources;

	private Peer peer;

	private ResourceSharingPolicy resourceSharingPolicy;

	private HashSet<Job> runningJobs;
	private HashSet<Job> runningLocalJobs;

	// a quantidade de recursos que o peer remoto está consumindo neste site
	protected HashMap<Peer, Integer> resourcesBeingConsumed;

	public ResourceAllocationPolicy(Peer peer, ResourceSharingPolicy resourceSharingPolicy) {

		this.peer = peer;

		this.availableResources = peer.getAmountOfResources();

		this.runningJobs = new HashSet<Job>();
		this.runningLocalJobs = new HashSet<Job>();

		this.resourcesBeingConsumed = new HashMap<Peer, Integer>();

		this.resourceSharingPolicy = resourceSharingPolicy;

		this.resourceSharingPolicy.addPeer(peer);

	}

	public int getAvailableResources() {
		return availableResources;
	}

	public boolean allocateJob(Job job, Peer consumer) {

		// There is available resources.
		if (availableResources > 0) {
			startJob(job);
			return true;
		}

		// There are not remote resources that may be preempted
		if (peer.getAmountOfResourcesToShare() == 0) {
			return false;
		}

		// This job may need preemption
		TreeMap<Peer, Integer> preemptablePeers = resourceSharingPolicy.calculateAllowedResources(peer, consumer, resourcesBeingConsumed, runningJobs);

		// Consumer is not preemptable: so it can preempt someone.
		if (!preemptablePeers.containsKey(consumer)) {
			// Warning: Será que não pode ser preemptado um job do próprio
			// cara? (Não, não pode!)
			preemptOneJob(preemptablePeers);
			startJob(job);
			return true;
		}

		// Peer could not preempt someone
		return false;

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

			int resourcesBeingConsumedByPeer = this.resourcesBeingConsumed.get(sourcePeer) - 1;
			if (resourcesBeingConsumedByPeer == 0) {
				this.resourcesBeingConsumed.remove(sourcePeer);
			} else {
				this.resourcesBeingConsumed.put(sourcePeer, resourcesBeingConsumedByPeer);
			}
			// compute balance
			if (!preempted) {
				resourceSharingPolicy.updateMutualBalance(peer, sourcePeer, job.getRunTimeDuration());
			} else {
				EventQueue.getInstance().addPreemptedJobEvent(job, EventQueue.getInstance().currentTime());
			}
		}

		this.availableResources++;

	}

	protected void preemptOneJob(TreeMap<Peer, Integer> allowedResources) {

		Peer chosen = null;

		LinkedList<Peer> peerList = new LinkedList<Peer>(resourcesBeingConsumed.keySet());

		chosen = peerList.getLast();

		assert chosen != null;

		// todos os jobs do escolhido que estão rodando
		List<Job> jobs = new LinkedList<Job>();
		for (Job j : runningJobs) {
			if (j.getSourcePeer() == chosen) {
				jobs.add(j);
			}
		}

		// get recently started job first
		Collections.sort(jobs, new Comparator<Job>() {
			@Override
			public int compare(Job j1, Job j2) {
				// TODO cast promíscuo
				return (int) (j2.getStartTime() - j1.getStartTime());
			}
		});

		finishJob(jobs.get(0), true);

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
		if (this.resourcesBeingConsumed.containsKey(sourcePeer)) {
			consumedResources = this.resourcesBeingConsumed.get(sourcePeer);
		}
		this.resourcesBeingConsumed.put(sourcePeer, consumedResources + 1);
	}

}