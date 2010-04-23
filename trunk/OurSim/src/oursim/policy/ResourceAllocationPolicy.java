package oursim.policy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.events.EventQueue;

public class ResourceAllocationPolicy {

	private int availableResources;

	private Peer peer;

	private ResourceSharingPolicy resourceSharingPolicy;

	private HashSet<Task> runningTasks;
	private HashSet<Task> runningLocalTasks;

	// a quantidade de recursos que o peer remoto está consumindo neste site
	protected HashMap<Peer, Integer> resourcesBeingConsumed;

	public ResourceAllocationPolicy(Peer peer, ResourceSharingPolicy resourceSharingPolicy) {

		this.peer = peer;

		this.availableResources = peer.getAmountOfResources();

		this.runningTasks = new HashSet<Task>();
		this.runningLocalTasks = new HashSet<Task>();

		this.resourcesBeingConsumed = new HashMap<Peer, Integer>();

		this.resourceSharingPolicy = resourceSharingPolicy;

		this.resourceSharingPolicy.addPeer(peer);

	}

	public int getAvailableResources() {
		return availableResources;
	}

	/**
	 * Only use resources that are busy by local jobs o total de recursos menos
	 * o que está sendo doado.
	 * 
	 * @return
	 */
	public long getAmountOfResourcesToShare() {
		return peer.getAmountOfResources() - runningLocalTasks.size();
		// TODO:TASK return peer.getAmountOfResources() -
		// runningLocalJobs.size();
	}

	public void finishTask(Task task, boolean preempted) {

		Peer sourcePeer = task.getSourceJob().getSourcePeer();

		if (sourcePeer == peer) {
			boolean removed = this.runningLocalTasks.remove(task);
			assert removed;
			// Don't compute own balance
		} else {
			boolean removed = this.runningTasks.remove(task);
			assert removed;

			int resourcesBeingConsumedByPeer = this.resourcesBeingConsumed.get(sourcePeer) - 1;
			if (resourcesBeingConsumedByPeer == 0) {
				this.resourcesBeingConsumed.remove(sourcePeer);
			} else {
				this.resourcesBeingConsumed.put(sourcePeer, resourcesBeingConsumedByPeer);
			}
			// compute balance
			if (!preempted) {
				resourceSharingPolicy.updateMutualBalance(peer, sourcePeer, task.getRunTimeDuration());
			} else {
				// TODO: This is not cool!
				EventQueue.getInstance().addPreemptedJobEvent(task.getSourceJob(), EventQueue.getInstance().currentTime());
			}
		}

		this.availableResources++;

	}

	protected void preemptOneTask(TreeMap<Peer, Integer> allowedResources) {

		Peer chosen = null;

		LinkedList<Peer> peerList = new LinkedList<Peer>(resourcesBeingConsumed.keySet());

		chosen = peerList.getLast();

		assert chosen != null;

		// todos os jobs do escolhido que estão rodando
		List<Task> tasks = new LinkedList<Task>();
		for (Task j : runningTasks) {
			if (j.getSourcePeer() == chosen) {
				tasks.add(j);
			}
		}

		// get recently started job first
		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
				// TODO cast promíscuo
				return (int) (t2.getStartTime() - t1.getStartTime());
			}
		});

		finishTask(tasks.get(0), true);

	}

	private void startTask(Task task) {
		availableResources--;
		Peer sourcePeer = task.getSourceJob().getSourcePeer();
		if (sourcePeer == peer) {
			runningLocalTasks.add(task);
		} else {
			runningTasks.add(task);
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

	public boolean allocateTask(Task task, Peer consumer) {
		// There is available resources.
		if (availableResources > 0) {
			startTask(task);
			return true;
		}

		// There are not remote resources that may be preempted
		if (peer.getAmountOfResourcesToShare() == 0) {
			return false;
		}

		// This task may need preemption
		TreeMap<Peer, Integer> preemptablePeers = resourceSharingPolicy.calculateAllowedResources(peer, consumer, resourcesBeingConsumed, runningTasks);

		// Consumer is not preemptable: so it can preempt someone.
		if (!preemptablePeers.containsKey(consumer)) {
			// Warning: Será que não pode ser preemptado um job do próprio
			// cara? (Não, não pode!)
			preemptOneTask(preemptablePeers);
			startTask(task);
			return true;
		}

		// Peer could not preempt someone
		return false;

	}

}
