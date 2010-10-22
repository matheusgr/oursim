package br.edu.ufcg.lsd.oursim.entities;

import java.util.ArrayList;
import java.util.List;

public class Grid {

	private List<Peer> peers;

	public Grid() {
		peers = new ArrayList<Peer>();
	}

	public boolean addPeer(Peer peer) {
		if (!this.peers.contains(peer)) {
			this.peers.add(peer);
			return true;
		} else {
			return false;
		}
	}

	public long getAmountOfAvailableTime() {
		long availableTime = 0l;
		for (Peer peer : peers) {
			availableTime += peer.getAmountOfAvailableTime();
		}
		return availableTime;
	}

	public long getAmountOfUsefulTime() {
		long usefulTime = 0l;
		for (Peer peer : peers) {
			usefulTime += peer.getAmountOfUsefulTime();
		}
		return usefulTime;
	}

	public long getAmountOfWastedTime() {
		long wastedTime = 0l;
		for (Peer peer : peers) {
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

}
