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
	return this.resourceAllocationPolicy.allocateJob(job, consumer);
    }

    public void finishJob(Job job, boolean preempted) {
	resourceAllocationPolicy.finishJob(job, preempted);
    }

    public double getUtilization() {
	return ((double) (this.amountOfResources - resourceAllocationPolicy.getAvailableResources())) / this.amountOfResources;
    }

}
