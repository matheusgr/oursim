package com.googlecode.oursim;

import java.util.HashSet;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;

public class DiscoveryService extends Sim_entity {
	// The class for the processor
	private Sim_port in;
	private HashSet<Sim_port> outs;

	DiscoveryService(String name) {
		super(name);
		// Port for receiving events from the source
		Sim_stat stat = new Sim_stat();
		stat.add_measure(Sim_stat.THROUGHPUT);
		set_stat(stat);
		in = new Sim_port("InWorkload");
		outs = new HashSet<Sim_port>();
		// Port for sending events to disk 1
		add_port(in);
	}


	public void addPeer(String peerEntity) {
		Sim_port out = new Sim_port("DS" + peerEntity);
		add_port(out);
		System.out.println(out.get_pname());
		outs.add(out);
		Sim_system.link_ports(get_name(), out.get_pname(), peerEntity, "DS_In");
	}

	public void body() {
		while (Sim_system.running()) {
			Sim_event e = new Sim_event();
			// Get the next event
			sim_get_next(e);
			if (e.get_tag() != -1) // end of simulation
				sim_trace(1, "Processed event" + e.get_tag());
			//System.out.println(e.get_data());
			//System.out.println(e.get_tag()); 
			// Process the event
			//sim_process(delay);
			// The event has completed service
			//sim_completed(e);
			for (Sim_port out : outs) {
				sim_schedule(out, 0.0, 100, e.get_data());
			}
		}
	}
	
}
