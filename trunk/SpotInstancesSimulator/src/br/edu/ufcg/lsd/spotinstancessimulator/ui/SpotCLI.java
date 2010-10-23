/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package br.edu.ufcg.lsd.spotinstancessimulator.ui;

import static br.edu.ufcg.lsd.oursim.ui.CLI.AVAILABILITY;
import static br.edu.ufcg.lsd.oursim.ui.CLI.EXECUTION_LINE;
import static br.edu.ufcg.lsd.oursim.ui.CLI.HELP;
import static br.edu.ufcg.lsd.oursim.ui.CLI.OUTPUT;
import static br.edu.ufcg.lsd.oursim.ui.CLI.USAGE;
import static br.edu.ufcg.lsd.oursim.ui.CLI.VERBOSE;
import static br.edu.ufcg.lsd.oursim.ui.CLI.WORKLOAD;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.formatSummaryStatistics;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.getSummaryStatistics;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.hasOptions;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.parseCommandLine;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.prepareOutputAccounting;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.showMessageAndExit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.time.StopWatch;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Grid;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.output.ComputingElementEventCounter;
import br.edu.ufcg.lsd.oursim.io.output.PrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;
import br.edu.ufcg.lsd.spotinstancessimulator.dispatchableevents.spotinstances.SpotPriceEventDispatcher;
import br.edu.ufcg.lsd.spotinstancessimulator.entities.EC2Instance;
import br.edu.ufcg.lsd.spotinstancessimulator.entities.EC2InstanceBadge;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPrice;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPriceFluctuation;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.workload.IosupWorkloadWithBidValue;
import br.edu.ufcg.lsd.spotinstancessimulator.parser.Ec2InstanceParser;
import br.edu.ufcg.lsd.spotinstancessimulator.policy.SpotInstancesScheduler;
import br.edu.ufcg.lsd.spotinstancessimulator.policy.SpotInstancesSchedulerLimited;
import br.edu.ufcg.lsd.spotinstancessimulator.simulationevents.SpotInstancesActiveEntity;
import br.edu.ufcg.lsd.spotinstancessimulator.util.SpotInstaceTraceFormat;

public class SpotCLI {

	public static final String SPOT_INSTANCES = "spot";

	public static final String INSTANCE_TYPE = "type";

	public static final String INSTANCE_REGION = "region";

	public static final String INSTANCE_SO = "so";

	public static final String BID_VALUE = "bid";

	public static final String LIMIT = "l";

	public static void main(String[] args) throws Exception {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		CommandLine cmd = parseCommandLine(args, prepareOptions(), HELP, USAGE, EXECUTION_LINE);

		PrintOutput printOutput = new PrintOutput((File) cmd.getOptionObject(OUTPUT), false);
		JobEventDispatcher.getInstance().addListener(printOutput);

		ComputingElementEventCounter computingElementEventCounter = prepareOutputAccounting(cmd, cmd.hasOption(VERBOSE));

		OurSim oursim = null;
		Input<? extends AvailabilityRecord> availability = null;
		Workload workload = null;
		JobSchedulerPolicy jobScheduler = null;
		Map<String, Peer> peersMap = null;

		if (cmd.hasOption(SPOT_INSTANCES)) {
			peersMap = GWAFormat.extractPeersFromGWAFile(cmd.getOptionValue(WORKLOAD), 0, FifoSharingPolicy.getInstance());
			String spotTraceFilePath = cmd.getOptionValue(AVAILABILITY);
			long timeOfFirstSpotPrice = SpotInstaceTraceFormat.extractTimeFromFirstSpotPrice(spotTraceFilePath);
			availability = new SpotPriceFluctuation(spotTraceFilePath, timeOfFirstSpotPrice);
			workload = defineWorkloadToSpotInstances(cmd, cmd.getOptionValue(WORKLOAD), workload, peersMap, spotTraceFilePath, BID_VALUE);
		} else {
			showMessageAndExit("Essa interface é só para spot instances.");
		}

		ArrayList<Peer> peers = new ArrayList<Peer>(peersMap.values());

		jobScheduler = createSpotInstancesScheduler(cmd, INSTANCE_TYPE, INSTANCE_REGION, INSTANCE_SO, AVAILABILITY);

		Grid grid = new Grid(peers);
		
		oursim = new OurSim(EventQueue.getInstance(), grid, jobScheduler, workload, availability);

		oursim.setActiveEntity(new SpotInstancesActiveEntity());

		oursim.start();

		printOutput.close();

		FileWriter fw = new FileWriter(cmd.getOptionValue(OUTPUT), true);
		stopWatch.stop();
		fw.write("# Simulation                  duration:" + stopWatch + ".\n");

		fw.write(formatSummaryStatistics(computingElementEventCounter,-1,-1,-1,stopWatch.getTime()) + ".\n");

		System.out.println(getSummaryStatistics(computingElementEventCounter,-1,-1,-1,stopWatch.getTime()));

		fw.close();
		JobEventDispatcher.getInstance().removeListener(printOutput);
		JobEventDispatcher.getInstance().removeListener(computingElementEventCounter);
		TaskEventDispatcher.getInstance().removeListener(computingElementEventCounter);
		EventQueue.getInstance().clear();

	}

