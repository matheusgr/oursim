package oursim.input;

import java.util.List;
import java.util.PriorityQueue;

import oursim.availability.AvailabilityRecord;
import oursim.entities.Machine;
import oursim.entities.Peer;

public class DedicatedResourcesAvailabilityCharacterization extends InputAbstract<AvailabilityRecord> {

	public DedicatedResourcesAvailabilityCharacterization(List<Peer> peers) {
		this.inputs = new PriorityQueue<AvailabilityRecord>();

		long timestamp = 0;
		long duration = Long.MAX_VALUE;
		for (Peer peer : peers) {
			for (Machine machine : peer.getResources()) {
				this.inputs.add(new AvailabilityRecord(machine.getName(), timestamp, duration));
			}
		}

	}
}
