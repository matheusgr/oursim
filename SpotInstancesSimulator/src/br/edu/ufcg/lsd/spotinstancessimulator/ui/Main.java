package br.edu.ufcg.lsd.spotinstancessimulator.ui;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		args = "-spot -l 20 -bid max -w spot_workload.txt -av resources/eu-west-1.linux.m1.small.csv -o spot-trace-20_7_dias_10_sites.txt".split("\\s+");
		
		String scheduler = "persistent";
		String nReplicas = "";
		int rodada = 2;
		int nRes = 25;
		int nSites = 75;
		int spotLimit = 100;
		String spotWorkload = "/home/edigley/local/traces/oursim/13_11_2010/oursim-trace-"+scheduler+"_"+nRes+"_machines_7_dias_"+nSites+"_sites_"+rodada+".txt_spot_workload_sorted.txt";
		String spt = "resources/eu-west-1.linux.m1.small.csv";
		String isd = "../OurSim/resources/iosup_site_description_"+nSites+"_sites.txt";
		String md = "../OurSim/resources/machines_speeds_"+nSites+"_sites_"+nRes+"_machines_by_site_1.txt";
		String spotsimTrace = String.format("spot-trace-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_%s.txt", scheduler, nRes, nSites, spotLimit,
				rodada);

		String uSpotFile = String.format("spot-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);

		String spotsimPattern = "spotsim.jar -spot -l %s -bid max -w %s -av %s -o %s -u %s -pd %s -md %s";
		String spotsimCMD = String.format(spotsimPattern, spotLimit, spotWorkload, spt, spotsimTrace, uSpotFile, isd, md);
		
//		args = "-spot -l 100 -bid max -w /home/edigley/local/traces/oursim/13_11_2010/oursim-trace-replication_25_machines_7_dias_25_sites_1.txt_spot_workload_sorted.txt -pd ../OurSim/resources/iosup_site_description_25_sites.txt -md ../OurSim/resources/machines_speeds_25_sites_25_machines_by_site_1.txt -av  resources/eu-west-1.linux.m1.small.csv  -o spot-trace-replication_25_machines_7_dias_25_sites_100_spotLimit_1.txt -u spot-trace-utilization-replication_25_machines_7_dias_25_sites_1.txt".split("\\s+");
		args = spotsimCMD.split("\\s+");
		SpotCLI.main(args);

	}

}
