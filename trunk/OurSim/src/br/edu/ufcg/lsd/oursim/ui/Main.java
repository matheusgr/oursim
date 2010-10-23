package br.edu.ufcg.lsd.oursim.ui;

import java.io.IOException;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		args = "-w resources/iosup_workload_1_dias_10_sites.txt -s persistent -pd resources/iosup_site_description_10_sites.txt -wt iosup -nr 5 -synthetic_av 86000 -o oursim-trace-5_7_dias_10_sites.txt -u oursim_system_utilization.txt".split("\\s+");
		CLI.main(args);
	}

}
