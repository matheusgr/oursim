package br.edu.ufcg.lsd.oursim.ui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import br.edu.ufcg.lsd.oursim.util.ArrayBuilder;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class CreateCMD {

	public static void main(String[] args) throws IOException {
		// cd /tmp;scp cororoca:~/workspace/OurSim/cmd.txt .; time sh cmd.txt
		String setUp = "cd /tmp && \\\n mkdir -p playpen/oursim && \\\n scp cororoca:~/workspace/OurSim/dist/oursim.zip . && \\\n unzip -o oursim.zip -d playpen/oursim && \\\n scp cororoca:~/workspace/SpotInstancesSimulator/dist/spotsim.zip . && \\\n unzip -o spotsim.zip -d playpen/oursim && cd playpen/oursim; \\\n";
		String tearDownSep = "";
		String cmd = "";
		cmd += setUp;

		String workloadType = "marcus";
		String workloadPattern = "resources/%s_workload_7_dias_%s_sites_%s.txt";
		String resultDir = "/local/edigley/traces/oursim/20_11_2010";
		long avDur = TimeUtil.ONE_WEEK + 10 * TimeUtil.ONE_HOUR;
		int spotLimit = 100;

		String scheduler;
		scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "3" : "";

		String java = " $JAVACALL ";
		String jvmArgs = "";
		cmd += String.format("JAVACALL='java %s -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar'; \\\n", jvmArgs);
		cmd += "SPT=resources/eu-west-1.linux.m1.small.csv; \\\n";

		// int[] nSitesV = ArrayBuilder.createVector(1000);
		// int[] nResV = ArrayBuilder.createVector(25, 50, 25);
		// int[] rodadas = ArrayBuilder.createVector(1, 1, 1);

		int[] nSitesV = new int[] { 50, 25, 10 };
		int[] nResV = new int[] { 50, 35, 25, 10 };
		int[] rodadas = new int[] { 6, 7, 8, 9, 10 };

		System.out.println("scheduler: " + scheduler);
		System.out.print("nSitesV  : ");
		ArrayBuilder.print(nSitesV);
		System.out.print("nResV    : ");
		ArrayBuilder.print(nResV);
		System.out.print("rodadas  : ");
		ArrayBuilder.print(rodadas);

		for (int rodada : rodadas) {
			for (int nSites : nSitesV) {
				for (int nRes : nResV) {// tá variando primeiro. Talvez fosse
					// melhor se nSites variasse primeiro
					// int nRes = 25;
					String isdFilePath = String.format("resources/iosup_site_description_%s_sites.txt", nSites);
					String mdFilePath = String.format("resources/machines_speeds_%s_sites_%s_machines_by_site_%s.txt", nSites, nRes, rodada);
					String spt = " $SPT ";
					String isd = " $ISD ";
					String md = " $MD ";
					String sep = "";

					String wFile = String.format(workloadPattern, workloadType, nSites, rodada);

					String oursimTrace = String.format("oursim-trace-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);

					String uFile = String.format("oursim-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);

					String oursimPattern = "oursim.jar -w %s -wt %s -s %s %s -pd %s -nr %s -synthetic_av %s -o %s -u %s -md %s";
					String oursimCMD = String.format(sep + java + oursimPattern, wFile, workloadType, scheduler, nReplicas, isd, nRes, avDur, oursimTrace,
							uFile, md);
					sep = " && \\\n ";

					String preSpotWorkload = oursimTrace + "_spot_workload.txt";
					String spotWorkload = oursimTrace + "_spot_workload_sorted.txt";
					String spotsimPrePattern = "sort -g %s > %s ";
					String spotsimPreCMD = String.format(sep + spotsimPrePattern, preSpotWorkload, spotWorkload);

					String spotsimTrace = String.format("spot-trace-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_%s.txt", scheduler, nRes, nSites, spotLimit,
							rodada);

					String uSpotFile = String.format("spot-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);

					String spotsimPattern = "spotsim.jar -spot -l %s -bid max -w %s -av %s -o %s -u %s -pd %s -md %s";
					String spotsimCMD = String.format(sep + java + spotsimPattern, spotLimit, spotWorkload, spt, spotsimTrace, uSpotFile, isd, md);
					cmd += String.format(tearDownSep + "ISD=%s; \\\n MD=%s; \\\n %s%s%s", isdFilePath, mdFilePath, oursimCMD, spotsimPreCMD, spotsimCMD);

					StringBuilder sb = new StringBuilder("#site	num_cpus\n");
					for (int i = 1; i <= nSites; i++) {
						sb.append(i).append(" ").append(nRes).append("\n");
					}
					FileUtils.writeStringToFile(new File(isdFilePath), sb.toString());

					String tearDownPattern = " scp %s \\\n     %s \\\n     %s \\\n     %s \\\n     %s \\\n     cororoca:%s ";

					tearDownSep = " && \\\n";
					cmd += tearDownSep + String.format(tearDownPattern, oursimTrace, spotWorkload, spotsimTrace, uFile, uSpotFile, resultDir);

				}
			}
		}

		FileUtils.writeStringToFile(new File("cmd.txt"), cmd);

		// System.out.println(cmd);
	}
}
