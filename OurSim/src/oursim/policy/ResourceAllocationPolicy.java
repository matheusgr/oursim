package oursim.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.entities.Task;

public class ResourceAllocationPolicy {

	private Peer peer;

	private ResourceManager resourceManager;

	private TaskManager taskManager;

	/**
	 * The number of resources that each foreign peer is consuming in this site.
	 * 
	 * numberOfAllocatedResourcesByPeer
	 */
	private Map<Peer, Integer> numOfAllocResByPeer;

	public ResourceAllocationPolicy(Peer peer, ResourceManager resourceManager, TaskManager taskManager) {

		this.peer = peer;

		this.resourceManager = resourceManager;

		this.taskManager = taskManager;

		this.numOfAllocResByPeer = new HashMap<Peer, Integer>();

	}

	public Machine allocateTask(Task task) {
		Machine resource = this.resourceManager.hasAvailableResource() ? this.resourceManager.allocateResourceToTask(task) : forceAPreemptionOnBehalfOf(task);

		if (resource != null) {
			this.taskManager.addTask(task, resource);
			increaseAccounting(task);
		}

		return resource;

	}

	public boolean deallocateTask(Task task) {
		this.resourceManager.releaseResource(this.taskManager.finishTask(task));
		decreaseAccounting(task);
		return true;
	}

	private Machine forceAPreemptionOnBehalfOf(Task task) {

		Machine releasedResource = null;

		Peer consumer = task.getSourcePeer();
		List<Peer> preemptablePeers = peer.prioritizePeersToPreemptionOnBehalfOf(consumer);

		if (!preemptablePeers.isEmpty()) {
			Task doomed = chooseATaskToBePreempted(preemptablePeers);
			releasedResource = this.taskManager.getMachine(doomed);
			this.peer.preemptTask(doomed);
		}

		return releasedResource;
	}

	private Task chooseATaskToBePreempted(List<Peer> preemptablePeers) {
		assert !preemptablePeers.isEmpty();

		Peer chosen = preemptablePeers.get(preemptablePeers.size() - 1);
		List<Task> tasks = this.taskManager.getAllTasksFromPeer(chosen);

		this.peer.prioritizeTasksToPreemption(tasks);
		return tasks.get(0);

	}

	private void increaseAccounting(Task task) {
		Peer sourcePeer = task.getSourcePeer();
		if (sourcePeer != peer) {
			int consumedResources = this.numOfAllocResByPeer.containsKey(sourcePeer) ? this.numOfAllocResByPeer.get(sourcePeer) : 0;
			this.numOfAllocResByPeer.put(sourcePeer, consumedResources + 1);
		}
	}

	private void decreaseAccounting(Task task) {
		Peer sourcePeer = task.getSourcePeer();
		if (sourcePeer != peer) {
			int resourcesBeingConsumedByPeer = this.numOfAllocResByPeer.get(sourcePeer) - 1;
			if (resourcesBeingConsumedByPeer == 0) {
				this.numOfAllocResByPeer.remove(sourcePeer);
			} else {
				this.numOfAllocResByPeer.put(sourcePeer, resourcesBeingConsumedByPeer);
			}
		}
	}

	public Set<Peer> getForeignConsumingPeers() {
		return this.numOfAllocResByPeer.keySet();
	}

	public Map<Peer, Integer> getNumberOfAllocatedResourcesByPeer() {
		return this.numOfAllocResByPeer;
	}

}
