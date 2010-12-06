package br.edu.ufcg.lsd.spotinstancessimulator.ui;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		int spotLimit = 100;
		String spotWorkload = "/home/edigley/local/traces/oursim/03_12_2010/oursim-trace-persistent_50_machines_7_dias_50_sites_1.txt_spot_workload_sorted.txt";
		String spt = "resources/eu-west-1.windows.m2.4xlarge.csv";
		String isd = "../OurSim/input-files/iosup_site_description_50_sites.txt";
		String md = "../OurSim/input-files/machines_speeds_50_sites_50_machines_by_site_1.txt";
		String spotsimTrace = "spotsim_output.txt";

		String spotsimPattern = "spotsim.jar -spot -l %s -bid max -w %s -av %s -pd %s -md %s -o %s";
		String spotsimCMD = String.format(spotsimPattern, spotLimit, spotWorkload, spt, isd, md, spotsimTrace);

		args = spotsimCMD.split("\\s+");
		SpotCLI.main(args);

	}
}
