package oursim.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import oursim.dispatchableevents.workerevents.WorkerEvent;
import oursim.dispatchableevents.workerevents.WorkerEventListenerAdapter;
import oursim.input.Workload;
import oursim.policy.ResourceAllocationPolicy;
import oursim.policy.ResourceManager;
import oursim.policy.ResourceRequestPolicy;
import oursim.policy.ResourceSharingPolicy;
import oursim.policy.TaskManager;
import oursim.simulationevents.EventQueue;

public class Peer extends WorkerEventListenerAdapter {

	private String name;

	private List<Machine> resources;

	private ResourceAllocationPolicy resourceAllocationPolicy;

	private ResourceSharingPolicy resourceSharingPolicy;

	private ResourceRequestPolicy resourceRequestPolicy;

	private ResourceManager resourceManager;

	private TaskManager taskManager;

	private Workload workload;

	private List<Job> jobs;

	private static long nextMachineId = 0;

	public Peer(String name, int numberOfResources, ResourceSharingPolicy resourceSharingPolicy) {
		this(name, numberOfResources, Processor.EC2_COMPUTE_UNIT.getSpeed(), resourceSharingPolicy);
	}

	public Peer(String name, int numberOfResources, long nodeMIPSRating, ResourceSharingPolicy resourceSharingPolicy) {
		this.name = name;

		this.resources = new ArrayList<Machine>(numberOfResources);

		this.jobs = new ArrayList<Job>();

		for (int i = 0; i < numberOfResources; i++) {
			addMachine(nodeMIPSRating);
		}

		this.resourceManager = new ResourceManager(this);
		this.taskManager = new TaskManager(this);

		this.resourceSharingPolicy = resourceSharingPolicy;
		this.resourceSharingPolicy.addPeer(this);

		this.resourceRequestPolicy = new ResourceRequestPolicy(this);

		this.resourceAllocationPolicy = new ResourceAllocationPolicy(this, resourceSharingPolicy, this.resourceManager, this.taskManager);

	}

	private void addMachine(long nodeMIPSRating) {
		this.resources.add(new Machine("m_" + nextMachineId, nodeMIPSRating));
		nextMachineId++;
	}

	public String getName() {
		return name;
	}

	public void updateTime(long currentTime) {
		this.taskManager.updateTime(currentTime);
	}

	public int getNumberOfResources() {
		return this.resources.size();
	}

	public void addResource(Machine machine) {
		this.resources.add(machine);
	}

	/**
	 * Only use resources that aren't busy by local jobs.
	 * 
	 * @return
	 */
	public long getNumberOfResourcesToShare() {
		return this.resourceManager.getNumberOfResources() - this.taskManager.getNumberOfLocallyConsumedResources();
	}

	boolean addJob(Job job) {
		assert job.getSourcePeer() == this;
		if (job.getSourcePeer() == this) {
			this.jobs.add(job);
			return true;
		} else {
			return false;
		}
	}

	public boolean addJob(Job job, Peer consumer) {
		throw new RuntimeException("Método ainda não implementado!");
	}

	public boolean addTask(Task task) {
		return this.resourceAllocationPolicy.allocateTask(task);
	}

	public void finishJob(Job job, boolean preempted) {
		throw new RuntimeException("Método ainda não implementado!");
	}

	public void finishTask(Task task) {
		assert this.taskManager.isInExecution(task) : task;
		this.resourceManager.releaseResource(this.taskManager.finishTask(task));
		this.resourceSharingPolicy.updateMutualBalance(this, task.getSourcePeer(), task);
	}

	public void preemptTask(Task task) {
		this.resourceManager.releaseResource(this.taskManager.finishTask(task));
		this.resourceSharingPolicy.updateMutualBalance(this, task.getSourcePeer(), task);
		EventQueue.getInstance().addPreemptedTaskEvent(task, EventQueue.getInstance().getCurrentTime());
	}

	public int getNumberOfAvailableResources() {
		return this.resourceManager.getNumberOfAvailableResources();
	}

	public double getUtilization() {
		return ((double) (this.getNumberOfResources() - this.getNumberOfAvailableResources())) / this.getNumberOfResources();
	}

	public List<Machine> getResources() {
		return resources;
	}

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

	public void prioritizeResourcesToConsume(List<Peer> peers) {
		this.resourceRequestPolicy.request(peers);
	}

	public void setWorkload(Workload workload) {
		assert this.workload == null;
		// TODO: Verificar a adequabilidade desse tratamento com o workload
		this.workload = workload;
	}

	public Workload getWorkload() {
		assert this.workload != null;
		return workload;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name).append("#resources", resources.size()).toString();
	}

}
