package oursim.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import oursim.dispatchableevents.workerevents.WorkerEvent;
import oursim.dispatchableevents.workerevents.WorkerEventListenerAdapter;
import oursim.input.Workload;
import oursim.policy.PeerRequestPolicy;
import oursim.policy.ResourceAllocationPolicy;
import oursim.policy.ResourceManager;
import oursim.policy.ResourceSharingPolicy;
import oursim.policy.TaskManager;
import oursim.simulationevents.EventQueue;

/**
 * 
 * Represents a peer in a Peer-to-Peer grid. A peer is a administrative domain
 * that holds and manages a collection of resources. The management is based in
 * a group of policies represented by {@link ResourceAllocationPolicy},
 * {@link ResourceSharingPolicy} and {@link ResourceRequestPolicy}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class Peer extends WorkerEventListenerAdapter {

	/**
	 * The peer's name.
	 */
	private final String name;

	/**
	 * The collection of resources owned by this peer.
	 */
	private final List<Machine> resources;

	private final ResourceAllocationPolicy resourceAllocationPolicy;

	private final ResourceSharingPolicy resourceSharingPolicy;

	private final PeerRequestPolicy resourceRequestPolicy;

	private final ResourceManager resourceManager;

	private final TaskManager taskManager;

	/**
	 * The workload originated by this peer, that is, all the jobs in this
	 * workloads belongs to this peer. Unlike {@link #jobs} the content of this
	 * collections changes as long the method of {@link Workload} is been
	 * called.
	 */
	private Workload workload;

	/**
	 * All the jobs originated by this peer, that is, all the jobs that belongs
	 * to this peer. Unlike {@link #workload} the content of this collections
	 * must remais unchanged through the entire life of simulation.
	 */
	private List<Job> jobs;

	/**
	 * Field to assure the uniqueness of the id of each machine.
	 */
	private static long nextMachineId = 0;

	/**
	 * 
	 * An convenient constructs for peers that have only homogeneous resources,
	 * that is, the peer have <code>amountOfResources</code> resources and
	 * each resource represents and reference machine.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param numberOfResources
	 *            The number of resources that this peer manages.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the resources of this peers
	 *            with another ones.
	 * @throws IllegalArgumentException
	 *             if <code>numberOfResources < 1</code>.
	 * 
	 */
	public Peer(String name, int numberOfResources, ResourceSharingPolicy resourceSharingPolicy) throws IllegalArgumentException {
		this(name, numberOfResources, Processor.EC2_COMPUTE_UNIT.getSpeed(), resourceSharingPolicy);
	}

	/**
	 * 
	 * An convenient constructs for peers that have only homogeneous resources,
	 * that is, the peer have <code>amountOfResources</code> resources and
	 * each resource has the same speed <code>nodeMIPSRating</code>.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param numberOfResources
	 *            The number of resources that this peer manages.
	 * @param nodeMIPSRating
	 *            the mips rating of each resource of this peer.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the resources of this peers
	 *            with another ones.
	 * @throws IllegalArgumentException
	 *             if <code>numberOfResources < 1</code>.
	 * 
	 */
	public Peer(String name, int numberOfResources, long nodeMIPSRating, ResourceSharingPolicy resourceSharingPolicy) throws IllegalArgumentException {
		this(name, resourceSharingPolicy);
		assert numberOfResources > 0;
		if (numberOfResources < 1) {
			throw new IllegalArgumentException("numberOfResources must be at least 1.");
		}
		for (int i = 0; i < numberOfResources; i++) {
			addResource(nodeMIPSRating);
		}
	}

	/**
	 * 
	 * An generic constructor for a peer. After instantiated, it must be called
	 * the method {@link #addResource(Machine)} to explicitly add the resources
	 * to this peer.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the resources of this peers
	 *            with another ones.
	 */
	public Peer(String name, ResourceSharingPolicy resourceSharingPolicy) {

		this.name = name;

		this.jobs = new ArrayList<Job>();
		this.resources = new ArrayList<Machine>();

		this.taskManager = new TaskManager(this);

		this.resourceSharingPolicy = resourceSharingPolicy;
		this.resourceSharingPolicy.addPeer(this);

		this.resourceRequestPolicy = new PeerRequestPolicy(this);

		this.resourceManager = new ResourceManager(this);
		this.resourceAllocationPolicy = new ResourceAllocationPolicy(this, resourceSharingPolicy, this.resourceManager, this.taskManager);

	}

	/**
	 * Create and adds a Machine based on the mipsRating.
	 * 
	 * @param nodeMIPSRating
	 *            the mips rating of the resource to be added.
	 */
	private void addResource(long nodeMIPSRating) {
		addResource(new Machine("m_" + nextMachineId, nodeMIPSRating));
		nextMachineId++;
	}

	/**
	 * Adds a new resource to this peer.
	 * 
	 * @param machine
	 *            The resource to be added.
	 */
	public void addResource(Machine machine) {
		this.resources.add(machine);
		// as have been added resources after the instantiation of the
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
	 * Update the status of the all tasks being executed in the resources of
	 * this peer.
	 * 
	 * @param currentTime
	 *            The instante at which the update refers to.
	 */
	public void updateTime(long currentTime) {
		this.taskManager.updateTime(currentTime);
	}

	/**
	 * @return the number of resources managed by this peer.
	 */
	public int getNumberOfResources() {
		return this.resources.size();
	}

	/**
	 * Only use resources that aren't busy by local jobs.
	 * 
	 * @return the number of resources that aren't busy by local jobs, and so
	 *         it's possible to share.
	 */
	public long getNumberOfResourcesToShare() {
		return this.resourceManager.getNumberOfResources() - this.taskManager.getNumberOfLocallyConsumedResources();
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
		return this.resourceAllocationPolicy.allocateTask(task);
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
			this.resourceManager.releaseResource(this.taskManager.finishTask(task));
			this.resourceSharingPolicy.updateMutualBalance(this, task.getSourcePeer(), task);
			EventQueue.getInstance().addPreemptedTaskEvent(task, EventQueue.getInstance().getCurrentTime());
		} else {
			throw new IllegalArgumentException("The task was not being executed in this peer.");
		}
	}

	/**
	 * Gets the number of free resources.
	 * 
	 * @return the number of resources that are available to process tasks.
	 */
	public int getNumberOfAvailableResources() {
		return this.resourceManager.getNumberOfAvailableResources();
	}

	/**
	 * @return the percentage of resources that are executing tasks.
	 */
	public double getUtilization() {
		return ((double) (this.getNumberOfResources() - this.getNumberOfAvailableResources())) / this.getNumberOfResources();
	}

	/**
	 * Gets the the resources managed by this peer.
	 * 
	 * @return the resources of this peer.
	 */
	public List<Machine> getResources() {
		return resources;
	}

	/**
	 * Verifies if this peer has the resource with the given name.
	 * 
	 * @param machineName
	 *            the name of the resource being queried.
	 * @return <code>true</code> if this peer has the resource,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasResource(String machineName) {
		return this.resourceManager.hasResource(machineName);
	}

	@Override
	public void workerAvailable(WorkerEvent workerEvent) {
		String machineName = (String) workerEvent.getSource();
		this.resourceManager.makeResourceAvailable(machineName);
		// TODO: deve-se reescalonar os jobs agora pois tem recurso disponível
	}

	@Override
	public void workerUnavailable(WorkerEvent workerEvent) {
		String machineName = (String) workerEvent.getSource();
		if (this.resourceManager.isAllocated(machineName)) {
			Machine resource = this.resourceManager.getResource(machineName);
			Task task = this.taskManager.getTask(resource);
			preemptTask(task);
		}
		this.resourceManager.makeResourceUnavailable(machineName);
	}

	/**
	 * Sorts the collection of peer in a way that the preferable peers to
	 * consume are firstly accessed.
	 * 
	 * @param peers
	 *            the peers available to be consumed.
	 */
	public void prioritizePeersToConsume(List<Peer> peers) {
		this.resourceRequestPolicy.prioritize(peers);
	}

	/**
	 * Sets the workload belonged to this peer.
	 * 
	 * @param workload
	 *            the workload.
	 */
	public void setWorkload(Workload workload) {
		assert this.workload == null;
		// TODO: Verificar a adequabilidade desse tratamento com o workload
		this.workload = workload;
	}

	/**
	 * @return the workload belonged to this peer.
	 */
	public Workload getWorkload() {
		assert this.workload != null;
		return workload;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name).append("#resources", resources.size()).toString();
	}

}
