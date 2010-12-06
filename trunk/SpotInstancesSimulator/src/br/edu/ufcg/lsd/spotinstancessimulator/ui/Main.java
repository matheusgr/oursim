package br.edu.ufcg.lsd.spotinstancessimulator.ui;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		args = "-spot -l 20 -bid max -w spot_workload.txt -av resources/eu-west-1.linux.m1.small.csv -o spot-trace-20_7_dias_10_sites.txt".split("\\s+");

		// SPT=; \
		// ISD=resources/iosup_site_description_50_sites.txt; \
		// MD=resources/machines_speeds_50_sites_50_machines_by_site_1.txt \
		// && \
		// $JAVACALL spotsim.jar -spot -gbp -l 2147483647 -bid max -w
		// resources/oursim-trace-persistent_50_machines_7_dias_50_sites_1.txt_spot_workload_sorted.txt
		// -av $SPT -o
		// spot-trace-persistent_50_machines_7_dias_50_sites_2147483647_spotLimit_groupedbypeer_true_av_us-east-1.linux.c1.medium.csv_1.txt
		// -u
		// spot-trace-utilization-persistent_50_machines_7_dias_50_sites_2147483647_spotLimit_groupedbypeer_true_av_us-east-1.linux.c1.medium.csv_1.txt
		// -pd $ISD -md $MD && \
		//		  
		//		
		String scheduler = "persistent";
		String nReplicas = "";
		int rodada = 1;
		int nRes = 50;
		int nSites = 50;
		int spotLimit = 2147483647;
		String sourceDir = "../OurSim/resources/";
		String spotWorkload = "oursim-trace-" + scheduler + "_" + nRes + "_machines_7_dias_" + nSites + "_sites_" + rodada + ".txt_spot_workload_sorted.txt";
		String spt = "us-east-1.linux.c1.medium.csv";
		String isd = "iosup_site_description_" + nSites + "_sites.txt";
		String md = "machines_speeds_" + nSites + "_sites_" + nRes + "_machines_by_site_1.txt";
		String spotsimTrace = String.format("spot-trace-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_true_av_%s_%s.txt", scheduler, nRes, nSites,
				spotLimit, spt, rodada);

		String uSpotFile = String.format("spot-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);

		String spotsimPattern = "spotsim.jar -spot -gbp -l %s -bid max -w %s -av %s -o %s -u %s -pd %s -md %s";
		String spotsimCMD = String.format(spotsimPattern, spotLimit, sourceDir + spotWorkload, sourceDir + spt, spotsimTrace, uSpotFile, sourceDir + isd,
				sourceDir + md);

		args = spotsimCMD.split("\\s+");
		args = "-spot -gbp -l 100 -bid max -w ../OurSim/resources/oursim-trace-persistent_50_machines_7_dias_50_sites_10.txt_spot_workload_sorted.txt -av  ../OurSim/resources/us-east-1.linux.m1.small.csv  -o spot-trace-persistent_50_machines_7_dias_50_sites_100_spotLimit_groupedbypeer_true_av_us-east-1.linux.m1.small.csv_10.txt -u spot-trace-utilization-persistent_50_machines_7_dias_50_sites_100_spotLimit_groupedbypeer_true_av_us-east-1.linux.m1.small.csv_10.txt -pd  ../OurSim/resources/iosup_site_description_50_sites.txt  -md  ../OurSim/resources/machines_speeds_50_sites_50_machines_by_site_10.txt"
				.split("\\s+");
		SpotCLI.main(args);

	}
}
