package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import oursim.entities.ComputableElement;
import oursim.entities.Peer;

public class DefaultSharingPolicy implements ResourceSharingPolicy {

	private static DefaultSharingPolicy instance = null;

	private DefaultSharingPolicy() {
	}

	public static DefaultSharingPolicy getInstance() {
		return instance = (instance != null) ? instance : new DefaultSharingPolicy();
	}

	@Override
	public void addPeer(Peer peer) {
	}

	@Override
	public void updateBalance(Peer provider, Peer consumer, long balance) {
	}

	@Override
	public void updateMutualBalance(Peer provider, Peer consumer, long runTimeDuration) {
	}

	@Override
	public long getBalance(Peer provider, Peer consumer) {
		return Long.MAX_VALUE;
	}

	@Override
	public Map<Peer, Long> calculateAllowedResources(final Peer provider, Peer consumer, final HashMap<Peer, Integer> resourcesBeingConsumed,
			final HashSet<? extends ComputableElement> runningElements) {
		HashMap<Peer, Long> preemptablePeers = new HashMap<Peer, Long>();
		return preemptablePeers;
	}

}
