package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import oursim.entities.ComputableElement;
import oursim.entities.Peer;

public interface ResourceSharingPolicy {

	public abstract void addPeer(Peer peer);

	public abstract long getBalance(Peer provider, Peer consumer);

	public abstract void updateBalance(Peer provider, Peer consumer, long runTime);

	public abstract void updateMutualBalance(Peer provider, Peer consumer, long runTime);

	/**
	 * @param resourcesBeingConsumed
	 *            a quantidade de recursos que cada peer remoto está consumindo
	 *            neste site
	 * @param runningElements
	 *            todos os jobs não locais que estão rodando neste site
	 */
	public abstract TreeMap<Peer, Integer> calculateAllowedResources(Peer provider, Peer consumer, HashMap<Peer, Integer> resourcesBeingConsumed,
			HashSet<? extends ComputableElement> runningElements);

}