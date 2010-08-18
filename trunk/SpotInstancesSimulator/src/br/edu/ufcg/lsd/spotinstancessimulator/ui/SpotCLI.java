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
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.hasOptions;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.parseCommandLine;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.prepareOutputAccounting;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.printOutput;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.printResume;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.showMessageAndExit;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.treatWrongCommand;
import static br.edu.ufcg.lsd.spotinstancessimulator.ui.SpotCLIUTil.createSpotInstancesScheduler;
import static br.edu.ufcg.lsd.spotinstancessimulator.ui.SpotCLIUTil.defineWorkloadToSpotInstances;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.time.StopWatch;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.output.ComputingElementEventCounter;
import br.edu.ufcg.lsd.oursim.io.output.PrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.ui.CLI;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPriceFluctuation;
import br.edu.ufcg.lsd.spotinstancessimulator.simulationevents.SpotInstancesActiveEntity;
import br.edu.ufcg.lsd.spotinstancessimulator.util.SpotInstaceTraceFormat;

public class SpotCLI {

	public static final String SPOT_INSTANCES = "spot";

	public static final String INSTANCE_TYPE = "type";

	public static final String INSTANCE_REGION = "region";

	public static final String INSTANCE_SO = "so";

	public static final String BID_VALUE = "bid";

	public static void main(String[] args) throws Exception {

		args = "-spot -bid min -md 3000 -w /home/edigley/workspace/OurSim/resources/nordugrid_setembro_2005.txt -av /home/edigley/local/traces/spot_instances/spot-instance-prices/eu-west-1.linux.m1.small.csv -o oursim_trace.txt"
				.split("\\s+");

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Options options = prepareOptions();

		CommandLine cmd = parseCommandLine(args, options);

		if (hasOptions(cmd, OUTPUT, WORKLOAD)) {

			PrintOutput printOutput = new PrintOutput(cmd.getOptionValue(OUTPUT), true);
			JobEventDispatcher.getInstance().addListener(printOutput);

			ComputingElementEventCounter computingElementEventCounter = prepareOutputAccounting(cmd, VERBOSE);

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

			oursim = new OurSim(EventQueue.getInstance(), peers, jobScheduler, workload, availability);

			oursim.setActiveEntity(new SpotInstancesActiveEntity());

			oursim.start();

			printOutput.close();

			FileWriter fw = new FileWriter(cmd.getOptionValue(OUTPUT), true);

			fw.write(printResume(computingElementEventCounter) + ".\n");

			printOutput(computingElementEventCounter);

			stopWatch.stop();
			fw.write("# Simulation                  duration:" + stopWatch + ".\n");

			fw.close();

		} else {
			treatWrongCommand(options, cmd, HELP, USAGE, EXECUTION_LINE);
		}
	}

	public static Options prepareOptions() {
		Options options = CLI.prepareOptions();
		options.addOption(SPOT_INSTANCES, "spot_instances", false, "Simular modelo amazon spot instances.");
		options.addOption(INSTANCE_TYPE, "instance_type", true, "Tipo de instância a ser simulada.");
		options.addOption(INSTANCE_REGION, "instance_region", true, "Região a qual a instância pertence.");
		options.addOption(INSTANCE_SO, "instance_so", true, "Sistema operacional da instância a ser simulada.");
		options.addOption(BID_VALUE, "bid_value", true, "Valor do bid para alocação de instâncias no modelo amazon spot instances..");
		return options;
	}

}