	static Workload defineWorkloadToSpotInstances(CommandLine cmd, String workloadFilePath, Workload workload, Map<String, Peer> peersMap,
			String spotTraceFilePath, String BID_VALUE) throws IOException, java.text.ParseException, FileNotFoundException {
		if (hasOptions(cmd, BID_VALUE)) {
			// long timeOfFirstSubmission =
			// GWAFormat.extractSubmissionTimeFromFirstJob(workloadFilePath);
			double bidValue = -1;
			try {
				bidValue = Double.parseDouble(cmd.getOptionValue(BID_VALUE));
			} catch (NumberFormatException e) {
				if (cmd.getOptionValue(BID_VALUE).equals("min")) {
					bidValue = SpotInstaceTraceFormat.extractlowestSpotPrice(spotTraceFilePath).getPrice();
				} else if (cmd.getOptionValue(BID_VALUE).equals("max")) {
					bidValue = SpotInstaceTraceFormat.extractHighestSpotPrice(spotTraceFilePath).getPrice();
				} else if (cmd.getOptionValue(BID_VALUE).equals("med")) {
					double min = SpotInstaceTraceFormat.extractlowestSpotPrice(spotTraceFilePath).getPrice();
					double max = SpotInstaceTraceFormat.extractHighestSpotPrice(spotTraceFilePath).getPrice();
					double med = (min + max) / 2.0;
					bidValue = med;
				} else {
					System.err.println("bid inválido.");
					System.exit(10);
				}
			}
			// workload = new
			// OnDemandGWANorduGridWorkloadWithBidValue(workloadFilePath,
			// peersMap, timeOfFirstSubmission, bidValue);
			workload = new IosupWorkloadWithBidValue(workloadFilePath, peersMap, 0, bidValue);
		} else {
			System.err.println("Combinação de parâmetros de spot-instances inválida.");
		}
		return workload;
	}

	static JobSchedulerPolicy createSpotInstancesScheduler(CommandLine cmd, String INSTANCE_TYPE, String INSTANCE_REGION, String INSTANCE_SO,
			String AVAILABILITY) throws FileNotFoundException, java.text.ParseException {
		JobSchedulerPolicy jobScheduler;
		String ec2InstancesFilePath = "resources/ec2_instances.txt";
		EC2Instance ec2Instance;
		if (cmd.hasOption(INSTANCE_TYPE)) {
			ec2Instance = loadEC2InstancesTypes(ec2InstancesFilePath).get(cmd.getOptionValue(INSTANCE_TYPE));
			EC2InstanceBadge badge = ec2Instance.getBadge(cmd.getOptionValue(INSTANCE_REGION), cmd.getOptionValue(INSTANCE_SO));
		} else {
			// us-west-1.windows.m2.4xlarge.csv
			File f = new File(cmd.getOptionValue(AVAILABILITY));
			String spotTraceFileName = f.getName();
			String resto = spotTraceFileName;
			String region = resto.substring(0, resto.indexOf("."));
			resto = resto.substring(resto.indexOf(".") + 1);
			String so = resto.substring(0, resto.indexOf("."));
			resto = resto.substring(resto.indexOf(".") + 1);
			String type = resto.substring(0, resto.lastIndexOf("."));
			ec2Instance = loadEC2InstancesTypes(ec2InstancesFilePath).get(type);
		}
		Peer spotInstancesPeer = new Peer("SpotInstancesPeer", FifoSharingPolicy.getInstance());

		SpotPrice initialSpotPrice = SpotInstaceTraceFormat.extractFirstSpotPrice(cmd.getOptionValue(AVAILABILITY));
		// jobScheduler = new SpotInstancesScheduler(spotInstancesPeer,
		// initialSpotPrice, ec2Instance.speed);
		int limit = Integer.parseInt(cmd.getOptionValue(LIMIT));
		jobScheduler = new SpotInstancesSchedulerLimited(spotInstancesPeer, initialSpotPrice, ec2Instance.speed, limit);
		SpotPriceEventDispatcher.getInstance().addListener((SpotInstancesScheduler) jobScheduler);
		return jobScheduler;
	}

	static Map<String, EC2Instance> loadEC2InstancesTypes(String filePath) throws FileNotFoundException {
		Ec2InstanceParser parser = new Ec2InstanceParser(new FileInputStream(new File(filePath)));
		Map<String, EC2Instance> ec2Instances = new HashMap<String, EC2Instance>();
		try {
			List<EC2Instance> result = parser.parse();
			for (EC2Instance ec2Instance : result) {
				ec2Instances.put(ec2Instance.type, ec2Instance);
			}
		} catch (Exception e) {
			System.exit(3);
		}
		return ec2Instances;
	}

	public static Options prepareOptions() {
		Options options = new Options();
		Option availability = new Option(AVAILABILITY, "availability", true, "Arquivo com a caracterização da disponibilidade para todos os recursos.");
		Option workload = new Option(WORKLOAD, "workload", true, "Arquivo com o workload no format GWA (Grid Workload Archive).");
		Option output = new Option(OUTPUT, "output", true, "O nome do arquivo em que o output da simulação será gravado.");

		workload.setRequired(true);
		output.setRequired(true);

		workload.setType(File.class);
		availability.setType(File.class);
		output.setType(File.class);

		options.addOption(availability);
		options.addOption(workload);
		options.addOption(output);
		options.addOption(SPOT_INSTANCES, "spot_instances", false, "Simular modelo amazon spot instances.");
		options.addOption(INSTANCE_TYPE, "instance_type", true, "Tipo de instância a ser simulada.");
		options.addOption(INSTANCE_REGION, "instance_region", true, "Região a qual a instância pertence.");
		options.addOption(INSTANCE_SO, "instance_so", true, "Sistema operacional da instância a ser simulada.");
		options.addOption(BID_VALUE, "bid_value", true, "Valor do bid para alocação de instâncias no modelo amazon spot instances..");
		options.addOption(LIMIT, "limit", true, "Número máximo de instâncias simultâneas que podem ser alocadas por usuário.");
		return options;
	}

}
