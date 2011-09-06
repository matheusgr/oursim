package br.edu.ufcg.lsd.spotinstancessimulator.ui;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		int spotLimit = 100;
		String spotWorkload = "/home/edigley/local/traces/oursim/03_12_2010/oursim-trace-persistent_50_machines_7_dias_50_sites_1.txt_spot_workload_sorted.txt";
//		spotWorkload = "../OurSim/input-files/oursim-trace-persistent_25_machines_7_dias_50_sites_1.txt_spot_workload_sorted.txt";
		spotWorkload = "../OurSim/input-files/marcus_workload_7_dias_50_sites_1.txt";
//		spotWorkload = "../OurSim/input-files/oursim-trace-persistent_50_machines_7_dias_50_sites_1.txt_spot_workload_sorted.txt";
		String spt = "resources/eu-west-1.windows.m2.4xlarge.csv";
		String isd = "../OurSim/input-files/iosup_site_description_50_sites.txt";
		String md = "../OurSim/input-files/machines_speeds_50_sites_50_machines_by_site_1.txt";
		String spotsimTrace = "spotsim_output.txt";

		String spotsimPattern = "spotsim.jar -spot -l %s -bid max -w %s -av %s -pd %s -md %s -o %s";
		String spotsimCMD = String.format(spotsimPattern, spotLimit, spotWorkload, spt, isd, md, spotsimTrace);

		args = spotsimCMD.split("\\s+");
		args = "-spot  -l 100 -bid max -w input-files/marcus_workload_7_dias_10_sites_2.txt -av input-files/us-east-1.linux.m1.small.csv -o spot-trace-persistent_30_machines_7_dias_10_sites_100_spotLimit_groupedbypeer_false_av_us-east-1.linux.m1.small.csv_2.txt  -pd  input-files/iosup_site_description_10_sites.txt  -md  input-files/machines_speeds_10_sites_30_machines_by_site_2.txt  -ait  input-files/ec2_instances.txt".split("\\s+");
		SpotCLI.main(args);

	}
}
