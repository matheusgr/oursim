package oursim.entities;

import oursim.policy.ResourceAllocationPolicy;
import oursim.policy.ResourceSharingPolicy;

public class Peer {

    private int amountOfResources;
    private String name;

    private ResourceAllocationPolicy resourceAllocationPolicy;

    public Peer(String name, int amountOfResources, ResourceSharingPolicy resourceSharingPolicy) {
	this.name = name;
	this.amountOfResources = amountOfResources;
	this.resourceAllocationPolicy = new ResourceAllocationPolicy(this, resourceSharingPolicy);
    }

    public double getUtilization() {
	return ((double) (this.amountOfResources - resourceAllocationPolicy.getAvailableResources())) / this.amountOfResources;
    }

    public int getAmountOfResources() {
	return amountOfResources;
    }
    
    public long getAmountOfResourcesToShare() {
	return resourceAllocationPolicy.getAmountOfResourcesToShare();
    }
    
    public boolean addJob(Job job, Peer consumer, long currentTime) {
	return this.resourceAllocationPolicy.allocateJob(job, consumer, currentTime);
    }

    public String getName() {
	return name;
    }

    public void finishJob(Job job, boolean preempted) {
	resourceAllocationPolicy.finishJob(job, preempted);
    }

}
