package br.edu.ufcg.lsd.oursim.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import br.edu.ufcg.lsd.oursim.util.ArrayBuilder;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class CopyOfCreateCMDWraperTask2 {

	public static void main(String[] args) throws IOException {
		// cd /tmp;scp cororoca:~/workspace/OurSim/cmd.txt .; time sh cmd.txt
		String setUp = "cd /tmp && \\\n mkdir -p playpen/oursim && \\\n scp cororoca:~/workspace/OurSim/dist/oursim.zip . && \\\n unzip -o oursim.zip -d playpen/oursim && \\\n scp cororoca:~/workspace/SpotInstancesSimulator/dist/spotsim.zip . && \\\n unzip -o spotsim.zip -d playpen/oursim && cd playpen/oursim \\\n";
		String cmd = "";
		String sep = "";
		// cmd += setUp;

		String workloadType = "marcus";
		String workloadPattern = "%s_workload_7_dias_%s_sites_%s.txt";
		String resultDir = "/local/edigley/traces/oursim/06_12_2010";
		long avDur = TimeUtil.ONE_WEEK + 10 * TimeUtil.ONE_HOUR;
		int spotLimit = 100; // Integer.MAX_VALUE;

		String scheduler;
		scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "3" : "";

		String java = " $JAVACALL ";
		String jvmArgs = "";
		cmd += String.format("JAVACALL='java %s -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar' && \\\n", jvmArgs);

		final String NL = " \\\n ";
		final String NC = " && \\\n";

		boolean groupedbypeer = true;

		boolean utilization = false;

		String[] spts = new String[] {

		"us-east-1.linux.m1.small.csv",

		// "us-east-1.linux.m1.large.csv",
		//
		// "us-east-1.linux.m1.xlarge.csv",
		//
		// "us-east-1.linux.m2.xlarge.csv",
		//
		// "us-east-1.linux.m2.2xlarge.csv",
		//
		// "us-east-1.linux.m2.4xlarge.csv",
		//
		// "us-east-1.linux.c1.medium.csv",
		//
		// "us-east-1.linux.c1.xlarge.csv",

		};

		// int[] nSitesV = ArrayBuilder.createVector(40, 50, 5);
		// int[] nResV = ArrayBuilder.createVector(25, 50, 25);
		// int[] rodadas = ArrayBuilder.createVector(1);

		int[] nSitesV = new int[] { 50 };
		int[] nResV = new int[] { 50};

		int[] rodadas = ArrayBuilder.createVector(1);

		System.out.println("scheduler: " + scheduler);
		System.out.print("nSitesV  : ");
		ArrayBuilder.print(nSitesV);
		System.out.print("nResV    : ");
		ArrayBuilder.print(nResV);
		System.out.print("rodadas  : ");
		ArrayBuilder.print(rodadas);

		String inputDir = "input-files/";
		List<String> inputs = new ArrayList<String>();
		List<String> outputs = new ArrayList<String>();

		// tá variando primeiro. Talvez fosse melhor se nSites variasse primeiro
		for (int rodada : rodadas) {
			for (String sptFilePath : spts) {
				for (int nSites : nSitesV) {
					for (int nRes : nResV) {
						String isdFilePath = String.format("iosup_site_description_%s_sites.txt", nSites);
						String mdFilePath = String.format("machines_speeds_%s_sites_%s_machines_by_site_%s.txt", nSites, nRes, rodada);
						String spt = " $SPT ";
						String isd = " $ISD ";
						String md = " $MD ";

						WraperTask oursimTask = new WraperTask();

						String wFile = String.format(workloadPattern, workloadType, nSites, rodada);
						oursimTask.inputs.add(wFile);
						oursimTask.inputs.add(isdFilePath);
						oursimTask.inputs.add(mdFilePath);

						String oursimTrace = String.format("oursim-trace-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);
						oursimTask.outputs.add(oursimTrace);
						outputs.add(oursimTrace);
						String uFile = String.format("oursim-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);
						if (utilization) {
							oursimTask.outputs.add(uFile);
							outputs.add(uFile);
						}
						String oursimPattern = java + "oursim.jar -w %s -wt %s -s %s %s -pd %s -nr %s -synthetic_av %s -o %s %s -md %s";
						oursimTask.cmd = String.format(oursimPattern, inputDir + wFile, workloadType, scheduler, nReplicas, isd, nRes, avDur, oursimTrace,
								utilization ? " -u " + uFile : "", md);

						WraperTask prespotsimTask = new WraperTask();
						String preSpotWorkload = oursimTrace + "_spot_workload.txt";
						oursimTask.outputs.add(preSpotWorkload);
						prespotsimTask.inputs.add(preSpotWorkload);

						String spotWorkload = oursimTrace + "_spot_workload_sorted.txt";
						prespotsimTask.outputs.add(spotWorkload);
						outputs.add(spotWorkload);
						String spotsimPrePattern = "sort -g %s > %s ";
						prespotsimTask.cmd = String.format(spotsimPrePattern, preSpotWorkload, spotWorkload);

						WraperTask spotsimTask = new WraperTask();
						spotsimTask.inputs.add(spotWorkload);
						spotsimTask.inputs.add(sptFilePath);
						spotsimTask.inputs.add(isdFilePath);
						spotsimTask.inputs.add(mdFilePath);

						String spotsimTrace = String.format("spot-trace-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_%s_av_%s_%s.txt", scheduler,
								nRes, nSites, spotLimit, groupedbypeer, sptFilePath, rodada);
						spotsimTask.outputs.add(spotsimTrace);
						outputs.add(spotsimTrace);
						String uSpotFile = String.format("spot-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_%s_av_%s_%s.txt",
								scheduler, nRes, nSites, spotLimit, groupedbypeer, sptFilePath, rodada);
						if (utilization) {
							spotsimTask.outputs.add(uSpotFile);
							outputs.add(uSpotFile);
						}
						String spotsimPattern = java + "spotsim.jar -spot %s -l %s -bid max -w %s -av %s -o %s %s -pd %s -md %s";
						String gdp = groupedbypeer ? "-gbp" : "";

						spotsimTask.cmd = String.format(spotsimPattern, gdp, spotLimit, spotWorkload, spt, spotsimTrace, utilization ? " -u " + uSpotFile : "",
								isd, md);

						inputs.add(wFile);
						inputs.add(sptFilePath);
						inputs.add(isdFilePath);
						inputs.add(mdFilePath);

						StringBuilder sb = new StringBuilder("#site	num_cpus\n");
						for (int i = 1; i <= nSites; i++) {
							sb.append(i).append(" ").append(nRes).append("\n");
						}
						FileUtils.writeStringToFile(new File("/local/edigley/traces/oursim/sites_description/" + isdFilePath), sb.toString());

						String tearDown = "scp";
						String tearDownSep = " ";
						for (String output : outputs) {
							tearDown += tearDownSep + output;
							tearDownSep = NL;
						}
						tearDown += " cororoca:" + resultDir;

						outputs.clear();

						String shellVarDef = String.format("SPT=%s && \\\n ISD=%s && \\\n MD=%s && \\\n", inputDir + sptFilePath, inputDir + isdFilePath,
								inputDir + mdFilePath);
						cmd += sep + shellVarDef + oursimTask + NC + prespotsimTask + NC + spotsimTask + NC + tearDown;
						sep = NC;

					}
				}
			}
		}

		FileUtils.deleteQuietly(new File(inputDir));

		(new File(inputDir)).mkdir();

		FileUtils.copyFileToDirectory(new File("/home/edigley/local/resources_BKP/exemplo-de-execucao.txt"), new File(inputDir));

		for (String inputFile : inputs) {
			String sourceDir = "";
			if (inputFile.startsWith(workloadType + "_workload")) {
				sourceDir = "/local/edigley/traces/oursim/workloads/";
			} else if (inputFile.startsWith("machines_speeds_") || inputFile.startsWith("iosup_site_description_")) {
				sourceDir = "/local/edigley/traces/oursim/sites_description/";
			} else if (inputFile.endsWith(".csv")) {
				sourceDir = "/local/edigley/traces/spot_instances/spot-instances-prices-23-11-2010/";
			}
			String inputFilePath = sourceDir + inputFile;
			FileUtils.copyFileToDirectory(new File(inputFilePath), new File(inputDir));
		}

		FileUtils.writeStringToFile(new File("cmd.txt"), setUp + NC + cmd);

	}
}
