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
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.parseCommandLine;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.prepareOutputAccounting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import br.edu.ufcg.lsd.oursim.ui.SystemConfigurationCommandParser;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;
import br.edu.ufcg.lsd.spotinstancessimulator.dispatchableevents.spotinstances.SpotPriceEventDispatcher;
import br.edu.ufcg.lsd.spotinstancessimulator.entities.EC2Instance;
import br.edu.ufcg.lsd.spotinstancessimulator.entities.EC2InstanceBadge;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPrice;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPriceFluctuation;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.workload.IosupWorkloadWithBidValue;
import br.edu.ufcg.lsd.spotinstancessimulator.parser.Ec2InstanceParser;
import br.edu.ufcg.lsd.spotinstancessimulator.policy.SpotInstancesMultiCoreSchedulerLimited;
import br.edu.ufcg.lsd.spotinstancessimulator.policy.SpotInstancesScheduler;
import br.edu.ufcg.lsd.spotinstancessimulator.simulationevents.SpotInstancesActiveEntity;
import br.edu.ufcg.lsd.spotinstancessimulator.util.SpotInstaceTraceFormat;

public class SpotCLI {

	public static final String SPOT_INSTANCES = "spot";

	public static final String INSTANCE_TYPE = "type";

	public static final String ALL_INSTANCE_TYPES = "ait";

	public static final String INSTANCE_REGION = "region";

	public static final String INSTANCE_SO = "so";

	public static final String BID_VALUE = "bid";

	public static final String LIMIT = "l";

	public static final String UTILIZATION = "u";

	public static final String SPOT_MACHINES_SPEED = "speed";

	public static final String MACHINES_DESCRIPTION = "md";

	public static final String PEERS_DESCRIPTION = "pd";

	private static final String GROUP_BY_PEEP = "gbp";

	public static void main(String[] args) throws Exception {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		CommandLine cmd = parseCommandLine(args, prepareOptions(), HELP, USAGE, EXECUTION_LINE);

		PrintOutput printOutput = new PrintOutput((File) cmd.getOptionObject(OUTPUT), false);
		JobEventDispatcher.getInstance().addListener(printOutput);

		ComputingElementEventCounter compElemEventCounter = prepareOutputAccounting(cmd, cmd.hasOption(VERBOSE));

		OurSim oursim = null;
		Input<? extends AvailabilityRecord> availability = null;
		Workload workload = null;
		JobSchedulerPolicy jobScheduler = null;

		Grid grid = prepareGrid(cmd);

		String spotTraceFilePath = cmd.getOptionValue(AVAILABILITY);
		List<SpotPrice> refSpotPrices = SpotInstaceTraceFormat.extractReferenceSpotPrices(spotTraceFilePath);

		long timeOfFirstSpotPrice = refSpotPrices.get(SpotInstaceTraceFormat.FIRST).getTime();
		long timeOfLastSpotPrice = refSpotPrices.get(SpotInstaceTraceFormat.LAST).getTime();

		long folga = TimeUtil.ONE_MONTH;
		long randomValue = (new Random()).nextInt((int) (timeOfLastSpotPrice - timeOfFirstSpotPrice - folga));

		long randomPoint = randomValue;

		availability = new SpotPriceFluctuation(spotTraceFilePath, timeOfFirstSpotPrice, randomPoint);

		workload = defineWorkloadToSpotInstances(cmd, grid.getMapOfPeers(), refSpotPrices);

		jobScheduler = createSpotInstancesScheduler(cmd, refSpotPrices);

		BufferedWriter bw = null;
		if (cmd.hasOption(UTILIZATION)) {
			bw = createBufferedWriter((File) cmd.getOptionObject(UTILIZATION));
			grid.setUtilizationBuffer(bw);
		}

		oursim = new OurSim(EventQueue.getInstance(), grid, jobScheduler, workload, availability);

		oursim.setActiveEntity(new SpotInstancesActiveEntity());

		oursim.start();

		printOutput.close();

		FileWriter fw = new FileWriter(cmd.getOptionValue(OUTPUT), true);
		stopWatch.stop();
		fw.write("# Simulation                  duration:" + stopWatch + ".\n");

		EC2Instance ec2Instance = ((SpotInstancesMultiCoreSchedulerLimited) jobScheduler).getEc2Instance();

		fw.write(formatSummaryStatistics(compElemEventCounter, ec2Instance.name, cmd.getOptionValue(LIMIT), ec2Instance.group, cmd.hasOption(GROUP_BY_PEEP),
				grid.getPeers().size(), grid.getListOfPeers().get(0).getNumberOfMachines(), -1, -1, stopWatch.getTime(), stopWatch.toString())
				+ "\n");

		// fw.write(" " + Integer.parseInt(cmd.getOptionValue(LIMIT))+ "\n");

		System.out.println(getSummaryStatistics(compElemEventCounter, ec2Instance.name, cmd.getOptionValue(LIMIT), ec2Instance.group, cmd
				.hasOption(GROUP_BY_PEEP), grid.getListOfPeers().size(), grid.getListOfPeers().get(0).getNumberOfMachines(), -1, -1, stopWatch.getTime(),
				stopWatch.toString()));

		fw.close();

		if (cmd.hasOption(UTILIZATION)) {
			closeBufferedWriter(bw);
		}

		JobEventDispatcher.getInstance().removeListener(printOutput);
		JobEventDispatcher.getInstance().removeListener(compElemEventCounter);
		TaskEventDispatcher.getInstance().removeListener(compElemEventCounter);
		EventQueue.getInstance().clear();

	}

