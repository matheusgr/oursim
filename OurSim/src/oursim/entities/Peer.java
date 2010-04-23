package oursim.entities;

import oursim.policy.ResourceAllocationPolicy;
import oursim.policy.ResourceSharingPolicy;

public class Peer {

	private String name;
	private int amountOfResources;

	private ResourceAllocationPolicy resourceAllocationPolicy;

	public Peer(String name, int amountOfResources, ResourceSharingPolicy resourceSharingPolicy) {
		this.name = name;
		this.amountOfResources = amountOfResources;
		this.resourceAllocationPolicy = new ResourceAllocationPolicy(this, resourceSharingPolicy);
	}

	public String getName() {
		return name;
	}

	public int getAmountOfResources() {
		return amountOfResources;
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
		return ((double) (this.amountOfResources - resourceAllocationPolicy.getAvailableResources())) / this.amountOfResources;
	}

}
