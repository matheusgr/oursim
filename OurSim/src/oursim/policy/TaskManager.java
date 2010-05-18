package oursim.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import oursim.entities.ComputableElement;
import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.entities.Processor;
import oursim.entities.Task;
import oursim.entities.TaskExecution;
import oursim.simulationevents.EventQueue;
import oursim.util.BidirectionalMap;

public class TaskManager {

	private BidirectionalMap<Task, Machine> tasksInExecution;

	private HashSet<Task> localTasks;
	private HashSet<Task> foreignTasks;

	private Peer peer;

	// a quantidade de recursos que o peer remoto está consumindo neste site
	private HashMap<Peer, Integer> numberOfAllocatedResourcesByPeer;

	public TaskManager(Peer peer) {
		this.peer = peer;
		this.tasksInExecution = new BidirectionalMap<Task, Machine>();
		this.foreignTasks = new HashSet<Task>();
		this.localTasks = new HashSet<Task>();
		this.numberOfAllocatedResourcesByPeer = new HashMap<Peer, Integer>();
	}

	public HashSet<Task> getForeignTasks() {
		return this.foreignTasks;
	}

	public HashSet<? extends ComputableElement> getLocalTasks() {
		return this.localTasks;
	}

	public void addTask(Task task, Machine resource) {
		assert resource != null && !tasksInExecution.containsKey(task);

		this.tasksInExecution.put(task, resource);
		long currentTime = EventQueue.getInstance().getCurrentTime();
		Processor defaultProcessor = resource.getDefaultProcessor();
		task.setTaskExecution(new TaskExecution(task, defaultProcessor, currentTime));
		task.setStartTime(currentTime);
		task.setTargetPeer(peer);

		Peer sourcePeer = task.getSourcePeer();
		if (sourcePeer == peer) {
			this.addLocalTask(task);
		} else {
			foreignTasks.add(task);
			int consumedResources = 0;
			if (this.numberOfAllocatedResourcesByPeer.containsKey(sourcePeer)) {
				consumedResources = this.numberOfAllocatedResourcesByPeer.get(sourcePeer);
			}
			this.numberOfAllocatedResourcesByPeer.put(sourcePeer, consumedResources + 1);
		}

	}

	private boolean remove(Task task) {
		return (task.getSourcePeer() == peer) ? removeLocalTask(task) : removeForeignTask(task);
	}

	public List<Task> getAllTasksFromPeer(Peer chosen) {
		List<Task> tasks;
		if (chosen == peer) {
			tasks = new ArrayList<Task>(localTasks);
		} else {
			// todos as tasks do escolhido que estão rodando
			tasks = new LinkedList<Task>();
			for (Task j : foreignTasks) {
				if (j.getSourcePeer() == chosen) {
					tasks.add(j);
				}
			}
		}
		return tasks;
	}

	/**
	 * Update the status of the all tasks being executed.
	 * 
	 * @param currentTime
	 *            The instante at which the update refers to.
	 */
	public void updateTime(long currentTime) {
		for (Entry<Task, Machine> taskAndMachine : tasksInExecution.entrySet()) {
			Task task = taskAndMachine.getKey();
			Long estimatedFinishTime = task.getTaskExecution().updateProcessing(currentTime);
			if (estimatedFinishTime != null) {
				long finishTime = EventQueue.getInstance().getCurrentTime() + estimatedFinishTime;
				// TODO:Verificar se tem como evitar essa chamada nessa classe
				EventQueue.getInstance().addFinishTaskEvent(finishTime, task);
			}
		}
	}

	public void addLocalTask(Task task) {
		this.localTasks.add(task);
	}

	private boolean removeLocalTask(Task task) {
		return this.localTasks.remove(task);
	}

	public Set<Peer> getForeignConsumingPeers() {
		return this.numberOfAllocatedResourcesByPeer.keySet();
	}

	private boolean removeForeignTask(Task task) {
		assert this.foreignTasks.contains(task);
		Peer sourcePeer = task.getSourcePeer();
		int resourcesBeingConsumedByPeer = this.numberOfAllocatedResourcesByPeer.get(sourcePeer) - 1;
		if (resourcesBeingConsumedByPeer == 0) {
			this.numberOfAllocatedResourcesByPeer.remove(sourcePeer);
		} else {
			this.numberOfAllocatedResourcesByPeer.put(sourcePeer, resourcesBeingConsumedByPeer);
		}

		return this.foreignTasks.remove(task);
	}

	public boolean hasForeignTask() {
		return !this.foreignTasks.isEmpty();
	}

	public void addForeignTask(Task task) {
		this.foreignTasks.add(task);
	}

	public Machine finishTask(Task task) {
		assert this.tasksInExecution.containsKey(task) : task;

		Machine machine = this.tasksInExecution.remove(task);
		boolean removed = this.remove(task);
		assert removed && machine != null;
		return machine;
	}

	public HashMap<Peer, Integer> getNumberOfAllocatedResourcesByPeer() {
		return numberOfAllocatedResourcesByPeer;
	}

	public int getNumberOfLocallyConsumedResources() {
		return this.localTasks.size();
	}

	public Machine getMachine(Task task) {
		return this.tasksInExecution.get(task);
	}

	public Task getTask(Machine resource) {

		return this.tasksInExecution.getKey(resource);
	}

	public void finishTask(Machine resource) {
		assert this.tasksInExecution.containsValue(resource);
		Task task = this.getTask(resource);
		finishTask(task);
	}

	public boolean isInExecution(Machine machine) {
		return this.tasksInExecution.containsValue(machine);
	}

	public boolean isInExecution(Task task) {
		return this.tasksInExecution.containsKey(task);
	}

}