	private static Grid prepareGrid(CommandLine cmd) throws FileNotFoundException {
		Grid grid = null;
		File peerDescriptionFile = (File) cmd.getOptionObject(PEERS_DESCRIPTION);
		File machinesDescriptionFile = (File) cmd.getOptionObject(MACHINES_DESCRIPTION);
		grid = SystemConfigurationCommandParser.readPeersDescription(peerDescriptionFile, machinesDescriptionFile, FifoSharingPolicy.getInstance());
		return grid;
	}

	static Workload defineWorkloadToSpotInstances(CommandLine cmd, Map<String, Peer> peersMap, List<SpotPrice> refSpotPrices) throws IOException,
			java.text.ParseException, FileNotFoundException {
		double bidValue = -1;
		try {
			bidValue = Double.parseDouble(cmd.getOptionValue(BID_VALUE));
		} catch (NumberFormatException e) {
			if (cmd.getOptionValue(BID_VALUE).equals("min")) {
				bidValue = refSpotPrices.get(SpotInstaceTraceFormat.LOWEST).getPrice();
			} else if (cmd.getOptionValue(BID_VALUE).equals("max")) {
				bidValue = refSpotPrices.get(SpotInstaceTraceFormat.HIGHEST).getPrice();
			} else if (cmd.getOptionValue(BID_VALUE).equals("med")) {
				double med = refSpotPrices.get(SpotInstaceTraceFormat.MEAN).getPrice();
				bidValue = med;
			} else {
				System.err.println("bid inválido.");
				System.exit(10);
			}
		}
		return new IosupWorkloadWithBidValue(cmd.getOptionValue(WORKLOAD), peersMap, 0, bidValue);
	}

	static JobSchedulerPolicy createSpotInstancesScheduler(CommandLine cmd, List<SpotPrice> refSpotPrices) throws FileNotFoundException,
			java.text.ParseException {
		JobSchedulerPolicy jobScheduler;
		// String ec2InstancesFilePath = "resources/ec2_instances.txt";
		File ec2InstancesFile = (File)cmd.getOptionObject(ALL_INSTANCE_TYPES);
		EC2Instance ec2Instance;
		if (cmd.hasOption(INSTANCE_TYPE)) {
			ec2Instance = loadEC2InstancesTypes(ec2InstancesFile).get(cmd.getOptionValue(INSTANCE_TYPE));
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
			ec2Instance = loadEC2InstancesTypes(ec2InstancesFile).get(type);
			ec2Instance.name = type;
			ec2Instance.group = type.substring(0, type.indexOf("."));
			ec2Instance.fileName = f.getName();
		}
		Peer spotInstancesPeer = new Peer("SpotInstancesPeer", FifoSharingPolicy.getInstance());

		SpotPrice initialSpotPrice = refSpotPrices.get(SpotInstaceTraceFormat.FIRST);
		int limit = Integer.parseInt(cmd.getOptionValue(LIMIT));
		long speed = ec2Instance.speedByCore;
		jobScheduler = new SpotInstancesMultiCoreSchedulerLimited(spotInstancesPeer, initialSpotPrice, ec2Instance, limit, cmd.hasOption(GROUP_BY_PEEP));
		SpotPriceEventDispatcher.getInstance().addListener((SpotInstancesScheduler) jobScheduler);
		return jobScheduler;
	}

	static Map<String, EC2Instance> loadEC2InstancesTypes(File file) throws FileNotFoundException {
		Ec2InstanceParser parser = new Ec2InstanceParser(new FileInputStream(file));
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
		Option utilization = new Option(UTILIZATION, "utilization", true, "Arquivo em que será registrada a utilização da grade.");
		Option machinesDescription = new Option(MACHINES_DESCRIPTION, "machinesdescription", true, "Descrição das máquinas presentes em cada peer.");
		Option peersDescription = new Option(PEERS_DESCRIPTION, "peers_description", true, "Arquivo descrevendo os peers.");
		Option allInstTypes = new Option(ALL_INSTANCE_TYPES, "all_instance_types", true, "Arquivo descrevendo todas as instâncias spot.");

		workload.setRequired(true);
		output.setRequired(true);
		peersDescription.setRequired(true);
		machinesDescription.setRequired(true);
		allInstTypes.setRequired(true);

		workload.setType(File.class);
		availability.setType(File.class);
		output.setType(File.class);
		utilization.setType(File.class);
		peersDescription.setType(File.class);
		machinesDescription.setType(File.class);
		allInstTypes.setType(File.class);

		options.addOption(peersDescription);
		options.addOption(machinesDescription);
		options.addOption(utilization);
		options.addOption(availability);
		options.addOption(workload);
		options.addOption(output);
		options.addOption(allInstTypes);
		options.addOption(SPOT_INSTANCES, "spot_instances", false, "Simular modelo amazon spot instances.");
		options.addOption(INSTANCE_TYPE, "instance_type", true, "Tipo de instância a ser simulada.");
		options.addOption(INSTANCE_REGION, "instance_region", true, "Região a qual a instância pertence.");
		options.addOption(INSTANCE_SO, "instance_so", true, "Sistema operacional da instância a ser simulada.");
		options.addOption(BID_VALUE, "bid_value", true, "Valor do bid para alocação de instâncias no modelo amazon spot instances..");
		options.addOption(GROUP_BY_PEEP, "group_by_peer", false, "permitir apenas um usuário da cloud por peer.");
		// options.addOption(SPOT_MACHINES_SPEED, "machines_speed", true,
		// "Velocidade das máquinas spot instance.");
		options.addOption(LIMIT, "limit", true, "Número máximo de instâncias simultâneas que podem ser alocadas por usuário.");
		return options;
	}

	private static BufferedWriter createBufferedWriter(File utilizationFile) {
		try {
			if (utilizationFile != null) {
				return new BufferedWriter(new FileWriter(utilizationFile));
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void closeBufferedWriter(BufferedWriter bw) {
		try {
			if (bw != null) {
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
