package br.edu.ufcg.lsd.oursim.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventListener;
import br.edu.ufcg.lsd.oursim.entities.util.ResourceAllocationManager;
import br.edu.ufcg.lsd.oursim.entities.util.ResourceManager;
import br.edu.ufcg.lsd.oursim.entities.util.TaskManager;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.ranking.PeerRankingPolicy;
import br.edu.ufcg.lsd.oursim.policy.ranking.ResourceRankingPolicy;
import br.edu.ufcg.lsd.oursim.policy.ranking.TaskPreemptionRankingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityAbstract;

/**
 * 
 * Represents a peer in a Peer-to-Peer grid. A peer is a administrative domain
 * that holds and manages a collection of machines. The management is based in a
 * group of policies represented by {@link ResourceAllocationManager},
 * {@link ResourceSharingPolicy} and {@link ResourceRankingPolicy}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class Peer extends ActiveEntityAbstract implements WorkerEventListener {

	/**
	 * The peer's name.
	 */
	private final String name;

	/**
	 * The collection of machines owned by this peer.
	 */
	private final List<Machine> machines;

	private final ResourceAllocationManager resourceAllocationManager;

	private final PeerRankingPolicy peerRankingPolicy;

	private final TaskPreemptionRankingPolicy taskPreemptionRankingPolicy;

	private final ResourceSharingPolicy resourceSharingPolicy;

	private final ResourceManager resourceManager;

	private final TaskManager taskManager;

	/**
	 * All the jobs originated by this peer, that is, all the jobs that belongs
	 * to this peer. Unlike {@link #workload} the source of this collections
	 * must remais unchanged through the entire life of simulation.
	 */
	private List<Job> jobs;

	/**
	 * Field to assure the uniqueness of the id of each machine.
	 */
	private static long nextMachineId = 0;

	/**
	 * 
	 * An convenient constructs for peers that have only homogeneous machines,
	 * that is, the peer have <code>numberOfMachines</code> machines and each
	 * machine represents an reference machine.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param numberOfMachines
	 *            The number of machines that this peer manages.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the machines of this peers
	 *            with another ones.
	 * @throws IllegalArgumentException
	 *             if <code>numberOfMachines &lt; 1</code>.
	 * 
	 */
	public Peer(String name, int numberOfMachines, ResourceSharingPolicy resourceSharingPolicy) throws IllegalArgumentException {
		this(name, numberOfMachines, Processor.EC2_COMPUTE_UNIT.getSpeed(), resourceSharingPolicy);
	}

	/**
	 * 
	 * An convenient constructs for peers that have only homogeneous machines,
	 * that is, the peer have <code>numberOfMachines</code> machines and each
	 * machine has the same speed <code>nodeMIPSRating</code>.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param numberOfMachines
	 *            The number of machines that this peer manages.
	 * @param nodeMIPSRating
	 *            the mips rating of each machine of this peer.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the machines of this peers
	 *            with another ones.
	 * @throws IllegalArgumentException
	 *             if <code>numberOfMachines &lt; 1</code>.
	 * 
	 */
	public Peer(String name, int numberOfMachines, long nodeMIPSRating, ResourceSharingPolicy resourceSharingPolicy) throws IllegalArgumentException {
		this(name, resourceSharingPolicy);
		assert numberOfMachines > 0;
		if (numberOfMachines < 1) {
			throw new IllegalArgumentException("numberOfMachines must be at least 1.");
		}
		for (int i = 0; i < numberOfMachines; i++) {
			addMachine(nodeMIPSRating);
		}
	}

	/**
	 * 
	 * An generic constructor for a peer. After instantiated, it must be called
	 * the method {@link #addMachine(Machine)} to explicitly add the machines to
	 * this peer.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the machines of this peers
	 *            with another ones.
	 */
	public Peer(String name, ResourceSharingPolicy resourceSharingPolicy) {

		this.name = name;

		this.resourceSharingPolicy = resourceSharingPolicy;
		this.resourceSharingPolicy.addPeer(this);

		this.jobs = new ArrayList<Job>();
		this.machines = new ArrayList<Machine>();

		this.taskManager = new TaskManager(this);

		this.peerRankingPolicy = new PeerRankingPolicy(this);

		this.resourceManager = new ResourceManager(this);
		this.resourceAllocationManager = new ResourceAllocationManager(this, this.resourceManager, this.taskManager);

		this.taskPreemptionRankingPolicy = new TaskPreemptionRankingPolicy(this);

	}

	/**
	 * Create and adds a Machine based on the mipsRating.
	 * 
	 * @param nodeMIPSRating
	 *            the mips rating of the machine to be added.
	 */
	private void addMachine(long nodeMIPSRating) {
		addMachine(new Machine("m_" + nextMachineId, nodeMIPSRating));
		nextMachineId++;
	}

	/**
	 * Adds a new machine to this peer.
	 * 
	 * @param machine
	 *            The machine to be added.
	 */
	public void addMachine(Machine machine) {
		this.machines.add(machine);
		// as have been added machines after the instantiation of the
		// ResourceManager, this must be updated.
		this.resourceManager.update(machine);
	}

	/**
	 * @return the name of this peer.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Update the status of the all tasks being executed in the machines of this
	 * peer.
	 * 
	 * @param currentTime
	 *            The instante at which the update refers to.
	 */
	public void updateTime(long currentTime) {
		for (Task task : this.taskManager.getRunningTasks()) {
			Long estimatedFinishTime = task.getTaskExecution().updateProcessing(currentTime);
			if (estimatedFinishTime != null) {
				long finishTime = getCurrentTime() + estimatedFinishTime;
				this.addFinishTaskEvent(finishTime, task);
			}
		}
	}

	/**
	 * @return the number of machines managed by this peer.
	 */
	public int getNumberOfMachines() {
		return this.machines.size();
	}

	/**
	 * Only use machines that aren't busy by local jobs.
	 * 
	 * @return the number of machines that aren't busy by local jobs, and so
	 *         it's possible to share.
	 */
	public long getNumberOfMachinesToShare() {
		// TODO: there are a bug here: it's needed to account the volatility of
		// the machines.
		// The right way: return
		// (this.resourceManager.getNumberOfAllocatedResources() +
		// this.resourceManager.getNumberOfAvailableResources())
		// - this.taskManager.getNumberOfLocalTasks();
		return this.resourceManager.getNumberOfResources() - this.taskManager.getNumberOfLocalTasks();

	}

	/**
	 * Adds a job to this peer. This means that this peer is the source of this
	 * job.
	 * 
	 * @param job
	 *            a job to be added to this peer.
	 * @return <code>true</code> if the job in fact belongs to this peer,
	 *         <code>false</code> otherwise.
	 */
	boolean addJob(Job job) {
		assert job.getSourcePeer() == this;
		if (job.getSourcePeer() == this) {
			this.jobs.add(job);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add a task to be executed in this peer.
	 * 
	 * @param task
	 *            The task to be executed.
	 * @return <code>true</code> if the task is succesfully added and is been
	 *         executed, <code>false</code> otherwise.
	 */
	public boolean executeTask(Task task) {
		Machine allocatedMachine = this.resourceAllocationManager.allocateTask(task);
		if (allocatedMachine != null) {
			long currentTime = getCurrentTime();
			Processor defaultProcessor = allocatedMachine.getDefaultProcessor();
			task.setTaskExecution(new TaskExecution(task, defaultProcessor, currentTime));
			task.setStartTime(currentTime);
			task.setTargetPeer(this);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Finishs the execution of a task. This means the task has been succesfully
	 * executed and has been completed.
	 * 
	 * @param task
	 *            The task to be finished.
	 * @throws IllegalArgumentException
	 *             if the task was not being executed in this peer.
	 */
	public void finishTask(Task task) throws IllegalArgumentException {
		assert this.taskManager.isInExecution(task) : task;
		if (this.taskManager.isInExecution(task)) {
			this.resourceManager.releaseResource(this.taskManager.finishTask(task));
			this.resourceSharingPolicy.updateMutualBalance(this, task.getSourcePeer(), task);
		} else {
			throw new IllegalArgumentException("The task was not being executed in this peer.");
		}
	}

	/**
	 * Preempts the execution of a task. This means the task was been executing
	 * but must be preempted, whatever the reason.
	 * 
	 * @param task
	 *            The task to be preempted.
	 */
	public void preemptTask(Task task) throws IllegalArgumentException {
		assert this.taskManager.isInExecution(task) : task;

		if (this.taskManager.isInExecution(task)) {
			this.resourceAllocationManager.deallocateTask(task);
			this.resourceSharingPolicy.updateMutualBalance(this, task.getSourcePeer(), task);
			this.addPreemptedTaskEvent(task);
		} else {
			throw new IllegalArgumentException("The task was not being executed in this peer.");
		}
	}

	/**
	 * Gets the number of free machines.
	 * 
	 * @return the number of machines that are available to process tasks.
	 */
	public int getNumberOfAvailableResources() {
		return this.resourceManager.getNumberOfAvailableResources();
	}

	/**
	 * @return the percentage of machines that are executing tasks.
	 */
	public double getUtilization() {
		return ((double) (this.getNumberOfMachines() - this.getNumberOfAvailableResources())) / this.getNumberOfMachines();
	}

	/**
	 * Gets the the machines managed by this peer.
	 * 
	 * @return the machines of this peer.
	 */
	public List<Machine> getMachines() {
		return machines;
	}

	/**
	 * Verifies if this peer has the machine with the given name.
	 * 
	 * @param machineName
	 *            the name of the resource being queried.
	 * @return <code>true</code> if this peer has the machine with the given
	 *         name, <code>false</code> otherwise.
	 */
	public boolean hasMachine(String machineName) {
		return this.resourceManager.hasResource(machineName);
	}

	// B-- beginning of implementation of WorkerEventListener

	@Override
	public void workerAvailable(Event<String> workerEvent) {
		String machineName = workerEvent.getSource();
		this.resourceManager.makeResourceAvailable(machineName);
		// TODO: deve-se reescalonar os jobs agora pois tem recurso disponível
	}

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
		String machineName = workerEvent.getSource();

		if (this.resourceManager.isAllocated(machineName)) {
			Machine resource = this.resourceManager.getResource(machineName);
			Task task = this.taskManager.getTask(resource);
			preemptTask(task);
		}
		this.resourceManager.makeResourceUnavailable(machineName);
	}

	@Override
	public void workerUp(Event<String> workerEvent) {
	}

	@Override
	public void workerDown(Event<String> workerEvent) {
	}

	@Override
	public void workerIdle(Event<String> workerEvent) {
	}

	@Override
	public void workerRunning(Event<String> workerEvent) {
	}

	// E-- end of implementation of WorkerEventListener

	/**
	 * Sorts the collection of peer in a way that the preferable peers to
	 * consume are firstly accessed.
	 * 
	 * @param peers
	 *            the peers available to be consumed.
	 */
	public void prioritizePeersToConsume(List<Peer> peers) {
		this.peerRankingPolicy.rank(peers);
	}

	/**
	 * Sorts the collection of tasks in a way that the preferable tasks to
	 * preemption are firstly accessed.
	 * 
	 * @param tasks
	 *            the tasks candidates to preemption.
	 */
	public void prioritizeTasksToPreemption(List<Task> tasks) {
		this.taskPreemptionRankingPolicy.rank(tasks);
	}

	/**
	 * Gets a collection of peers in a way that the preferable peers to
	 * preemption are firstly accessed.
	 * 
	 * @param consumer
	 *            the peer which will benefit from the preemption.
	 * @return the a collection of peers in a way that the preferable peers to
	 *         preemption are firstly accessed.
	 */
	public List<Peer> prioritizePeersToPreemptionOnBehalfOf(Peer consumer) {
		Map<Peer, Integer> allocatedMachinesByPeer = this.resourceAllocationManager.getNumberOfAllocatedResourcesByPeer();
		Set<Task> foreignTasks = this.taskManager.getForeignTasks();
		return this.resourceSharingPolicy.getPreemptablePeers(this, consumer, allocatedMachinesByPeer, foreignTasks);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name).append("#machines", machines.size()).toString();
	}

}
