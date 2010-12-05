package br.edu.ufcg.lsd.oursim.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import br.edu.ufcg.lsd.oursim.util.ArrayBuilder;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class CreateCMD2 {

	public static void main(String[] args) throws IOException {
		// cd /tmp;scp cororoca:~/workspace/OurSim/cmd.txt .; time sh cmd.txt
		String setUp = "cd /tmp && \\\n mkdir -p playpen/oursim && \\\n scp cororoca:~/workspace/OurSim/dist/oursim.zip . && \\\n unzip -o oursim.zip -d playpen/oursim && \\\n scp cororoca:~/workspace/SpotInstancesSimulator/dist/spotsim.zip . && \\\n unzip -o spotsim.zip -d playpen/oursim && cd playpen/oursim \\\n";
		String tearDownSep = "";
		String cmd = "";
		// cmd += setUp;

		String workloadType = "marcus";
		String workloadPattern = "%s_workload_7_dias_%s_sites_%s.txt";
		String resultDir = "/local/edigley/traces/oursim/02_12_2010";
		long avDur = TimeUtil.ONE_WEEK + 10 * TimeUtil.ONE_HOUR;
		int spotLimit = 100;
		spotLimit = Integer.MAX_VALUE;

		String scheduler;
		scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "3" : "";

		String java = " $JAVACALL ";
		String jvmArgs = "";
		cmd += String.format("JAVACALL='java %s -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar'; \\\n", jvmArgs);
		// cmd += "SPT=resources/eu-west-1.linux.m1.small.csv; \\\n";

		boolean groupedbypeer = true;

		String[] spts = new String[] {

		"us-east-1.linux.m1.small.csv",

		"us-east-1.linux.m1.large.csv",

		"us-east-1.linux.m1.xlarge.csv",

		"us-east-1.linux.m2.xlarge.csv",

		"us-east-1.linux.m2.2xlarge.csv",

		"us-east-1.linux.m2.4xlarge.csv",

		"us-east-1.linux.c1.medium.csv",

		"us-east-1.linux.c1.xlarge.csv", };

		int[] nSitesV = ArrayBuilder.createVector(5, 50, 5);
		int[] nResV = ArrayBuilder.createVector(5, 50, 5);
		int[] rodadas = ArrayBuilder.createVector(1);

		System.out.println("scheduler: " + scheduler);
		System.out.print("nSitesV  : ");
		ArrayBuilder.print(nSitesV);
		System.out.print("nResV    : ");
		ArrayBuilder.print(nResV);
		System.out.print("rodadas  : ");
		ArrayBuilder.print(rodadas);

		boolean oursim = false;
		boolean spotsim = true;
		String inputDir = "resources/";
		List<String> spotWorkloads = new ArrayList<String>();
		List<String> inputs = new ArrayList<String>();
		List<String> outputs = new ArrayList<String>();

		for (int rodada : rodadas) {
			for (String sptFilePath : spts) {
				for (int nSites : nSitesV) {
					for (int nRes : nResV) {// tá variando primeiro. Talvez
						// fosse
						// melhor se nSites variasse primeiro
						// int nRes = 25;
						String isdFilePath = String.format("iosup_site_description_%s_sites.txt", nSites);
						String mdFilePath = String.format("machines_speeds_%s_sites_%s_machines_by_site_%s.txt", nSites, nRes, rodada);
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
						oursimCMD = oursim ? oursimCMD : "";
						sep = " && \\\n ";

						String preSpotWorkload = oursimTrace + "_spot_workload.txt";
						String spotWorkload = oursimTrace + "_spot_workload_sorted.txt";
						String spotsimPrePattern = "sort -g %s > %s ";
						String spotsimPreCMD = String.format(sep + spotsimPrePattern, preSpotWorkload, spotWorkload);
						spotsimPreCMD = oursim ? spotsimPreCMD : "";

						String spotsimTrace = String.format("spot-trace-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_%s_av_%s_%s.txt", scheduler,
								nRes, nSites, spotLimit, groupedbypeer, sptFilePath, rodada);

						String uSpotFile = String.format("spot-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_%s_av_%s_%s.txt",
								scheduler, nRes, nSites, spotLimit, groupedbypeer, sptFilePath, rodada);

						String spotsimPattern = "spotsim.jar -spot %s -l %s -bid max -w %s -av %s -o %s -u %s -pd %s -md %s";
						String gdp = groupedbypeer ? "-gbp" : "";
						String spotsimCMD = String.format(sep + java + spotsimPattern, gdp, spotLimit, inputDir + spotWorkload, spt, spotsimTrace, uSpotFile,
								isd, md);
						spotsimCMD = spotsim ? spotsimCMD : "";

						cmd += String.format(tearDownSep + "SPT=%s; \\\n ISD=%s; \\\n MD=%s \\\n %s%s%s", inputDir + sptFilePath, inputDir + isdFilePath,
								inputDir + mdFilePath, oursimCMD, spotsimPreCMD, spotsimCMD);

						// inputs.add(wFile);
						inputs.add(spotWorkload);
						inputs.add(isdFilePath);
						inputs.add(mdFilePath);
						inputs.add(sptFilePath);

						StringBuilder sb = new StringBuilder("#site	num_cpus\n");
						for (int i = 1; i <= nSites; i++) {
							sb.append(i).append(" ").append(nRes).append("\n");
						}
						FileUtils.writeStringToFile(new File(isdFilePath), sb.toString());

						String tearDownPattern = " scp %s \\\n     %s \\\n     %s \\\n     %s \\\n     %s \\\n     cororoca:%s ";

						tearDownSep = " && \\\n";
						// oursimTrace = oursim ? oursimTrace : "";
						uFile = oursim ? uFile : "";
						spotWorkload = spotsim ? spotWorkload : "";
						uSpotFile = spotsim ? uSpotFile : "";
						spotWorkloads.add(spotWorkload);
						cmd += tearDownSep + String.format(tearDownPattern, "", inputDir + spotWorkload, spotsimTrace, uFile, uSpotFile, resultDir);

					}
				}
			}
		}

		FileUtils.deleteQuietly(new File(inputDir));

		(new File(inputDir)).mkdir();

		String preCP = "";

		FileUtils.copyFileToDirectory(new File("resources-/exemplo-de-execucao.txt"), new File(inputDir));

		// if (!oursim) {
		// String sep = "";
		// for (String spotWorkload : spotWorkloads) {
		// preCP += sep + " scp
		// cororoca:/local/edigley/traces/oursim/20_11_2010/" + spotWorkload + "
		// . ";
		// sep = " && ";
		// preCP += sep + " scp
		// cororoca:/local/edigley/traces/oursim/20_11_2010/" +
		// spotWorkload.replace("_spot_workload_sorted.txt", "") + " . ";
		// }
		// }

		for (String inputFile : inputs) {
			String sourceDir = spotWorkloads.contains(inputFile) ? "/local/edigley/traces/oursim/20_11_2010/" : "resources-/";
			FileUtils.copyFileToDirectory(new File(sourceDir + inputFile), new File(inputDir));
		}

		FileUtils.writeStringToFile(new File("cmd.txt"), setUp + preCP + " && \\\n" + cmd);
		// System.out.println(cmd);
	}
}
