package oursim.policy;

import java.util.Collections;
import java.util.List;

import oursim.Parameters;
import oursim.entities.Peer;

public class RequestPolicy {

    public void request(List<Peer> peers) {
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
    }

}
