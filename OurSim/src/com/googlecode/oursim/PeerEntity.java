package com.googlecode.oursim;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;

public class PeerEntity extends Sim_entity{

	private Sim_port in;

	public PeerEntity(String name) {
		super(name);
		// Port for receiving events from the source
		Sim_stat stat = new Sim_stat();
		stat.add_measure(Sim_stat.THROUGHPUT);
		set_stat(stat);
		in = new Sim_port("DS_In");
		// Port for sending events to disk 1
		add_port(in);
	}
	
	public void body() {
		while (Sim_system.running()) {
			Sim_event e = new Sim_event();
			// Get the next event
			sim_get_next(e);
			// Process the event
			sim_process(10.0);
			if (e.get_tag() != -1) // end of simulation
				sim_trace(1, "Processed event... " + e.get_data() + " / " + e.get_tag());
			// The event has completed service
			sim_completed(e);
		}
	}

}
