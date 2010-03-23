package com.googlecode.oursim;

import eduni.simjava.Sim_system;

public class OurSim {

	public static void main(String[] args) {
		Sim_system.initialise();
		// Workload
		new Worload("Workload", 50);
		// Add the processor
		DiscoveryService rs = new DiscoveryService("DiscoveryService");
		for (int i = 0; i < 100; i++) {
			PeerEntity pe = new PeerEntity("Peer" + i);
			rs.addPeer(pe.get_name());
		}
		Sim_system.link_ports("Workload", "Workload", "DiscoveryService", "InWorkload");
		// Run the simulation
		Sim_system.set_trace_detail(false, true, false);
		//Sim_system.set_trace_detail(true, true, true); // waiting.. entity trace... process start
		//Sim_system.set_termination_condition(Sim_system.EVENTS_COMPLETED, "Peer0", 100, 8, false);
		Sim_system.run();
	}
	
}
