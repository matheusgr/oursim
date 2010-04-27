package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import oursim.entities.ComputableElement;
import oursim.entities.Peer;

public interface ResourceSharingPolicy {

	void addPeer(Peer peer);

	long getBalance(Peer provider, Peer consumer);

	void updateBalance(Peer provider, Peer consumer, long runTime);

	void updateMutualBalance(Peer provider, Peer consumer, long runTime);

	/**
	 * TODO: O nome desse método não tá legal! O que ele faz é definir os peers que podem ser preemptados.
	 * @param resourcesBeingConsumed
	 *            a quantidade de recursos que cada peer remoto está consumindo
	 *            neste site
	 * @param runningElements
	 *            todos os jobs não locais que estão rodando neste site
	 */
	Map<Peer, Long> calculateAllowedResources(Peer provider, Peer consumer, HashMap<Peer, Integer> resourcesBeingConsumed,
			HashSet<? extends ComputableElement> runningElements);

}