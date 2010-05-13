package oursim.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import oursim.entities.ComputableElement;
import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.entities.Processor;
import oursim.entities.Task;
import oursim.entities.TaskExecution;
import oursim.simulationevents.EventQueue;

public class TaskManager {

	private Map<Task, Machine> tasksInExecution;
	private Map<Machine, Task> machinesInExecution;

	private HashSet<Task> localTasks;
	private HashSet<Task> foreignTasks;

	private Peer peer;

	// a quantidade de recursos que o peer remoto está consumindo neste site
	private HashMap<Peer, Integer> amountOfAllocatedResourcesByPeer;

	public TaskManager(Peer peer) {
		this.peer = peer;
		this.tasksInExecution = new HashMap<Task, Machine>();
		this.machinesInExecution = new HashMap<Machine, Task>();
		this.foreignTasks = new HashSet<Task>();
		this.localTasks = new HashSet<Task>();
		this.amountOfAllocatedResourcesByPeer = new HashMap<Peer, Integer>();
	}

	public HashSet<Task> getForeignTasks() {
		return this.foreignTasks;
	}

	public HashSet<? extends ComputableElement> getLocalTasks() {
		return this.localTasks;
	}

	public void addTask(Task task, Machine resource) {
		assert resource != null && !tasksInExecution.containsKey(task) && !machinesInExecution.containsKey(resource);

		this.tasksInExecution.put(task, resource);
		this.machinesInExecution.put(resource, task);
		long currentTime = EventQueue.getInstance().currentTime();
		Processor defaultProcessor = resource.getDefaultProcessor();
		task.setTaskExecution(new TaskExecution(task, defaultProcessor, currentTime));

		Peer sourcePeer = task.getSourcePeer();
		if (sourcePeer == peer) {
			this.addLocalTask(task);
		} else {
			foreignTasks.add(task);
			int consumedResources = 0;
			if (this.amountOfAllocatedResourcesByPeer.containsKey(sourcePeer)) {
				consumedResources = this.amountOfAllocatedResourcesByPeer.get(sourcePeer);
			}
			this.amountOfAllocatedResourcesByPeer.put(sourcePeer, consumedResources + 1);
		}

	}

	public boolean remove(Task task) {
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

	public void updateTime(long currentTime) {
		for (Entry<Task, Machine> taskAndMachine : tasksInExecution.entrySet()) {
			Task task = taskAndMachine.getKey();
			Long estimatedFinishTime = task.getTaskExecution().updateProcessing(currentTime);
			if (estimatedFinishTime != null) {
				long finishTime = EventQueue.getInstance().currentTime() + estimatedFinishTime;
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
		return this.amountOfAllocatedResourcesByPeer.keySet();
	}

	private boolean removeForeignTask(Task task) {
		assert this.foreignTasks.contains(task);
		Peer sourcePeer = task.getSourcePeer();
		int resourcesBeingConsumedByPeer = this.amountOfAllocatedResourcesByPeer.get(sourcePeer) - 1;
		if (resourcesBeingConsumedByPeer == 0) {
			this.amountOfAllocatedResourcesByPeer.remove(sourcePeer);
		} else {
			this.amountOfAllocatedResourcesByPeer.put(sourcePeer, resourcesBeingConsumedByPeer);
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
		Task taskTemp = this.machinesInExecution.remove(machine);
		boolean removed = this.remove(task);
		assert removed && machine != null && taskTemp != null;
		return machine;
	}

	public HashMap<Peer, Integer> getAmountOfAllocatedResourcesByPeer() {
		return amountOfAllocatedResourcesByPeer;
	}

	public int getAmountOfLocallyConsumedResources() {
		return this.localTasks.size();
	}

	public Machine getMachine(Task task) {
		return this.tasksInExecution.get(task);
	}

	public Task getTask(Machine resource) {
		return this.machinesInExecution.get(resource);
	}

	public void finishTask(Machine resource) {
		assert this.machinesInExecution.containsKey(resource);
		Task task = this.getTask(resource);
		finishTask(task);
	}

	public boolean isInExecution(Machine machine) {
		return this.machinesInExecution.containsKey(machine);
	}

	public boolean isInExecution(Task task) {
		return this.tasksInExecution.containsKey(task);
	}

}
