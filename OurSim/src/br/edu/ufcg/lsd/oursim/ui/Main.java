package br.edu.ufcg.lsd.oursim.ui;

import java.io.IOException;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		args = "-w resources/iosup_workload_7_dias_10_sites.txt -s persistent -pd resources/iosup_site_description.txt -wt iosup -nr 5 -synthetic_av 2678400 -o oursim-trace-5_7_dias_10_sites.txt".split("\\s+");
		CLI.main(args);
	}

}
