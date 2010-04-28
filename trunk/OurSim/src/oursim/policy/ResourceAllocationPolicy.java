package oursim.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.entities.Processor;
import oursim.entities.Task;
import oursim.entities.TaskExecution;
import oursim.events.EventQueue;

public class ResourceAllocationPolicy {

	private Peer peer;

	private ResourceSharingPolicy resourceSharingPolicy;

	private List<Machine> allocatedResources;

	private List<Machine> availableResources;	
	
	private Map<Task, Machine> tasksInExecution;

	private HashSet<Task> localTasks;
	private HashSet<Task> foreignTasks;

	// a quantidade de recursos que o peer remoto está consumindo neste site
	protected HashMap<Peer, Integer> amountOfAllocatedResourcesByPeer;

	public ResourceAllocationPolicy(Peer peer, ResourceSharingPolicy resourceSharingPolicy) {

		this.peer = peer;

		this.availableResources = peer.getResources();

		this.allocatedResources = new ArrayList<Machine>();

		this.tasksInExecution = new HashMap<Task, Machine>();

		this.foreignTasks = new HashSet<Task>();
		this.localTasks = new HashSet<Task>();

		this.amountOfAllocatedResourcesByPeer = new HashMap<Peer, Integer>();

		this.resourceSharingPolicy = resourceSharingPolicy;

		this.resourceSharingPolicy.addPeer(peer);

	}

	public int getAvailableResources() {
		return availableResources.size();
	}

	/**
	 * Only use resources that are busy by local jobs.
	 * 
	 * o total de recursos menos o que está sendo doado.
	 * 
	 * @return
	 */
	public long getAmountOfResourcesToShare() {
		return peer.getAmountOfResources() - localTasks.size();
	}

	public void finishTask(Task task, boolean preempted) {

		Peer sourcePeer = task.getSourcePeer();

		if (sourcePeer == peer) {
			boolean removed = this.localTasks.remove(task);
			assert removed;
			// Don't compute own balance
		} else {
			boolean removed = this.foreignTasks.remove(task);
			assert removed;

			int resourcesBeingConsumedByPeer = this.amountOfAllocatedResourcesByPeer.get(sourcePeer) - 1;
			if (resourcesBeingConsumedByPeer == 0) {
				this.amountOfAllocatedResourcesByPeer.remove(sourcePeer);
			} else {
				this.amountOfAllocatedResourcesByPeer.put(sourcePeer, resourcesBeingConsumedByPeer);
			}
			// compute balance
			if (!preempted) {
				resourceSharingPolicy.updateMutualBalance(peer, sourcePeer, task.getRunningTime());
			} else {
				// TODO: This is not cool!
				EventQueue.getInstance().addPreemptedTaskEvent(task, EventQueue.getInstance().currentTime());
			}
		}

		releaseResource(task);

	}

	private void allocateResource(Task task) {
		Machine resource = this.availableResources.remove(0);
		this.allocatedResources.add(resource);
		this.tasksInExecution.put(task, resource);
	}

	private void releaseResource(Task task) {
		Machine resource = this.tasksInExecution.remove(task);
		this.allocatedResources.remove(resource);
		this.availableResources.add(resource);
	}

	protected void preemptOneTask(Map<Peer, Long> preemptablePeers) {

		Peer chosen = null;

		LinkedList<Peer> peerList = new LinkedList<Peer>(amountOfAllocatedResourcesByPeer.keySet());

		chosen = peerList.getLast();

		assert chosen != null;

		// todos os jobs do escolhido que estão rodando
		List<Task> tasks = new LinkedList<Task>();
		for (Task j : foreignTasks) {
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

		assert task.getTaskExecution() == null;

		allocateResource(task);
		long currentTime = EventQueue.getInstance().currentTime();
		Processor defaultProcessor = tasksInExecution.get(task).getDefaultProcessor();
		task.setTaskExecution(new TaskExecution(task, defaultProcessor, currentTime));

		Peer sourcePeer = task.getSourcePeer();
		if (sourcePeer == peer) {
			localTasks.add(task);
		} else {
			foreignTasks.add(task);
		}

		if (sourcePeer == peer) {
			return;
		}

		int consumedResources = 0;
		if (this.amountOfAllocatedResourcesByPeer.containsKey(sourcePeer)) {
			consumedResources = this.amountOfAllocatedResourcesByPeer.get(sourcePeer);
		}
		this.amountOfAllocatedResourcesByPeer.put(sourcePeer, consumedResources + 1);

		assert task.getTaskExecution() != null;
	}

	public boolean allocateTask(Task task, Peer consumer) {
		// There is available resources.
		if (getAvailableResources() > 0) {
			startTask(task);
			return true;
		}

		// There are not remote resources that may be preempted
		if (peer.getAmountOfResourcesToShare() == 0) {
			return false;
		}

		// This task may need preemption
		Map<Peer, Long> preemptablePeers = resourceSharingPolicy.calculateAllowedResources(peer, consumer, amountOfAllocatedResourcesByPeer, foreignTasks);

		// Consumer is not preemptable: so it can preempt someone.
		if (!preemptablePeers.isEmpty() && !preemptablePeers.containsKey(consumer)) {
			// Warning: Será que não pode ser preemptado um job do próprio
			// cara? (Não, não pode!)
			preemptOneTask(preemptablePeers);
			startTask(task);
			return true;
		}

		// Peer could not preempt someone
		return false;

	}

	public void updateTime(long currentTime) {
		for (Entry<Task, Machine> taskAndMachine : tasksInExecution.entrySet()) {
			Task task = taskAndMachine.getKey();
			Long estimatedFinishTime = task.getTaskExecution().updateProcessing(currentTime);
			if (estimatedFinishTime != null) {
				long finishTime = EventQueue.getInstance().currentTime() + estimatedFinishTime;
				EventQueue.getInstance().addFinishTaskEvent(finishTime, task);
			}
		}
	}

}
