package br.edu.ufcg.lsd.oursim.ui;

import java.io.IOException;

import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		 String workload = "resources/iosup_workload_7_dias_10_sites.txt";
//		String workload = "/local/edigley/traces/oursim/marcus_workload_7_dias_20_sites.txt";
		String peersDescription = "resources/iosup_site_description_10_sites.txt";
		String machinesDescription = "resources/machines_speeds_10_sites_25_machines_by_site.txt";
		String scheduler = "persistent";
//		String workloadType = "marcus2";
		String workloadType = "iosup";
		String numberOfResources = "25";
		String simulationDuration = String.valueOf(TimeUtil.ONE_WEEK + TimeUtil.ONE_HOUR);

		String output = "oursim-trace-5_7_dias_10_sites.txt";
		String utilization = "oursim_system_utilization.txt";

		String argsPattern = "-w %s -s %s -pd %s -md %s -wt %s -nr %s -synthetic_av %s -o %s -u %s -we worker_events_refactored.txt";
		String argsString = String.format(""+argsPattern, workload, scheduler, peersDescription, machinesDescription, workloadType, numberOfResources,
				simulationDuration, output, utilization);
		args = argsString.split("\\s+");
		// args = "-w resources/iosup_workload_1_dias_10_sites.txt -s persistent
		// -pd resources/iosup_site_description_10_sites.txt -md
		// resources/machines_speeds_10_sites_25_machines_by_site.txt -wt iosup
		// -nr 25 -synthetic_av 86000 -o oursim-trace-5_7_dias_10_sites.txt -u
		// oursim_system_utilization.txt"
		// .split("\\s+");
		CLI.main(args);
	}

}
