package oursim.entities;

import oursim.policy.AllocationPolicy;
import oursim.policy.SharingPolicy;

public class Peer {

    private int amountOfResources;
    private String name;

    private AllocationPolicy allocationPolicy;

    public Peer(String name, int amountOfResources, SharingPolicy sharingPolicy) {
	this.name = name;
	this.amountOfResources = amountOfResources;
	this.allocationPolicy = new AllocationPolicy(this, sharingPolicy);
    }

    public double getUtilization() {
	return ((double) (this.amountOfResources - allocationPolicy.getAvailableResources())) / this.amountOfResources;
    }

    public int getAmountOfResources() {
	return amountOfResources;
    }

    public boolean addJob(Job job, Peer consumer, long currentTime) {
	return this.allocationPolicy.addJob(job, consumer, currentTime);
    }

    public String getName() {
	return name;
    }

    public void finishJob(Job job, boolean preempted) {
	allocationPolicy.finishJob(job, preempted);
    }

}
