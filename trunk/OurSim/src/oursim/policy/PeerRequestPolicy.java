package oursim.policy;

import java.util.Collections;
import java.util.List;

import oursim.Parameters;
import oursim.entities.Peer;

public class PeerRequestPolicy extends RequestPolicy<Peer, Peer> {

	public PeerRequestPolicy(Peer requester) {
		super(requester);
	}

	@Override
	public void prioritize(List<Peer> peers) {
		// // Getting best balance first
		// Collections.sort(peers, new Comparator<Peer>() {
		// @Override
		// public int compare(Peer o1, Peer o2) {
		// return o2.getBalance(consumer)
		// - o1.getBalance(consumer);
		// }
		//
		// });
		Collections.shuffle(peers, Parameters.RANDOM);
		// Trying own resources first:
		for (int i = 0; i < peers.size(); i++) {
			if (peers.get(i) == requester) {
				peers.set(i, peers.get(0));
				peers.set(0, requester);
				break;
			}
		}
		assert (peers.get(0) == requester);
	}

}
