package oursim.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import oursim.entities.ComputableElement;
import oursim.entities.Peer;
import oursim.entities.Task;

public class DefaultSharingPolicy implements ResourceSharingPolicy {

	private static DefaultSharingPolicy instance = null;

	private DefaultSharingPolicy() {
	}

	public static DefaultSharingPolicy getInstance() {
		return instance = (instance != null) ? instance : new DefaultSharingPolicy();
	}

	@Override
	public List<Peer> getPreemptablePeers(final Peer provider, Peer consumer, final HashMap<Peer, Integer> resourcesBeingConsumed,
			final HashSet<? extends ComputableElement> runningElements) {
		ArrayList<Peer> preemptablePeers = new ArrayList<Peer>();
		if (consumer == provider) {
			for (Peer peer : resourcesBeingConsumed.keySet()) {
				if (peer != provider) { 
					preemptablePeers.add(peer);
				}
			}
		}
		assert !preemptablePeers.contains(provider);
		return preemptablePeers;
	}

	@Override
	public long getBalance(Peer source, Peer target) {
		return Long.MAX_VALUE;
	}

	@Override
	public void addPeer(Peer peer) {
	}

	@Override
	public void increaseBalance(Peer source, Peer target, Task task) {
	}

	@Override
	public void decreaseBalance(Peer source, Peer target, Task task) {
	}

	@Override
	public void updateMutualBalance(Peer provider, Peer consumer, Task task) {
	}

}
