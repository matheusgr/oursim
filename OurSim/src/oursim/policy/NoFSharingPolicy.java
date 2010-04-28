package oursim.policy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import oursim.entities.ComputableElement;
import oursim.entities.Peer;
import oursim.entities.Task;

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

	private void updateBalance(Peer provider, Peer consumer, long balance) {
		HashMap<Peer, Long> balances = allBalances.get(provider);
		long finalBalance = getBalance(consumer, balances) + balance;
		if (finalBalance < 0) {
			balances.remove(consumer);
		} else {
			balances.put(consumer, finalBalance);
		}
	}

	@Override
	public void increaseBalance(Peer source, Peer target, Task task) {
		assert target != source;
		if (task.isFinished()) {
			updateBalance(source, target, task.getRunningTime());
		}

	}

	@Override
	public void decreaseBalance(Peer source, Peer target, Task task) {
		if (task.isFinished()) {
			updateBalance(source, target, -task.getRunningTime());
		}
	}

	@Override
	public void updateMutualBalance(Peer provider, Peer consumer, Task task) {
		// Don't update the balance to itself
		if (provider != consumer) {
			decreaseBalance(provider, consumer, task);
			increaseBalance(consumer, provider, task);
		}
	}

	@Override
	public long getBalance(Peer source, Peer target) {
		assert allBalances.containsKey(source);
		HashMap<Peer, Long> balances = allBalances.get(source);
		return getBalance(target, balances);
	}

	private long getBalance(Peer consumer, HashMap<Peer, Long> balances) {
		return balances.containsKey(consumer) ? balances.get(consumer) : 0;
	}

	@Override
	public List<Peer> getPreemptablePeers(final Peer provider, Peer consumer, final HashMap<Peer, Integer> resourcesBeingConsumed,
			final HashSet<? extends ComputableElement> runningElements) {

		List<Peer> peersByPriorityOfPreemption = sortPeersByPriorityOfPreemption(provider, consumer, resourcesBeingConsumed, runningElements);
		peersByPriorityOfPreemption.remove(consumer);
		return peersByPriorityOfPreemption;
	}

	private List<Peer> sortPeersByPriorityOfPreemption(final Peer provider, Peer consumer, final HashMap<Peer, Integer> resourcesBeingConsumed,
			final HashSet<? extends ComputableElement> runningElements) {
		assert allBalances.containsKey(provider);

		HashMap<Peer, Long> balances = allBalances.get(provider);

		long resourcesToShare = provider.getAmountOfResourcesToShare();

		// a soma dos balances de todos os peers neste provedor
		int totalBalance = 0;

		// quanto cada peer merece neste provedor, ordenado pelos criterios de
		// desempate.
		TreeMap<Peer, Long> preemptablePeers = new TreeMap<Peer, Long>(new Comparator<Peer>() {
			@Override
			public int compare(Peer peer1, Peer peer2) {

				if (peer1 == peer2) {
					return 0;
				}

				// Local first
				if (peer1 == provider) {
					return -4;
				}

				// Best balance first
				long balanceDiff = getBalance(provider, peer1) - getBalance(provider, peer2);
				if (balanceDiff != 0) {
					return balanceDiff > 0 ? -3 : 3;
				}
				int p1Consumer = resourcesBeingConsumed.containsKey(peer1) ? resourcesBeingConsumed.get(peer1) : 0;
				int p2Consumer = resourcesBeingConsumed.containsKey(peer2) ? resourcesBeingConsumed.get(peer2) : 0;
				int usingDiff = p1Consumer - p2Consumer;

				// Consuming size second
				if (usingDiff != 0) {
					return usingDiff > 0 ? -2 : 2;
				}

				assert (p2Consumer != 0 || p1Consumer != 0);

				long older = Long.MAX_VALUE;
				Peer p = peer1;

				// Recently job last
				for (ComputableElement j : runningElements) {
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

		HashMap<Peer, Integer> resourcesBeingConsumedClone = new HashMap<Peer, Integer>(resourcesBeingConsumed);

		// If this peer is not consuming, put in this map.
		if (!resourcesBeingConsumedClone.containsKey(consumer)) {
			resourcesBeingConsumedClone.put(consumer, 0);
		}

		for (Peer remoteConsumer : resourcesBeingConsumedClone.keySet()) {
			totalBalance += getBalance(remoteConsumer, balances);
		}

		long resourcesLeft = resourcesToShare;

		// quantidade de peers que já estão consumindo neste provedor
		int numConsumingPeers = resourcesBeingConsumedClone.size();

		// Set minimum resources allowed for each peer
		for (Peer remoteConsumer : resourcesBeingConsumedClone.keySet()) {
			long resourcesForPeer = 0;
			double share;
			if (totalBalance == 0) {
				share = (1.0d / numConsumingPeers);
			} else {
				share = ((double) getBalance(remoteConsumer, balances)) / totalBalance;
			}
			resourcesForPeer = (int) (share * resourcesToShare);
			int resourcesInUse = resourcesBeingConsumedClone.get(remoteConsumer);
			// If consuming is using more resources than allowed
			if (consumer == remoteConsumer) { // Resource want one more
				// machine
				if (resourcesInUse + 1 > resourcesForPeer) {
					preemptablePeers.put(remoteConsumer, resourcesForPeer);
					return new ArrayList<Peer>(preemptablePeers.keySet());
				}
			}
			if (Math.min(resourcesInUse, resourcesForPeer) > resourcesForPeer) {
				preemptablePeers.put(remoteConsumer, resourcesForPeer);
			}
			resourcesLeft -= resourcesInUse;
		}

		// consumer will not get any resource from this provider
		if (preemptablePeers.containsKey(consumer)) {
			return new ArrayList<Peer>(preemptablePeers.keySet());
		}

		// distribute resources left using reluctant strategy
		while (resourcesLeft > 0) {
			for (Iterator<Peer> iterator = preemptablePeers.keySet().iterator(); iterator.hasNext();) {
				Peer peer = iterator.next();
				if (resourcesBeingConsumedClone.get(peer) < preemptablePeers.get(peer)) {
					preemptablePeers.put(peer, preemptablePeers.get(peer) + 1);
					resourcesLeft--;
					if (preemptablePeers.get(peer) >= resourcesBeingConsumedClone.get(peer)) {
						iterator.remove();
					}
				}
				if (resourcesLeft == 0) {
					break;
				}
			}
		}

		assert (resourcesLeft == 0);

		return new ArrayList<Peer>(preemptablePeers.keySet());
	}

}
