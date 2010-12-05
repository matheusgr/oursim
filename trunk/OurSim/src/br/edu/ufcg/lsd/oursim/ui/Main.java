package br.edu.ufcg.lsd.oursim.ui;

import java.io.IOException;

import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String workload = "resources-/marcus_workload_7_dias_50_sites_2.txt";
		String peersDescription = "resources-/iosup_site_description_50_sites.txt";
		String machinesDescription = "resources-/machines_speeds_50_sites_50_machines_by_site_2.txt";
		String scheduler;
		scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "3" : "";

		String workloadType = "marcus";
		String numberOfResources = "25";
		String simulationDuration = String.valueOf(TimeUtil.ONE_HOUR + TimeUtil.ONE_HOUR);

		String output = "oursim-trace-25_7_dias_50_sites.txt";
		String utilization = "oursim_system_utilization.txt";

		String argsPattern = "-v -w %s -s %s -pd %s -md %s -wt %s -nr %s -synthetic_av %s -o %s -u %s";// -te /tmp/te.txt -we /tmp/we.txt";

		String argsString = String.format("" + argsPattern, workload, scheduler + " " + nReplicas, peersDescription, machinesDescription, workloadType,
				numberOfResources, simulationDuration, output, utilization);
		
		argsString = "-w resources/marcus_workload_7_dias_10_sites_14.txt -wt marcus -s persistent  -pd  resources/iosup_site_description_10_sites.txt  -nr 35 -synthetic_av 640800 -o oursim-trace-persistent_35_machines_7_dias_10_sites_14.txt  -md  resources/machines_speeds_10_sites_35_machines_by_site_14.txt";
		
		args = argsString.split("\\s+");
		CLI.main(args);
	}

}
