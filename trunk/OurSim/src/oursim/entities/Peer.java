package oursim.entities;

import java.util.ArrayList;
import java.util.List;

import oursim.policy.ResourceAllocationPolicy;
import oursim.policy.ResourceSharingPolicy;

public class Peer {

	private String name;

	private List<Machine> resources;

	private ResourceAllocationPolicy resourceAllocationPolicy;

	private static long nextMachineId = 0;

	public Peer(String name, int amountOfResources, int nodeMIPSRating, ResourceSharingPolicy resourceSharingPolicy) {
		this.name = name;

		this.resources = new ArrayList<Machine>(amountOfResources);

		for (int i = 0; i < amountOfResources; i++) {
			addMachine(nodeMIPSRating);
		}

		this.resourceAllocationPolicy = new ResourceAllocationPolicy(this, resourceSharingPolicy);
	}

	private void addMachine(int nodeMIPSRating) {
		this.resources.add(new Machine("M" + nextMachineId, nodeMIPSRating));
		nextMachineId++;
	}

	public String getName() {
		return name;
	}

	public void updateTime(long currentTime) {
		this.resourceAllocationPolicy.updateTime(currentTime);
	}

	public int getAmountOfResources() {
		return this.resources.size();
	}

	public void addResource(Machine machine) {
		this.resources.add(machine);
	}

	public long getAmountOfResourcesToShare() {
		return resourceAllocationPolicy.getAmountOfResourcesToShare();
	}

	public boolean addJob(Job job, Peer consumer) {
		throw new RuntimeException("Método ainda não implementado!");
	}

	public boolean addTask(Task task, Peer consumer) {
		return this.resourceAllocationPolicy.allocateTask(task, consumer);
	}

	public void finishJob(Job job, boolean preempted) {
		throw new RuntimeException("Método ainda não implementado!");
	}

	public void finishTask(Task task, boolean preempted) {
		resourceAllocationPolicy.finishTask(task, preempted);
	}

	public double getUtilization() {
		return ((double) (this.getAmountOfResources() - resourceAllocationPolicy.getAvailableResources())) / this.getAmountOfResources();
	}

	public List<Machine> getResources() {
		return resources;
	}

}
