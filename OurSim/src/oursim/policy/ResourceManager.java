package oursim.policy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import oursim.entities.Machine;
import oursim.entities.Peer;

public class ResourceManager {

	private Peer peer;

	private Map<String, Machine> allocated;

	private Map<String, Machine> free;

	private Map<String, Machine> unavailable;

	public ResourceManager(Peer peer) {

		this.peer = peer;

		this.free = new HashMap<String, Machine>();
		this.allocated = new HashMap<String, Machine>();
		this.unavailable = new HashMap<String, Machine>();

		for (Machine machine : this.peer.getResources()) {
			this.unavailable.put(machine.getName(), machine);
		}

	}

	public Machine allocateResource() {
		assert this.hasAvailableResource();
		Iterator<Machine> it = free.values().iterator();
		Machine chosen = it.next();
		it.remove();
		this.allocated.put(chosen.getName(), chosen);
		return chosen;
	}

	public void releaseResource(Machine resource) {
		assert resource != null;
		this.releaseResource(resource.getName());
	}

	public void releaseResource(String machineName) {
		assert this.allocated.containsKey(machineName) : machineName;
		Machine resource = this.allocated.remove(machineName);
		this.free.put(resource.getName(), resource);
	}

	public void makeResourceUnavailable(String machineName) {
		assert this.allocated.containsKey(machineName) || this.free.containsKey(machineName);

		Machine resource = this.allocated.containsKey(machineName) ? this.allocated.remove(machineName) : this.free.remove(machineName);

		this.unavailable.put(resource.getName(), resource);
	}

	public void makeResourceAvailable(String machineName) {
		assert this.unavailable.containsKey(machineName) : machineName;
		Machine resource = this.unavailable.remove(machineName);
		this.free.put(machineName, resource);
	}

	public boolean isAllocated(String machineName) {
		return this.allocated.containsKey(machineName);
	}

	public boolean isAllocated(Machine machine) {
		return isAllocated(machine.getName());
	}

	public boolean hasAvailableResource() {
		return !this.free.isEmpty();
	}

	public int getAvailableResources() {
		return free.size();
	}

	public int getAmountOfResources() {
		return this.peer.getAmountOfResources();
	}

	public Machine getResource(String machineName) {
		if (this.free.containsKey(machineName)) {
			return this.free.get(machineName);
		} else if (this.allocated.containsKey(machineName)) {
			return this.allocated.get(machineName);
		} else {
			return this.unavailable.get(machineName);
		}
	}

	public boolean hasResource(String machineName) {
		return this.free.containsKey(machineName) || this.allocated.containsKey(machineName) || this.unavailable.containsKey(machineName);
	}

}