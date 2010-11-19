package br.edu.ufcg.lsd.oursim.ui;

import java.io.IOException;

import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String workload = "resources/marcus_workload_7_dias_50_sites_1.txt";
		String peersDescription = "resources/iosup_site_description_50_sites.txt";
		String machinesDescription = "resources/machines_speeds_50_sites_25_machines_by_site_1.txt";
		String scheduler;
		scheduler = "persistent";
		scheduler = "replication";
		String nReplicas = scheduler.equals("replication") ? "3" : "";

		String workloadType = "marcus";
		String numberOfResources = "25";
		String simulationDuration = String.valueOf(TimeUtil.ONE_WEEK + TimeUtil.ONE_HOUR);

		String output = "oursim-trace-25_7_dias_50_sites.txt";
		String utilization = "oursim_system_utilization.txt";

		String argsPattern = "-w %s -s %s -pd %s -md %s -wt %s -nr %s -synthetic_av %s -o %s -u %s";// -we
																									// worker_events_refactored.txt";
		String argsString = String.format("" + argsPattern, workload, scheduler + " " + nReplicas, peersDescription, machinesDescription, workloadType,
				numberOfResources, simulationDuration, output, utilization);
		args = argsString.split("\\s+");
		CLI.main(args);
	}

}
