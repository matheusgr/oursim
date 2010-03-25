package com.googlecode.oursim;

import br.edu.ufcg.lsd.gridsim.Job;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;

public class Worload extends Sim_entity {

	private double delay;
	private Sim_port out;

	public Worload(String name, double delay) {
		super(name);
		this.delay = delay;
		// Port for sending events to the processor
		out = new Sim_port("Workload");
		add_port(out);
	}

	public void body() {
		for (int i = 0; i < 10; i++) {
			// Send the processor a job
			// port, delay, tag, data
			sim_schedule(out, 0.0, 10, new Job(i, 0,0,"1"));
			//sim_schedule(out, 0.0, 0);
			// Pause
			sim_trace(1, "Processed event");
			sim_pause(delay);
		}
	}
	

}
