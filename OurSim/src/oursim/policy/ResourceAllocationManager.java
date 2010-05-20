package oursim.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.entities.util.ResourceManager;
import oursim.entities.util.TaskManager;

/**
 * 
 * A manager to help in allocation of resource to tasks..
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class ResourceAllocationManager {

	/**
	 * The peer that holds the resources being allocated.
	 */
	private Peer peer;

	/**
	 * an helper object to manages the resources.
	 */
	private ResourceManager resourceManager;

	/**
	 * an helper object to manages the tasks that are running.
	 */
	private TaskManager taskManager;

	/**
	 * The number of resources that each foreign peer is consuming in this site.
	 * 
	 * numberOfAllocatedResourcesByPeer
	 */
	private Map<Peer, Integer> numOfAllocResByPeer;

	/**
	 * An ordinary constructor.
	 * 
	 * @param peer
	 *            The peer that holds the resources being allocated.
	 * @param resourceManager
	 *            an helper object to manages the resources.
	 * @param taskManager
	 *            an helper object to manages the tasks that are running.
	 */
	public ResourceAllocationManager(Peer peer, ResourceManager resourceManager, TaskManager taskManager) {

		this.peer = peer;

		this.resourceManager = resourceManager;

		this.taskManager = taskManager;

		this.numOfAllocResByPeer = new HashMap<Peer, Integer>();

	}

	/**
	 * Try to performs an allocation of a machine to a given task. If the
	 * allocation succeeds, after the end of the execution of the task the
	 * machine must be deallocated by calling the method
	 * {@link #deallocateTask(Task)}.
	 * 
	 * @param task
	 *            the given task.
	 * @return the machine that have been allocated to the given task or
	 *         <code>null</code> if there are no machine that could be
	 *         allocated to the task.
	 * @see {@link #deallocateTask(Task)}
	 */
	public Machine allocateTask(Task task) {
		Machine resource = this.resourceManager.hasAvailableResource() ? this.resourceManager.allocateResourceToTask(task) : forceAPreemptionOnBehalfOf(task);

		if (resource != null) {
			this.taskManager.startTask(task, resource);
			increaseAccounting(task.getSourcePeer());
		}

		return resource;

	}

	/**
	 * Performs an deallocation of a task. The task must have been allocated by
	 * calling the method {@link #allocateTask(Task)}.
	 * 
	 * @param task
	 *            the task to be deallocated.
	 * @return <code>true</code> if the task has been succesfully deallocate,
	 *         <code>false</code> otherwise.
	 * @see {@link #allocateTask(Task)}
	 */
	public boolean deallocateTask(Task task) {
		assert this.resourceManager.isAllocated(this.taskManager.getMachine(task));
		if (this.resourceManager.isAllocated(this.taskManager.getMachine(task))) {
			this.resourceManager.releaseResource(this.taskManager.finishTask(task));
			decreaseAccounting(task.getSourcePeer());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Force a preemption of a task on behalf of a given another.
	 * 
	 * @param task
	 *            task that will benefit itself of the preemption.
	 * @return the machine where the preempted task was running.
	 */
	private Machine forceAPreemptionOnBehalfOf(Task task) {

		Machine releasedMachine = null;

		Peer consumer = task.getSourcePeer();
		List<Peer> preemptablePeers = peer.prioritizePeersToPreemptionOnBehalfOf(consumer);

		if (!preemptablePeers.isEmpty()) {
			Task doomed = chooseATaskToBePreempted(preemptablePeers);
			releasedMachine = this.taskManager.getMachine(doomed);
			this.peer.preemptTask(doomed);
		}

		return releasedMachine;
	}

	/**
	 * Choose from a given collection of peers once task to be preempted.
	 * 
	 * @param preemptablePeers
	 *            the given collection of peers from which the task will be
	 *            preempted.
	 * @return the task to be preempted
	 */
	private Task chooseATaskToBePreempted(List<Peer> preemptablePeers) {
		assert !preemptablePeers.isEmpty();

		Peer chosen = preemptablePeers.get(preemptablePeers.size() - 1);
		List<Task> tasks = this.taskManager.getAllTasksFromPeer(chosen);

		this.peer.prioritizeTasksToPreemption(tasks);
		return tasks.get(0);

	}

	/**
	 * Update (increasing) the counting of resources been consumed by a given
	 * peer.
	 * 
	 * @param consumer
	 *            the peer which update will be related to.
	 */
	private void increaseAccounting(Peer consumer) {
		if (consumer != peer) {
			int consumedResources = this.numOfAllocResByPeer.containsKey(consumer) ? this.numOfAllocResByPeer.get(consumer) : 0;
			this.numOfAllocResByPeer.put(consumer, consumedResources + 1);
		}
	}

	/**
	 * Update (decreasing) the counting of resources been consumed by a given
	 * peer.
	 * 
	 * @param consumer
	 *            the peer which update will be related to.
	 */
	private void decreaseAccounting(Peer consumer) {
		if (consumer != peer) {
			int resourcesBeingConsumedByPeer = this.numOfAllocResByPeer.get(consumer) - 1;
			if (resourcesBeingConsumedByPeer == 0) {
				this.numOfAllocResByPeer.remove(consumer);
			} else {
				this.numOfAllocResByPeer.put(consumer, resourcesBeingConsumedByPeer);
			}
		}
	}

	/**
	 * Gets all the foreign peers that are consuming from the resources mananged
	 * by this resourceAllocationPolicy.
	 * 
	 * @return all the foreign peers
	 */
	public Set<Peer> getForeignConsumingPeers() {
		return this.numOfAllocResByPeer.keySet();
	}

	/**
	 * Gets The number of resources that each foreign peer is consuming in this
	 * site.
	 * 
	 * @return The number of resources that each foreign peer is consuming in
	 *         this site.
	 */
	public Map<Peer, Integer> getNumberOfAllocatedResourcesByPeer() {
		return this.numOfAllocResByPeer;
	}

}
