package br.edu.ufcg.lsd.oursim.ui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import br.edu.ufcg.lsd.oursim.util.ArrayBuilder;

public class CreateJob {

	public static void main(String[] args) throws IOException {
		// scp cororoca:~/workspace/OurSim/cmd.txt .
		String setUp = "cd /tmp && mkdir -p playpen/oursim && scp cororoca:~/workspace/OurSim/dist/oursim.zip . && unzip -o oursim.zip -d playpen/oursim && scp cororoca:~/workspace/SpotInstancesSimulator/dist/spotsim.zip . && unzip -o spotsim.zip -d playpen/oursim && cd playpen/oursim;";
		String tearDown = "";
		String tearDownSep = "";
		String cmd = "";
		cmd += setUp;
		String scheduler = "";
		scheduler = "persistent";
		scheduler = "replication 3";
		String resultDir = String.format("/local/edigley/traces/oursim/trace_media_15s_%s_heterogeneous_resources",
				scheduler.startsWith("replication") ? "replication" : "persistent");
		String java = " $JAVACALL ";
		cmd += "JAVACALL='java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar'";
		cmd += ";SPT=resources/eu-west-1.linux.m1.small.csv";
		int[] nSitesV = ArrayBuilder.createVector(2,10);
		int[] nResV = ArrayBuilder.createVector(10, 50,20);
		ArrayBuilder.print(nSitesV);
		ArrayBuilder.print(nResV);
		for (int nSites : nSitesV) {
			int spotLimit = 100;
			for (int nRes : nResV) {
				// int nRes = 25;
				String isdFilePath = String.format("resources/iosup_site_description_%s_sites.txt", nSites);
				String mdFilePath = String.format("resources/machines_speeds_%s_sites_%s_machines_by_site.txt", nSites, nRes);
				String spt = " $SPT ";
				String isd = " $ISD ";
				String md = " $MD ";
				String sep = "";
				String oursimTracePattern = "oursim-trace-%2$s_7_dias_%1$s_sites.txt";
				String oursimPattern = "oursim.jar -w resources/iosup_workload_7_dias_%1$s_sites.txt -s %5$s -pd %3$s -wt iosup -nr %2$s -synthetic_av 2678400 -o "
						+ oursimTracePattern + " -u oursim-trace-utilization-%2$s_7_dias_%1$s_sites.txt -md %4$s";
				String oursimCMD = String.format(sep + java + oursimPattern, nSites, nRes, isd, md, scheduler);
				sep = " && ";

				String spotsimPrePattern = "sort -g " + oursimTracePattern + "_spot_workload.txt > " + oursimTracePattern + "_spot_workload_sorted.txt ";
				String spotsimPreCMD = String.format(sep + spotsimPrePattern, nSites, nRes);
				String spotsimTracePattern = "spot-trace-%2$s_7_dias_%1$s_sites_%3$s_spotLimit.txt";
				String spotsimPattern = "spotsim.jar -spot -l %3$s -bid max -w " + oursimTracePattern + "_spot_workload_sorted.txt -av %4$s -o "
						+ spotsimTracePattern;
				String spotsimCMD = String.format(sep + java + spotsimPattern, nSites, nRes, spotLimit, spt);
				cmd += String.format(";ISD=%s;MD=%s;%s%s%s", isdFilePath, mdFilePath, oursimCMD, spotsimPreCMD, spotsimCMD);

				StringBuilder sb = new StringBuilder("#site	num_cpus\n");
				for (int i = 1; i <= nSites; i++) {
					sb.append(i).append(" ").append(nRes).append("\n");
				}
				FileUtils.writeStringToFile(new File(isdFilePath), sb.toString());

				String tearDownPattern = "scp " + oursimTracePattern + " " + oursimTracePattern + "_spot_workload_sorted.txt " + spotsimTracePattern
						+ " oursim-trace-utilization-%2$s_7_dias_%1$s_sites.txt cororoca:%4$s ";
				tearDown += tearDownSep + String.format(tearDownPattern, nSites, nRes, spotLimit, resultDir);
				tearDownSep = " && ";
			}
			// scp cororoca:~/workspace/OurSim/cmd.txt .
		}

		cmd += " && " + tearDown;

		FileUtils.writeStringToFile(new File("cmd.txt"), cmd);

		// System.out.println(cmd);
	}
	
}
