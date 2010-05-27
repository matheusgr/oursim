package oursim.entities.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.util.BidirectionalMap;

/**
 * 
 * A helper class to deals with tasks in execution.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class TaskManager {

	/**
	 * The peer where the tasks are executing.
	 */
	private Peer peer;

	/**
	 * mapping between the tasks that are executing and the respective machine.
	 */
	private BidirectionalMap<Task, Machine> tasksInExecution;

	/**
	 * The collection of local tasks that are running, that is, the tasks that
	 * are running and belongs to {@link #peer}.
	 */
	private Set<Task> localTasks;

	/**
	 * The collection of foreign tasks that are running, that is, the tasks that
	 * are running and doesn't belong to {@link #peer}.
	 */
	private Set<Task> foreignTasks;

	/**
	 * 
	 * An ordinary constructor.
	 * 
	 * @param peer
	 *            The peer which holds the resources where the tasks running.
	 */
	public TaskManager(Peer peer) {
		this.peer = peer;
		this.tasksInExecution = new BidirectionalMap<Task, Machine>();
		this.foreignTasks = new HashSet<Task>();
		this.localTasks = new HashSet<Task>();
	}

	/**
	 * The collection of foreign tasks that are running, that is, the tasks that
	 * are running and doesn't belong to {@link #peer}.
	 * 
	 * @return The collection of foreign running tasks
	 */
	public Set<Task> getForeignTasks() {
		return this.foreignTasks;
	}

	/**
	 * The collection of local tasks that are running, that is, the tasks that
	 * are running and belongs to {@link #peer}.
	 * 
	 * @return The collection of local running tasks.
	 */
	public Set<Task> getLocalTasks() {
		return this.localTasks;
	}

	/**
	 * Adds a running tasks. This means that the {@link Task} <code>task</code>
	 * are running in a {@link Machine} <code>machine</code>.
	 * 
	 * @param task
	 *            the task to be added.
	 * @param resource
	 *            the machine in which the task are running.
	 */
	public void startTask(Task task, Machine resource) {
		assert resource != null && !tasksInExecution.containsKey(task);

		this.tasksInExecution.put(task, resource);

		Peer sourcePeer = task.getSourcePeer();
		if (sourcePeer == peer) {
			this.addLocalTask(task);
		} else {
			this.addForeignTask(task);
		}

	}

	/**
	 * Finishs the task that are running in a given machine.
	 * 
	 * @param resource
	 *            the given machine.
	 */
	public void finishTask(Machine resource) {
		assert this.tasksInExecution.containsValue(resource);
		Task task = this.getTask(resource);
		finishTask(task);
	}

	/**
	 * Finishs a given task.
	 * 
	 * @param task
	 *            the given task.
	 * @return the machine in which the given task was running.
	 */
	public Machine finishTask(Task task) {
		assert this.tasksInExecution.containsKey(task) : task;
		Machine machine = this.tasksInExecution.remove(task);
		boolean removed = this.remove(task);
		assert removed && machine != null;
		return machine;
	}

	/**
	 * Remove a task from this taskManager.
	 * 
	 * @param task
	 *            the task to be removed.
	 * @return <code>true</code> if the task has been successfully removed,
	 *         <code>false</false> otherwise.
	 */
	private boolean remove(Task task) {
		return (task.getSourcePeer() == peer) ? removeLocalTask(task) : removeForeignTask(task);
	}

	/**
	 * Gets all tasks that are running and belongs to a given peer.
	 * 
	 * @param chosen
	 *            the given peer.
	 * @return all tasks that are running and belongs to the given peer.
	 */
	public List<Task> getAllTasksFromPeer(Peer chosen) {
		List<Task> tasks;
		if (chosen == peer) {
			tasks = new ArrayList<Task>(localTasks);
		} else {
			// todas as tasks do escolhido que estão rodando
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
	 * Gets all the running tasks of this taskManager.
	 * 
	 * @return all the running tasks of this taskManager.
	 */
	public Set<Task> getRunningTasks() {
		return this.tasksInExecution.keySet();
	}

	/**
	 * Checks if there are some foreign task running in the peer of this
	 * TaskManager.
	 * 
	 * @return <code>true</code> if there are some foreign task running,
	 *         <code>false</false> otherwise.
	 */
	public boolean hasForeignTask() {
		return !this.foreignTasks.isEmpty();
	}

	/**
	 * Gets the total of local running tasks, that is, that tasks thar are
	 * running and belong to the peer that holds this taskManager.
	 * 
	 * @return Gets the total of resources being locally consumed
	 */
	public int getNumberOfLocalTasks() {
		return this.localTasks.size();
	}

	/**
	 * Gets the machine in which a given task are running.
	 * 
	 * @param task
	 *            the given task.
	 * @return the machine in which the given task are running.
	 */
	public Machine getMachine(Task task) {
		return this.tasksInExecution.get(task);
	}

	/**
	 * Gets the task that are running in a given machine.
	 * 
	 * @param resource
	 *            the given machine.
	 * @return the task that are running in a given machine.
	 */
	public Task getTask(Machine resource) {

		return this.tasksInExecution.getKey(resource);
	}

	/**
	 * Checks if there are some task running in a given machine.
	 * 
	 * @param machine
	 *            the given machine.
	 * @return <code>true</code> if there are some foreign task running in the
	 *         given machine, <code>false</false> otherwise.
	 */
	public boolean isInExecution(Machine machine) {
		return this.tasksInExecution.containsValue(machine);
	}

	/**
	 * Checks if a given task is running.
	 * 
	 * @param task
	 *            the given task.
	 * @return <code>true</code> if the given task is running,
	 *         <code>false</false> otherwise.
	 */
	public boolean isInExecution(Task task) {
		return this.tasksInExecution.containsKey(task);
	}

	private void addLocalTask(Task task) {
		assert !this.localTasks.contains(task);
		this.localTasks.add(task);
	}

	private void addForeignTask(Task task) {
		this.foreignTasks.add(task);
	}

	private boolean removeLocalTask(Task task) {
		assert this.localTasks.contains(task);
		return this.localTasks.remove(task);
	}

	private boolean removeForeignTask(Task task) {
		assert this.foreignTasks.contains(task);
		return this.foreignTasks.remove(task);
	}

}
