package br.edu.ufcg.lsd.oursim.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import br.edu.ufcg.lsd.oursim.util.AB;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class CreateCMD {

	static final String NL = " \\\n ";

	static final String SNCP = " && ( " + NL;

	static final String FNCP = " ) && " + NL;

	static final String NCP = " ) && ( " + NL;

	static final String NC = " && " + NL;

	static final String NC_PAR = " & " + NL;

	static long[] peerRankingSeeds = new long[] { -1,

	9354269,

	3296549,

	1113286,

	78904,

	323543,

	2391341,

	87,

	1001,

	865,

	555438,

	73465,

	367143,

	15495,

	99999,

	8348,

	672436,

	1,

	3640,

	15396,

	983,

	78,

	4353,

	7434,

	3245,

	2435345,

	86765,

	3333,

	65346,

	7654,

	3253245,

	};

	static String[] avCharacterizationSeeds = new String[] { " placeholder",

	" 1234 13455 5566 6548 8764 5674 ",

	" 786423 6768 387341 21321 1 98 ",

	" 3 978 222 23 641325 76 ",

	" 143 33 98533 7842 9 532172 ",

	" 6324 84579 378314 248753 3214 976 ",

	" 956 324 32346 7548 3297 6695 ",

	" 3 8924 745 2131 77 435 ",

	" 33156 78686 190275 6133 658 47 ",

	" 349 1111 89435 23 8421 952332 ",

	" 98324 324 23412 8945932 31654 24946",

	" 992734 7885 44325  43589 346589 243612 ",

	" 546  4161 787   64164   61441  64651",

	" 46316 46466 43456 64 3456 65434 ",

	" 5633 465 46 7982 44782 97896  ",

	" 58 6597 379 91738 1353 74673 ",

	" 3461 947374 1616 87364 367131 898923 ",

	" 47 38 14378 637 835 217 ",

	" 643 46 56 434 768 367 ",

	" 48 63 7940 906 36 8896 ",

	" 3647 86 56 37 485 3647 ",

	" 3364 3453 554 46335 45433 4523 ",

	" 036 64967 8 3245 92 653 ",

	" 932 2 3045 9 375 234 ",

	" 8943 78 543 6582 3 54 ",

	" 52 57 89 545 436 543 ",

	" 2 938 7 456 54 76  ",

	" 34 4645 435234 664 767 1 ",

	" 9089 0890 7054 9643 8284 95966",

	" 547 89 3 675 332 884 ",

	" 4595 678365 78346 537 8456 92384   ",

	};

	static String[] allSpts = new String[] {

	"us-east-1.linux.m1.small.csv",

	"us-east-1.linux.m1.large.csv",

	"us-east-1.linux.m1.xlarge.csv",

	"us-east-1.linux.c1.medium.csv",

	"us-east-1.linux.c1.xlarge.csv",

	"us-east-1.linux.m2.xlarge.csv",

	"us-east-1.linux.m2.2xlarge.csv",

	"us-east-1.linux.m2.4xlarge.csv"

	};

	static int[] allSpotLimits = new int[] { 100, Integer.MAX_VALUE, 500, 400, 300, 200 };

	// cd /tmp;scp ourico:~/workspace/OurSim/cmd.txt .; time sh cmd.txt
	public static void main(String[] args) throws IOException, InterruptedException {

		String cmd = "";
		String sep = "";
//		String host = "150.165.85.109";
		String host = "cariri";

		String workloadType = "marcus";
		String workloadPattern = "%s_workload_7_dias_%s_sites_%s.txt";

		String nupp = "10upp";
		
		String resultDir =                  "/local/edigley/mestrado/traces/24_07_2013_"+nupp+"/";
		String jobResultDir = resultDir;
		String workloadsDir =               "/local/edigley/mestrado/workloads/"+nupp+"/";
		String peerCapacityDescriptionDir = "/local/edigley/mestrado/grid_capacity/";
		String spotInstancesPricesDir =     "/local/edigley/mestrado/spot_instances_prices/23_11_2010/";
		String workspaceDir = "/local/edigley/workspaces/simulacao/";
		String execDir = "/tmp";

		String scheduler;
		//scheduler = "replication";
		scheduler = "persistent";
		String nReplicas = scheduler.equals("replication") ? "2" : "";

		String java = " $JAVACALL ";
		String jvmArgs = "";
		cmd += String.format("JAVACALL='java %s -Xms512M -Xmx2048M -XX:-UseGCOverheadLimit -jar' " + NC, jvmArgs);

		boolean groupedbypeer = false;

		boolean utilization = false;

		boolean runOurSim = AB.toString(args).contains("oursim") ? true : false;

		boolean runSpotSim = AB.toString(args).contains("spotsim") ? true : false;

		int parLevel = 4;

		// adicionar a opção nof como uma booleana

		String[] spts = runSpotSim ? allSpts : new String[] {};
		// String[] spts = runSpotSim ? new String[] { allSpts[3] } : new
		// String[] {};
		// String[] spts = runSpotSim ? new String[] { allSpts[5] } : new
		// String[] {};
		// String[] spts = runSpotSim ? AB.exclude(allSpts, 3) : new String[]
		// {};
		// String[] spts = runSpotSim ? new String[] { allSpts[0] } : new
		// String[] {};

		if (AB.toString(args).contains("spotsim")) {
			int pos = AB.getPositionIn("spotsim", args);
			if (args.length > pos + 1) {
				// spts = args.length > pos + 1 ? new String[] {
				// "us-east-1.linux." + args[pos + 1] + ".csv" } : spts;
				String[] nspts = new String[args.length - (pos + 1)];
				for (int i = 0; i < nspts.length; i++) {
					nspts[i] = "us-east-1.linux." + args[pos + 1 + i] + ".csv";
				}
				spts = nspts;
			}
		}

		int[] spotLimits = runSpotSim ? new int[] { allSpotLimits[0] } : new int[] {};

		//int[] nSitesV = AB.cv(Integer.parseInt(args[0]));
		int[] nSitesV = AB.cv(args[0]);

		int[] nResV = AB.cv(30);

		// ultimo foi em tubarao sites 40 rodada 14

		int[] rodadas = AB.cv(args[1]);

		String inputDir = "input-files/";
		List<String> inputs = new ArrayList<String>();
		List<String> outputs = new ArrayList<String>();

		List<WraperTask> tasks = new ArrayList<WraperTask>();

		String rodadaID = generateRodadaID(nSitesV, nResV, scheduler, spts, spotLimits, groupedbypeer, rodadas, runOurSim, runSpotSim);

		System.out.println(rodadaID.replaceAll("_", "\n"));

		// tá variando primeiro. Talvez fosse melhor se nSites variasse primeiro
		for (int rodada : rodadas) {
			// for (String sptFilePath : spts) {
			for (int nSites : nSitesV) {
				for (int nRes : nResV) {
					String aitFilePath = "ec2_instances.txt";
					String isdFilePath = String.format("iosup_site_description_%s_sites.txt", nSites);
					String mdFilePath = String.format("machines_speeds_%s_sites_%s_machines_by_site_%s.txt", nSites, nRes, rodada);
					String spt = " $SPT ";
					String isd = " $ISD ";
					String md = " $MD ";
					String ait = " $AIT ";

					WraperTask oursimTask = new WraperTask();

					String wFile = String.format(workloadPattern, workloadType, nSites, rodada);
					oursimTask.inputs.add(wFile);
					oursimTask.inputs.add(isdFilePath);
					oursimTask.inputs.add(mdFilePath);
					oursimTask.labels.put(isd.trim(), inputDir + isdFilePath);
					oursimTask.labels.put(md.trim(), inputDir + mdFilePath);
					oursimTask.labels.put(java.trim(), "unzip oursim.zip ; java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar ");

					String oursimTrace = String.format("oursim-trace-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);
					oursimTask.outputs.add(oursimTrace);
					if (runOurSim) {
						outputs.add(oursimTrace);
					}
					String uFile = String.format("oursim-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s.txt", scheduler, nRes, nSites, rodada);
					if (utilization) {
						oursimTask.outputs.add(uFile);
						outputs.add(uFile);
					}
					String oursimPattern = java + "oursim.jar -w %s -wt %s -s %s %s -pd %s -nr %s -synthetic_av ourgrid -o %s %s -md %s -prs %s -acs %s";// XXX
					// -erw
					// %s";
					String preSpotWorkload = oursimTrace + "_spot_workload.txt";
					preSpotWorkload = "";// XXX
					oursimTask.cmd = String.format(oursimPattern, inputDir + wFile, workloadType, scheduler, nReplicas, isd, nRes, oursimTrace,
							utilization ? " -u " + uFile : "", md, peerRankingSeeds[rodada], avCharacterizationSeeds[rodada]);

					WraperTask prespotsimTask = new WraperTask();

					oursimTask.outputs.add(preSpotWorkload);
					prespotsimTask.inputs.add(preSpotWorkload);

					String spotWorkload = oursimTrace;// +
					// "_spot_workload_sorted.txt";
					spotWorkload = wFile;// XXX
					// prespotsimTask.outputs.add(spotWorkload);
					// outputs.add(spotWorkload);//XXX
					// String spotsimPrePattern = "sort -g %s > %s ";
					// prespotsimTask.cmd = String.format(spotsimPrePattern,
					// preSpotWorkload, spotWorkload);

					if (runOurSim) {
						tasks.add(oursimTask);
					}
					List<WraperTask> spotTasks = new ArrayList<WraperTask>();

					for (String sptFilePath : spts) {
						for (int spotLimit : spotLimits) {

							WraperTask spotsimTask = new WraperTask();
							spotsimTask.inputs.add(spotWorkload);
							spotsimTask.inputs.add(sptFilePath);
							spotsimTask.inputs.add(isdFilePath);
							spotsimTask.inputs.add(mdFilePath);
							spotsimTask.inputs.add(aitFilePath);

							spotsimTask.labels.put(spt.trim(), inputDir + sptFilePath);
							spotsimTask.labels.put(md.trim(), inputDir + mdFilePath);
							spotsimTask.labels.put(isd.trim(), inputDir + isdFilePath);
							spotsimTask.labels.put(ait.trim(), inputDir + aitFilePath);
							spotsimTask.labels.put(java.trim(), "unzip oursim.zip ; java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar ");

							String spotsimTrace = String.format("spot-trace-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_%s_av_%s_%s.txt",
									scheduler, nRes, nSites, spotLimit, groupedbypeer, sptFilePath, rodada);
							spotsimTask.outputs.add(spotsimTrace);
							outputs.add(spotsimTrace);
							String uSpotFile = String.format(
									"spot-trace-utilization-%s_%s_machines_7_dias_%s_sites_%s_spotLimit_groupedbypeer_%s_av_%s_%s.txt", scheduler, nRes,
									nSites, spotLimit, groupedbypeer, sptFilePath, rodada);
							if (utilization) {
								spotsimTask.outputs.add(uSpotFile);
								outputs.add(uSpotFile);
							}
							String spotsimPattern = java + "spotsim.jar -spot %s -l %s -bid max -w %s -av %s -o %s %s -pd %s -md %s -ait %s";
							String gdp = groupedbypeer ? "-gbp" : "";

							spt = inputDir + sptFilePath;
							spotsimTask.cmd = String.format(spotsimPattern, gdp, spotLimit, inputDir + spotWorkload, spt, spotsimTrace, utilization ? " -u "
									+ uSpotFile : "", isd, md, ait);

							inputs.add(wFile);
							inputs.add(sptFilePath);
							inputs.add(isdFilePath);
							inputs.add(mdFilePath);
							inputs.add(aitFilePath);

							// inputs.add(spotWorkload);//XXX

							spotTasks.add(spotsimTask);
							tasks.add(spotsimTask);
						}

					}

					inputs.add(wFile);// XXX onlyOurSim
					inputs.add(isdFilePath);// XXX onlyOurSim
					inputs.add(mdFilePath);// XXX onlyOurSim

					StringBuilder sb = new StringBuilder("#site	num_cpus\n");
					for (int i = 1; i <= nSites; i++) {
						sb.append(i).append(" ").append(nRes).append("\n");
					}
					FileUtils.writeStringToFile(new File(peerCapacityDescriptionDir + isdFilePath), sb.toString());

					String tearDown = "scp";
					String tearDownSep = " ";
					for (String output : outputs) {
						tearDown += tearDownSep + output;
						tearDownSep = NL;
					}
					tearDown += " "+host+":" + resultDir;
					outputs.clear();

					String shellVarDef = String.format("ISD=%s && \\\n MD=%s && \\\n AIT=%s \\\n", inputDir + isdFilePath, inputDir + mdFilePath, inputDir
							+ aitFilePath);
					cmd += NL + sep + shellVarDef;

					if (runOurSim) {
						cmd += " && " + oursimTask;// XXX + NC +
						// prespotsimTask;
					}

					// int taskIndex = -1;
					for (WraperTask spotsimTask : spotTasks) {
						// taskIndex++;
						// String pref = taskIndex == 0 ? "" : " ) ";
						// String suf = taskIndex == spotTasks.size() - 1 ? " )
						// " : "";
						// cmd += (taskIndex % parLevel == 0 ? pref + SNCP :
						// NC_PAR) + spotsimTask + suf;
						cmd += NC + spotsimTask;
					}
					cmd += NC + tearDown;
					sep = NC;

				}
			}
			// }
		}

		FileUtils.deleteQuietly(new File(inputDir));

		(new File(inputDir)).mkdir();

//		FileUtils.copyFileToDirectory(new File("/home/edigley/local/resources_BKP/exemplo-de-execucao.txt"), new File(inputDir));

		for (String inputFile : inputs) {
			String sourceDir = null;

			if (inputFile.endsWith(".txt_spot_workload_sorted.txt")) {
				sourceDir = "/local/edigley/traces/oursim/03_12_2010/";
			} else if (inputFile.startsWith(workloadType + "_workload")) {
				sourceDir = workloadsDir;
			} else if (inputFile.endsWith("ec2_instances.txt")) {
				sourceDir = "../SpotInstancesSimulator/resources/";
			} else if (inputFile.startsWith("machines_speeds_") || inputFile.startsWith("iosup_site_description_")) {
				sourceDir = peerCapacityDescriptionDir;
			} else if (inputFile.endsWith(".csv")) {
				sourceDir = spotInstancesPricesDir;
			}
			File iFile = new File(sourceDir + inputFile);
			if (iFile.exists()) {
				FileUtils.copyFileToDirectory(iFile, new File(inputDir));
			} else {
				System.out.println("Arquivo não existente: " + iFile.getAbsolutePath());
				System.exit(1);
			}
		}

		String setUp = "";
		setUp += "cd "+execDir+" " + NC;
		// setUp += " rm -rf playpen " + NC;
		setUp += "  mkdir -p playpen/oursim_" + rodadaID + " " + NC;
		setUp += "  scp "+host+":"+workspaceDir+"/OurSim/rodadas/" + rodadaID + "/oursim.zip . " + NC;
		setUp += "  unzip -o oursim.zip -d playpen/oursim_" + rodadaID + " " + NC;
		// setUp += " scp
		// ourico:~/workspace/SpotInstancesSimulator/dist/spotsim.zip . " +
		// NC;
		// setUp += " unzip -o spotsim.zip -d playpen/oursim " + NC;
		setUp += "  cd playpen/oursim_" + rodadaID + " " + NL;

		FileUtils.writeStringToFile(new File("cmd.txt"), setUp + NC + cmd);

		FileUtils.writeStringToFile(new File("rodadas.properties"), "rodada.id=" + rodadaID + "\n");

		FileWriter fw = new FileWriter("rodadas.txt", true);
		fw.write(rodadaID + "\n");
		fw.close();

		StringBuilder jobSB = new StringBuilder();
		jobSB.append("job : \n");
		jobSB.append("\tlabel : ").append(rodadaID).append(" \n");
		jobSB.append("\trequirements : ( os == linux )\n\n");

		for (WraperTask task : tasks) {

			jobSB.append("\ttask : \n");
			// jobSB.append("\t\tinit : store
			// /local/edigley/workspace/OurSim/rodadas/" + rodadaID +
			// "/oursim.zip oursim.zip \n");
			jobSB.append("\t\tinit : put oursim.zip oursim.zip \n");

			jobSB.append("\t\tremote : ");
			for (Entry<String, String> entry : task.labels.entrySet()) {
				task.cmd = task.cmd.replace(entry.getKey(), entry.getValue());
			}

			jobSB.append(task.cmd + "\n");

			jobSB.append("\t\tfinal : ");
			for (String output : task.outputs) {
				if (!output.trim().isEmpty()) {
					// jobSB.append(String.format("\tget %1$s %2$s/%1$s \n",
					// output, jobResultDir));
					jobSB.append(String.format("\tget %1$s %1$s \n", output, jobResultDir));
				}
			}

		}

		FileUtils.writeStringToFile(new File("oursim.jdf"), jobSB.toString());

		StringBuilder xargsSB = new StringBuilder();
		StringBuilder outputSB = new StringBuilder();

		for (WraperTask task : tasks) {

			for (Entry<String, String> entry : task.labels.entrySet()) {
				task.cmd = task.cmd.replace(entry.getKey(), entry.getValue());
			}

			outputSB.append("scp "+task.outputs.get(0)+" "+host+":"+resultDir + "\n");
			
			String onlyTheArgs = task.cmd.replace("unzip oursim.zip ; java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar  oursim.jar", "")
										 .replace("unzip oursim.zip ; java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar  spotsim.jar", "");
			xargsSB.append(onlyTheArgs + "\n");

		}

		FileUtils.writeStringToFile(new File("args.txt"), xargsSB.toString());
		FileUtils.writeStringToFile(new File("outputs.txt"), outputSB.toString());
		
		StringBuilder cmdXargsSB = new StringBuilder();
		if (runOurSim) {
			//cmdXargsSB.append("cat args.txt | xargs -n25 -t -P5 java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar oursim.jar");
			cmdXargsSB.append("cat args.txt | xargs -n25 -t -P2 java -Xms500M -Xmx4000M -XX:-UseGCOverheadLimit -jar oursim.jar");
		}else{
			//cmdXargsSB.append("cat args.txt | xargs -n17 -t -P15 java -Xms250M -Xmx1000M -XX:-UseGCOverheadLimit -jar spotsim.jar");
			cmdXargsSB.append("cat args.txt | xargs -n17 -t -P2 java -Xms500M -Xmx4000M -XX:-UseGCOverheadLimit -jar spotsim.jar");
		}
		FileUtils.writeStringToFile(new File("cmdXargs.txt"), setUp + NC + cmdXargsSB + NC + "time sh outputs.txt");
		
		System.out.println("\n  Finished!!!!");
		
		Thread.sleep(2000);

	}

	private static String generateRodadaID(int[] sitesV, int[] resV, String scheduler, String[] spts, int[] spotLimits, boolean groupedbypeer, int[] rodadas,
			boolean runOurSim, boolean runSpotSim) {

		StringBuilder sb = new StringBuilder();

		String[] sptsSimple = getInstancesSimpleName(spts);

		sb

		.append("npeers=").append(AB.toString(sitesV))

		.append("_nmacbypeer=").append(AB.toString(resV))

		.append("_scheduler=").append(scheduler)

		.append(runSpotSim ? "_spts=" : "").append(runSpotSim ? AB.toString(sptsSimple) : "")

		.append(runSpotSim ? "_limit=" : "").append(runSpotSim ? AB.toString(spotLimits) : "")

		.append(runSpotSim ? "_gbp=" : "").append(runSpotSim ? groupedbypeer : "")

		.append("_rodadas=").append(AB.toString(rodadas))

		.append("_oursim=").append(runOurSim)

		.append("_spotsim=").append(runSpotSim)

		.append("");

		return sb.toString();

	}

	private static String[] getInstancesSimpleName(String[] spts) {
		String[] retorno = new String[spts.length];
		for (int i = 0; i < spts.length; i++) {
			String spotTraceFileName = spts[i];
			String resto = spotTraceFileName;
			String region = resto.substring(0, resto.indexOf("."));
			resto = resto.substring(resto.indexOf(".") + 1);
			String so = resto.substring(0, resto.indexOf("."));
			resto = resto.substring(resto.indexOf(".") + 1);
			String type = resto.substring(0, resto.lastIndexOf("."));
			retorno[i] = type;
		}
		return retorno;
	}

}
