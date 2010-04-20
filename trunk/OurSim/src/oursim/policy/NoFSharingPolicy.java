package oursim.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import oursim.Parameters;
import oursim.entities.Job;
import oursim.entities.Peer;

public class NoFSharingPolicy implements ResourceSharingPolicy {

    private Map<Peer, HashMap<Peer, Long>> allBalances;

    private static NoFSharingPolicy instance = null;

    private NoFSharingPolicy() {
	allBalances = new HashMap<Peer, HashMap<Peer, Long>>();
    }

    public static NoFSharingPolicy getInstance() {
	return instance = (instance != null) ? instance : new NoFSharingPolicy();
    }

    @Override
    public void addPeer(Peer peer) {
	this.allBalances.put(peer, new HashMap<Peer, Long>());
    }

    @Override
    public void setBalance(Peer provider, Peer consumer, long runTime) {
	assert consumer != provider;
	HashMap<Peer, Long> balances = allBalances.get(provider);

	long finalBalance = getBalance(consumer, balances) + runTime;

	if (finalBalance < 0) {
	    balances.remove(consumer);
	} else {
	    balances.put(consumer, finalBalance);
	}

    }

    @Override
    public long getBalance(Peer provider, Peer consumer) {
	assert allBalances.containsKey(provider);
	HashMap<Peer, Long> balances = allBalances.get(provider);
	return getBalance(consumer, balances);
    }

    private long getBalance(Peer consumer, HashMap<Peer, Long> balances) {
	return balances.containsKey(consumer) ? balances.get(consumer) : 0;
    }

    public HashMap<Peer, Integer> calculateAllowedResources(final Peer provider, Peer consumer, final HashMap<Peer, Integer> remoteConsumingPeers,
	    final HashSet<Job> runningJobs) {
	assert allBalances.containsKey(provider);
	HashMap<Peer, Long> balances = allBalances.get(provider);

	long resourcesToShare = provider.getAmountOfResourcesToShare();

	// If its local, remove one resource from remote
	if (consumer == provider) {
	    resourcesToShare -= 1;
	}

	// a soma dos balances de todos os peers neste provedor
	int totalBalance = 0;

	// quanto cada peer merece neste provedor
	HashMap<Peer, Integer> allowedResources = new HashMap<Peer, Integer>();

	for (Peer remoteConsumer : remoteConsumingPeers.keySet()) {
	    totalBalance += getBalance(remoteConsumer, balances);
	}

	// pois se não for novo já está na contagem anterior
	totalBalance += remoteConsumingPeers.containsKey(consumer) ? 0 : getBalance(consumer, balances);

	long resourcesLeft = resourcesToShare;

	// quantidade de peers que já estão consumindo neste provedor
	int numConsumingPeers = remoteConsumingPeers.size();
	numConsumingPeers += remoteConsumingPeers.containsKey(consumer) ? 0 : 1;

	// Set minimum resources allowed for each peer
	for (Peer remoteConsumer : remoteConsumingPeers.keySet()) {
	    int resourcesForPeer = 0;
	    double share;
	    if (totalBalance == 0) {
		share = (1.0d / numConsumingPeers);
	    } else {
		share = ((double) getBalance(remoteConsumer, balances)) / totalBalance;
	    }
	    resourcesForPeer = (int) (share * resourcesToShare);
	    allowedResources.put(remoteConsumer, resourcesForPeer);
	    resourcesLeft -= resourcesForPeer;
	}

	// para o caso do consumer ser um novato neste provedor
	if (!remoteConsumingPeers.containsKey(consumer)) {
	    int resourcesForPeer = 0;
	    double share;
	    if (totalBalance == 0) {
		share = (1.0d / numConsumingPeers);
	    } else {
		share = ((double) getBalance(consumer, balances)) / totalBalance;
	    }
	    resourcesForPeer = (int) (share * resourcesToShare);
	    allowedResources.put(consumer, resourcesForPeer);
	    resourcesLeft -= resourcesForPeer;
	}

	// recursos que o peer em questão já está utilizando
	int resourcesInUse = remoteConsumingPeers.containsKey(consumer) ? remoteConsumingPeers.get(consumer) : 0;

	if (consumer != provider && resourcesInUse <= allowedResources.get(consumer)) {
	    return allowedResources;
	}

	ArrayList<Peer> consumersList = new ArrayList<Peer>(remoteConsumingPeers.size() + 1);
	consumersList.addAll(remoteConsumingPeers.keySet());
	if (!remoteConsumingPeers.containsKey(consumer)) {
	    consumersList.add(consumer);
	}

	Collections.shuffle(consumersList, Parameters.RANDOM);
	Collections.sort(consumersList, new Comparator<Peer>() {

	    @Override
	    public int compare(Peer peer1, Peer peer2) {
		// Best balance first
		long balanceDiff = getBalance(provider, peer1) - getBalance(provider, peer2);
		if (balanceDiff != 0) {
		    return balanceDiff > 0 ? -3 : 3;
		}
		int p1Consumer = remoteConsumingPeers.containsKey(peer1) ? remoteConsumingPeers.get(peer1) : 0;
		int p2Consumer = remoteConsumingPeers.containsKey(peer2) ? remoteConsumingPeers.get(peer2) : 0;
		int usingDiff = p1Consumer - p2Consumer;
		// Consuming size second
		if (usingDiff != 0) {
		    return usingDiff > 0 ? -2 : 2;
		}

		assert (p2Consumer != 0 && p1Consumer != 0);

		long older = Long.MAX_VALUE;
		Peer p = peer1;

		for (Job j : runningJobs) {
		    if (j.getSourcePeer() == peer1 && j.getStartTime() < older) {
			p = peer1;
			older = j.getStartTime();
		    } else if (j.getSourcePeer() == peer2 && j.getStartTime() < older) {
			p = peer2;
			older = j.getStartTime();
		    }
		}
		return p == peer1 ? -1 : 1;
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

}
