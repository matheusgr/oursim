package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import oursim.entities.ComputableElement;
import oursim.entities.Peer;
import oursim.entities.Task;

public interface ResourceSharingPolicy {

	void addPeer(Peer peer);

	long getBalance(Peer source, Peer target);

	void increaseBalance(Peer source, Peer target, Task task);

	void decreaseBalance(Peer source, Peer target, Task task);

	void updateMutualBalance(Peer provider, Peer consumer, Task task);

	/**
	 * TODO: O nome desse método não tá legal! O que ele faz é definir os peers
	 * que podem ser preemptados.
	 * 
	 * @param resourcesBeingConsumed
	 *            a quantidade de recursos que cada peer remoto está consumindo
	 *            neste site
	 * @param runningElements
	 *            todos os jobs não locais que estão rodando neste site
	 */
	List<Peer> getPreemptablePeers(Peer provider, Peer consumer, HashMap<Peer, Integer> resourcesBeingConsumed,
			HashSet<? extends ComputableElement> runningElements);

}