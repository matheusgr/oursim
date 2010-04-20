package oursim.policy;

import java.util.Collections;
import java.util.List;

import oursim.Parameters;
import oursim.entities.Peer;

public class ResourceRequestPolicy {

    public void request(Peer consumer, List<Peer> peers) {
	// // Getting best balance first
	// Collections.sort(peers, new Comparator<Peer>() {
	// @Override
	// public int compare(Peer o1, Peer o2) {
	// return o2.getBalance(consumer)
	// - o1.getBalance(consumer);
	// }
	//
	// });
	Collections.shuffle(peers,Parameters.RANDOM);
	// Trying own resources first:
	for (int i = 0; i < peers.size(); i++) {
		if (peers.get(i) == consumer) {
			peers.set(i, peers.get(0));
			peers.set(0, consumer);
			break;
		}
	}
	assert (peers.get(0) == consumer);
    }

}
