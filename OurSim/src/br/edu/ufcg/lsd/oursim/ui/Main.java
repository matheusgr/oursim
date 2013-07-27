package br.edu.ufcg.lsd.oursim.ui;

import java.io.IOException;

import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//String workload = "resources/marcus_workload_1_dias_10_sites_1.txt";
		//String workload = "/home/edigley/local/traces/oursim/marcus/new_workload/teste_geracao_de_workload/marcus_workload_7_dias_30_sites_1.txt";
//		String workload = "input-files/head_marcus_workload_7_dias_30_sites_1.txt";
		String workload = "input-files/marcus_workload_7_dias_10_sites_11.txt";
		String peersDescription = "input-files/iosup_site_description_10_sites.txt";
		String machinesDescription = "input-files/machines_speeds_10_sites_30_machines_by_site_11.txt";
		String scheduler;
		scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "3" : "";
		String schedulerOption = scheduler + " " + nReplicas;

		String workloadType = "marcus";
		String avDuration = "ourgrid";//String.valueOf(TimeUtil.ONE_WEEK + TimeUtil.ONE_DAY);

		String output = "oursim-trace-20_7_dias_10_sites.txt";
		String utilizationFile = "oursim_system_utilization.txt";
		String workerEventsFile = "oursim_worker_events.txt";
		String taskEventsFile = "oursim_task_events.txt";

		String u = "";
		String te = "";
		String we = "";

		/* Descomente as linhas abaixo se quiser rastrear os respectivos eventos */
		/* -u <file> : a cada evento registra a utilizacao do sistem */
		// u = String.format("-u %s", utilizationFile);
		/* -te <file> : registra todos os eventos envolvendo as tasks */
		// te = String.format("-te %s", taskEventsFile);
		/* -we <file> : registra todos os eventos de (in)disponibilidade */
		// we = String.format("-we %s", workerEventsFile);
		String optional = String.format(" %s %s %s ", u, te, we);

		String argsPattern = "-h 20000 -wt %s -w %s -s %s -pd %s -md %s -synthetic_av %s -o %s %s -prs 45354 -acs 89345 3245324 7963452 12342 13 9035";

		String argsString = String.format(argsPattern, workloadType, workload, schedulerOption, peersDescription, machinesDescription, avDuration, output,
				optional);
		
//		argsString = " -w input-files/workload_test.txt -wt marcus -s persistent  -pd  input-files/iosup_site_description_10_sites.txt  -nr 30 -synthetic_av ourgrid -o oursim-trace-persistent_30_machines_7_dias_10_sites_1.txt  -md  input-files/machines_speeds_10_sites_30_machines_by_site_1.txt  -prs 9354269 -acs  1234 13455 5566 6548 8764 5674 ";
		
		args = argsString.split("\\s+");
		// System.out.println(argsString);
		CLI.main(args);
	}

}
