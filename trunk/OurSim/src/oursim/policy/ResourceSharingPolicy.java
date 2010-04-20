package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;

import oursim.entities.Job;
import oursim.entities.Peer;

public interface ResourceSharingPolicy {

    public abstract void setBalance(Peer provider, Peer consumer, long runTime);

    public abstract long getBalance(Peer provider, Peer consumer);

    /**
     * @param provider
     * @param consumer
     * @param balances
     * @param remoteConsumingPeers
     *                a quantidade de recursos que o peer remoto está consumindo
     *                neste site
     * @param currentTime
     * @return
     */
    public abstract HashMap<Peer, Integer> calculateAllowedResources(Peer provider, Peer consumer, final HashMap<Peer, Integer> remoteConsumingPeers,
	    final HashSet<Job> runningJobs);

    public abstract void addPeer(Peer peer);

}