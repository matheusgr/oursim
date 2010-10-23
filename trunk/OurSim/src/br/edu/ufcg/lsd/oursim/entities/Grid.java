package br.edu.ufcg.lsd.oursim.entities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grid {

	Map<String, Peer> peers;
	List<Peer> peersAsList;

	public Grid() {
		peers = new HashMap<String, Peer>();
		peersAsList = new ArrayList<Peer>();
	}

	public Grid(Collection<Peer> peers) {
		this();
		this.addPeers(peers);
	}

	public boolean addPeer(Peer peer) {
		if (!this.peers.containsKey(peer.getName())) {
			this.peers.put(peer.getName(), peer);
			this.peersAsList.add(peer);
			return true;
		} else {
			return false;
		}
	}

	public boolean addPeers(Collection<Peer> peers) {
		boolean result = true;
		for (Peer peer : peers) {
			result |= this.addPeer(peer);
		}
		return result;
	}

	public boolean containsPeer(String peerName) {
		return this.peers.containsKey(peerName);
	}

	public long getAmountOfAvailableTime() {
		long availableTime = 0l;
		for (Peer peer : peers.values()) {
			availableTime += peer.getAmountOfAvailableTime();
		}
		return availableTime;
	}

	public long getAmountOfUsefulTime() {
		long usefulTime = 0l;
		for (Peer peer : peers.values()) {
			usefulTime += peer.getAmountOfUsefulTime();
		}
		return usefulTime;
	}

	public long getAmountOfWastedTime() {
		long wastedTime = 0l;
		for (Peer peer : peers.values()) {
			wastedTime += peer.getAmountOfWastedTime();
		}
		return wastedTime;
	}

	public double getUtilization() {
		double utilization = (getAmountOfUsefulTime() / (getAmountOfAvailableTime() * 1.0));
		return utilization;
	}

	public double getTrueUtilization() {
		double realUtilization = ((getAmountOfUsefulTime() + getAmountOfWastedTime()) / (getAmountOfAvailableTime() * 1.0));
		return realUtilization;
	}

	public Map<String, Peer> getMapOfPeers() {
		return peers;
	}

	public List<Peer> getListOfPeers() {
		return peersAsList;
	}

	public Collection<Peer> getPeers() {
		return peers.values();
	}

	public void accountForUtilization(long currentTime, int numberOfEnqueuedTasks) {
		try {
			if (bw != null) {
				double utilization = 0;
				int numberOfAvailableResources = 0;
				for (Peer peer : peers.values()) {
					utilization += peer.getUtilization();
					numberOfAvailableResources += (peer.getNumberOfMachines() - peer.getNumberOfUnavailableResources());
				}
				utilization = utilization / (peers.size() * 1.0);
				bw.append(currentTime + ":" + utilization + ":" + numberOfEnqueuedTasks + ":" + numberOfAvailableResources).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedWriter bw = null;

	public void setUtilizationBuffer(BufferedWriter utilizationBuffer) {
		this.bw = utilizationBuffer;
	}

}
