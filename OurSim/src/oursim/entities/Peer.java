package oursim.entities;

import java.util.ArrayList;
import java.util.List;

import oursim.events.EventQueue;
import oursim.policy.ResourceAllocationPolicy;
import oursim.policy.ResourceManager;
import oursim.policy.ResourceSharingPolicy;
import oursim.policy.TaskManager;
import oursim.workerevents.WorkerEvent;
import oursim.workerevents.WorkerEventListenerAdapter;

public class Peer extends WorkerEventListenerAdapter {

	private String name;

	private List<Machine> resources;

	private ResourceAllocationPolicy resourceAllocationPolicy;

	private ResourceManager resourceManager;

	private TaskManager taskManager;

	private ResourceSharingPolicy resourceSharingPolicy;

	private static long nextMachineId = 0;

	public Peer(String name, int amountOfResources, int nodeMIPSRating, ResourceSharingPolicy resourceSharingPolicy) {
		this.name = name;

		this.resources = new ArrayList<Machine>(amountOfResources);

		for (int i = 0; i < amountOfResources; i++) {
			addMachine(nodeMIPSRating);
		}

		this.resourceManager = new ResourceManager(this);
		this.taskManager = new TaskManager(this);

		this.resourceSharingPolicy = resourceSharingPolicy;
		this.resourceSharingPolicy.addPeer(this);

		this.resourceAllocationPolicy = new ResourceAllocationPolicy(this, resourceSharingPolicy, this.resourceManager, this.taskManager);

	}

	private void addMachine(int nodeMIPSRating) {
		this.resources.add(new Machine("m_" + nextMachineId, nodeMIPSRating));
		nextMachineId++;
	}

	public String getName() {
		return name;
	}

	public void updateTime(long currentTime) {
		this.taskManager.updateTime(currentTime);
	}

	public int getAmountOfResources() {
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
	public long getAmountOfResourcesToShare() {
		return this.resourceManager.getAmountOfResources() - this.taskManager.getAmountOfLocallyConsumedResources();
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
		this.resourceManager.releaseResource(this.taskManager.finishTask(task));
		this.resourceSharingPolicy.updateMutualBalance(this, task.getSourcePeer(), task);
	}

	public void preemptTask(Task task) {
		this.resourceManager.releaseResource(this.taskManager.finishTask(task));
		this.resourceSharingPolicy.updateMutualBalance(this, task.getSourcePeer(), task);
		EventQueue.getInstance().addPreemptedTaskEvent(task, EventQueue.getInstance().currentTime());
	}

	public int getAmountOfAvailableResources() {
		return this.resourceManager.getAvailableResources();
	}

	public double getUtilization() {
		return ((double) (this.getAmountOfResources() - this.getAmountOfAvailableResources())) / this.getAmountOfResources();
	}

	public List<Machine> getResources() {
		return resources;
	}

	@Override
	public void workerAvailable(WorkerEvent workerEvent) {
		String machineName = (String) workerEvent.getSource();
		if (this.resourceManager.hasResource(machineName)) {
			this.resourceManager.makeResourceAvailable(machineName);
		}

		// TODO: deve-se escalonar os jobs agora pois tem recurso disponível
	}

	@Override
	public void workerUnavailable(WorkerEvent workerEvent) {
		String machineName = (String) workerEvent.getSource();
		if (this.resourceManager.hasResource(machineName)) {
			if (this.resourceManager.isAllocated(machineName)) {
				Machine resource = this.resourceManager.getResource(machineName);
				Task task = this.taskManager.getTask(resource);
				preemptTask(task);
			}
			this.resourceManager.makeResourceUnavailable(machineName);
		}
	}

}
