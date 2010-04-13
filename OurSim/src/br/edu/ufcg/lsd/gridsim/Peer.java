package br.edu.ufcg.lsd.gridsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Peer {

    protected int resources;
    protected int availableResources;

    protected HashSet<Job> runningJobs;
    protected HashSet<Job> runningLocalJobs;

    protected HashMap<Peer, Integer> balances;
    // a quantidade de recursos que o peer remoto está consumindo neste site
    protected HashMap<Peer, Integer> remoteConsumingPeers;

    protected HashMap<String, Peer> peersMap;

    public Peer(int size, String clusterId) {

	this.resources = size;
	this.availableResources = size;

	this.runningJobs = new HashSet<Job>();
	this.runningLocalJobs = new HashSet<Job>();
	this.balances = new HashMap<Peer, Integer>();
	this.remoteConsumingPeers = new HashMap<Peer, Integer>();

    }

    public void setPeersMap(HashMap<String, Peer> peersMap) {
	this.peersMap = peersMap;
    }

    @Deprecated
    public int getBalance(Peer consumingPeer) {
	int balance = 0;
	if (Configuration.getInstance().useNoF()) {
	    if (this.balances.containsKey(consumingPeer)) {
		balance = this.balances.get(consumingPeer);
	    }
	}
	return balance;
    }

    @Deprecated
    public void finishOportunisticJob(Job job, boolean preempted) {

	Peer peer = peersMap.get(getJobOrigSite(job));

	if (this == peer) {
	    boolean removed = this.runningLocalJobs.remove(job);
	    assert removed;
	} else {
	    boolean removed = this.runningJobs.remove(job);
	    assert removed;
	}

	this.availableResources++;

	if (peer == this) {
	    return; // Don't compute own balance
	} else {
	    int value = this.remoteConsumingPeers.get(peer) - 1;
	    if (value == 0) {
		this.remoteConsumingPeers.remove(peer);
	    } else {
		this.remoteConsumingPeers.put(peer, value);
	    }
	}

	if (Configuration.getInstance().checkpointEnabled() || !preempted) {
	    setBalance(peer, -job.getRunTime());
	    // Não aparenta ser um comportamento autônomo. Está setando o
	    // balance do peer origem.
	    peer.setBalance(this, job.getRunTime());
	}

    }

    @Deprecated
    private String getJobOrigSite(Job job) {
	return job.getOrigSite();
    }

    @Deprecated
    public boolean addOportunisticJob(Job job, final Peer consumer, int time) {

	// There is available resources.
	if (availableResources > 0) {
	    startJob(job);
	    return true;
	}

	// Peer is full of local jobs
	if (runningLocalJobs.size() == this.resources) {
	    return false;
	}

	// This job may need preemption
	HashMap<Peer, Integer> allowedResources = calculateAllowedResources(consumer, time);

	int usedResources = remoteConsumingPeers.containsKey(consumer) ? remoteConsumingPeers.get(consumer) : 0;

	if (consumer == this || allowedResources.get(consumer) > usedResources) {
	    // Warning: Será que não pode ser preemptado um job do próprio cara?
	    // (Não, não pode!)
	    preemptOneJob(allowedResources, time);
	    startJob(job);
	    return true;
	}
	return false;
    }

    @Deprecated
    protected HashMap<Peer, Integer> calculateAllowedResources(Peer newConsumer, int currentTime) {
	// Set<Peer> consumers = consumingPeers.keySet();

	// Only use resources that are busy by local jobs
	// o total de recursos menos o que está sendo doado.
	int resourcesToShare = this.getRemoteShareSize(currentTime);

	// If its local, remove one resource from remote
	if (newConsumer == this) {
	    resourcesToShare -= 1;
	}

	// a soma dos balances de todos os peers
	int totalBalance = 0;

	HashMap<Peer, Integer> allowedResources = new HashMap<Peer, Integer>();

	for (Peer p : remoteConsumingPeers.keySet()) {
	    totalBalance += getBalance(p);
	}

	// pois se não for novo já está na contagem anterior
	totalBalance += remoteConsumingPeers.containsKey(newConsumer) ? 0 : getBalance(newConsumer);

	int resourcesLeft = resourcesToShare;

	int pSize = remoteConsumingPeers.size();

	pSize += remoteConsumingPeers.containsKey(newConsumer) ? 0 : 1;

	// Set minimum resources allowed for each peer
	for (Peer p : remoteConsumingPeers.keySet()) {
	    int resourcesForPeer = 0;
	    double share;
	    if (totalBalance == 0) {
		share = (1.0d / pSize);
	    } else {
		share = ((double) getBalance(p)) / totalBalance;
	    }
	    resourcesForPeer = (int) (share * resourcesToShare);
	    allowedResources.put(p, resourcesForPeer);
	    resourcesLeft -= resourcesForPeer;
	}

	if (!remoteConsumingPeers.containsKey(newConsumer)) {
	    int resourcesForPeer = 0;
	    double share;
	    if (totalBalance == 0) {
		share = (1.0d / pSize);
	    } else {
		share = ((double) getBalance(newConsumer)) / totalBalance;
	    }
	    resourcesForPeer = (int) (share * resourcesToShare);
	    allowedResources.put(newConsumer, resourcesForPeer);
	    resourcesLeft -= resourcesForPeer;
	}

	// recursos que o peer em questão já está utilizando
	int resourcesInUse = remoteConsumingPeers.containsKey(newConsumer) ? remoteConsumingPeers.get(newConsumer) : 0;

	if (newConsumer != this && resourcesInUse <= allowedResources.get(newConsumer)) {
	    return allowedResources;
	}

	final Peer actual = this;

	ArrayList<Peer> consumersList = new ArrayList<Peer>(remoteConsumingPeers.size() + 1);
	consumersList.addAll(remoteConsumingPeers.keySet());
	if (!remoteConsumingPeers.containsKey(newConsumer)) {
	    consumersList.add(newConsumer);
	}

	Collections.shuffle(consumersList);
	Collections.sort(consumersList, new Comparator<Peer>() {

	    @Override
	    public int compare(Peer o1, Peer o2) {
		// Best balance first
		int balanceDiff = actual.getBalance(o1) - actual.getBalance(o2);
		if (balanceDiff != 0) {
		    return balanceDiff > 0 ? -3 : 3;
		}
		int p1Consumer = remoteConsumingPeers.containsKey(o1) ? remoteConsumingPeers.get(o1) : 0;
		int p2Consumer = remoteConsumingPeers.containsKey(o2) ? remoteConsumingPeers.get(o2) : 0;
		int usingDiff = p1Consumer - p2Consumer;
		// Consuming size second
		if (usingDiff != 0) {
		    return usingDiff > 0 ? -2 : 2;
		}

		assert (p2Consumer != 0 && p1Consumer != 0);

		int older = Integer.MAX_VALUE;
		Peer p = o1;

		for (Job j : runningJobs) {
		    if (peersMap.get(getJobOrigSite(j)) == o1 && j.getStartTime() < older) {
			p = o1;
			older = j.getStartTime();
		    } else if (peersMap.get(getJobOrigSite(j)) == o2 && j.getStartTime() < older) {
			p = o2;
			older = j.getStartTime();
		    }
		}
		return p == o1 ? -1 : 1;
	    }
	});

	// distribute resources left using reluctant strategy
	while (resourcesLeft > 0) {
	    for (Peer p : consumersList) {
		allowedResources.put(p, allowedResources.get(p) + 1);
		resourcesLeft--;
		if (resourcesLeft == 0) {
		    break;
		}
	    }
	}

	return allowedResources;
    }

    @Deprecated
    protected void preemptOneJob(HashMap<Peer, Integer> allowedResources, int time) {
	Peer choosen = null;

	LinkedList<Peer> peerList = new LinkedList<Peer>(remoteConsumingPeers.keySet());
	Collections.shuffle(peerList);

	// pega o que estiver usando mais do que merece
	for (Peer p : peerList) {
	    int usedResources = remoteConsumingPeers.get(p);
	    if (usedResources > allowedResources.get(p)) {
		choosen = p;
		break;
	    }
	}

	assert choosen != null;

	// todos os jobs do escolhido que estão rodando
	List<Job> jobs = new LinkedList<Job>();
	for (Job j : runningJobs) {
	    if (peersMap.get(getJobOrigSite(j)) == choosen) {
		jobs.add(j);
	    }
	}

	// get recently start job first
	Collections.sort(jobs, new Comparator<Job>() {

	    @Override
	    public int compare(Job o1, Job o2) {
		return o2.getStartTime() - o1.getStartTime();
	    }
	});
	// if (jobs.size() == 0) { // DEBUG!
	// System.out.println(resources);
	// System.out.println(jobs);
	// System.out.println(allowedResources);
	// System.out.println(peersMap);
	// System.out.println(avaliableResources);
	// System.out.println(this);
	// System.out.println(getRemoteShareSize(time));
	// }
	Job j = jobs.get(0);
	j.preemptJob(time);
	finishOportunisticJob(j, true);
	j.setPeer(null);
	rescheduleJob(j);
    }

    @Deprecated
    private void startJob(Job job) {
	availableResources--;
	Peer peer = peersMap.get(getJobOrigSite(job));
	if (peer == this) {
	    runningLocalJobs.add(job);
	} else {
	    runningJobs.add(job);
	}

	if (peer == this) {
	    return;
	}

	int consumedResources = 0;
	if (this.remoteConsumingPeers.containsKey(peer)) {
	    consumedResources = this.remoteConsumingPeers.get(peer);
	}
	this.remoteConsumingPeers.put(peer, consumedResources + 1);
    }

    @Deprecated
    protected void setBalance(Peer peer, int time) {
	if (peer == this) {
	    return;
	}
	int curBalance = 0;
	if (this.balances.containsKey(peer)) {
	    curBalance = this.balances.get(peer);
	}
	int finalBalance = curBalance + time;
	if (finalBalance < 0) {
	    this.balances.remove(peer);
	} else {
	    this.balances.put(peer, curBalance + time);
	}
    }

    public double getUtilization() {
	return ((double) (resources - availableResources)) / resources;
    }

    @Deprecated
    protected int getRemoteShareSize(int currentTime) {
	return this.resources - this.runningLocalJobs.size();
    }

    @Deprecated
    private void rescheduleJob(Job j) {
	GlobalScheduler.getInstance().schedule(j);
    }

}
