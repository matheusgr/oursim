package oursim.policy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.entities.Task;

public class ResourceAllocationPolicy {

	private Peer peer;

	private ResourceSharingPolicy resourceSharingPolicy;

	private ResourceManager resourceManager;

	private TaskManager taskManager;

	public ResourceAllocationPolicy(Peer peer, ResourceSharingPolicy resourceSharingPolicy, ResourceManager resourceManager, TaskManager taskManager) {

		this.peer = peer;

		this.resourceManager = resourceManager;

		this.taskManager = taskManager;

		this.resourceSharingPolicy = resourceSharingPolicy;

	}

	public boolean allocateTask(Task task) {
		Machine resource = this.resourceManager.hasAvailableResource() ? this.resourceManager.allocateResource() : forceAPreemptionOnBehalfOf(task);

		if (resource != null) {
			this.taskManager.addTask(task, resource);
			return true;
		} else {
			return false;
		}

	}

	private Machine forceAPreemptionOnBehalfOf(Task task) {

		Machine resource = null;

		HashMap<Peer, Integer> allocatedResourcesByPeer = this.taskManager.getAmountOfAllocatedResourcesByPeer();
		HashSet<Task> foreignTasks = this.taskManager.getForeignTasks();
		Peer consumer = task.getSourcePeer();
		List<Peer> preemptablePeers = resourceSharingPolicy.getPreemptablePeers(peer, consumer, allocatedResourcesByPeer, foreignTasks);

		// Consumer is not preemptable: so it can preempt someone.
		if (!preemptablePeers.isEmpty()) {
			Task chosen = chooseATaskToBePreempted(preemptablePeers);
			resource = this.taskManager.getMachine(chosen);
			this.peer.preemptTask(chosen);
		}
		
		return resource;
	}

	private Task chooseATaskToBePreempted(List<Peer> preemptablePeers) {
		assert !preemptablePeers.isEmpty();

		Peer chosen = preemptablePeers.get(preemptablePeers.size() - 1);

		List<Task> tasks = this.taskManager.getAllTasksFromPeer(chosen);

		// get recently started job first
		// XXX Política para preemptar task por peer
		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
				return (int) (t2.getStartTime() - t1.getStartTime());
			}
		});

		return tasks.get(0);

	}

}
