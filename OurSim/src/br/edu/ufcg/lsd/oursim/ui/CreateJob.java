package br.edu.ufcg.lsd.oursim.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import br.edu.ufcg.lsd.oursim.util.ArrayBuilder;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class CreateJob {

	public static void main(String[] args) throws IOException {

		String setUp = "job : \n\tlabel : oursim \n\n";
		String tearDownSep = "";
		String cmd = "";
		cmd += setUp;

		String inputDir = "resources/";

		String workloadType = "marcus";
		String workloadPattern = "%s_workload_7_dias_%s_sites_%s.txt";
		long avDur = TimeUtil.ONE_WEEK + 10 * TimeUtil.ONE_HOUR;
		int spotLimit = 100;

		String scheduler;
		scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "3" : "";

		String java = " $JAVACALL ";
		String jvmArgs = "";
		String jcall = String.format("JAVACALL='java %s -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar'; \\\n", jvmArgs);
		String spotPriceOscilationFile = "eu-west-1.linux.m1.small.csv";
		String sptD = "SPT=" + inputDir + spotPriceOscilationFile + "; \\\n";

		List<String> inputs = new ArrayList<String>();
		inputs.add(spotPriceOscilationFile);

		int[] nSitesV = ArrayBuilder.createVector(50);
		int[] nResV = ArrayBuilder.createVector(50, 50, 25);
		int[] rodadas = ArrayBuilder.createVector(2, 2, 1);

		ArrayBuilder.print(nSitesV);
		ArrayBuilder.print(nResV);
		ArrayBuilder.print(rodadas);

		for (int rodada : rodadas) {
			for (int nSites : nSitesV) {
				for (int nRes : nResV) {
					// int nRes = 25;
					String isdFilePath = String.format("iosup_site_description_%s_sites.txt", nSites);
					String mdFilePath = String.format("machines_speeds_%s_sites_%s_machines_by_site_%s.txt", nSites, nRes, rodada);
					String spt = " $SPT ";
					String isd = " $ISD ";
					String md = " $MD ";
					String sep = "";

					String wFile = String.format(workloadPattern, workloadType, nSites, rodada);
					inputs.add(wFile);

					String oursimTrace = String.format("oursim-trace-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);

					String uFile = String.format("oursim-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);

					String oursimPattern = "oursim.jar -w %s -wt %s -s %s %s -pd %s -nr %s -synthetic_av %s -o %s -u %s -md %s";
					String oursimCMD = String.format(sep + java + oursimPattern, inputDir + wFile, workloadType, scheduler, nReplicas, isd, nRes, avDur,
							oursimTrace, uFile, md);
					sep = " && ";

					String preSpotWorkload = oursimTrace + "_spot_workload.txt";
					String spotWorkload = oursimTrace + "_spot_workload_sorted.txt";
					String spotsimPrePattern = "sort -g %s > %s ";
					String spotsimPreCMD = String.format(sep + spotsimPrePattern, preSpotWorkload, spotWorkload);

					String spotsimTrace = String.format("spot-trace-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_%s.txt", scheduler, nRes, nSites, spotLimit,
							rodada);

					String uSpotFile = String.format("spot-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);

					String spotsimPattern = "spotsim.jar -spot -l %s -bid max -w %s -av %s -o %s -u %s -pd %s -md %s";
					String spotsimCMD = String.format(sep + java + spotsimPattern, spotLimit, spotWorkload, spt, spotsimTrace, uSpotFile, isd, md);

					cmd += "\ttask : \n";
					cmd += "\t\tinit : put /local/edigley/workspace/OurSim/dist/oursim.zip oursim.zip \n";
					cmd += "\t\tremote : ";
					cmd += String.format(tearDownSep + "unzip -o oursim.zip; JAVACALL='java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar';ISD=%s; MD=%s; %s%s%s\n", inputDir + isdFilePath, inputDir + mdFilePath, oursimCMD, "","",
							spotsimCMD);

					inputs.add(isdFilePath);
					inputs.add(mdFilePath);

					cmd += "\t\tfinal : ";
					String getPattern = "\t\t\tget %1$s /local/edigley/traces/oursim/01_12_2010/%1$s \n";
					cmd += String.format(getPattern, oursimTrace);
					cmd += String.format(getPattern, spotWorkload);
//					cmd += String.format(getPattern, spotsimTrace);
					cmd += String.format(getPattern, uFile);
//					cmd += String.format(getPattern, uSpotFile);
					cmd += "\n";

				}
			}
		}

		FileUtils.writeStringToFile(new File("oursim.jdf"), cmd);

		for (String inputFile : inputs) {
			FileUtils.copyFileToDirectory(new File("resources-/" + inputFile), new File(inputDir));
		}

	}

}
