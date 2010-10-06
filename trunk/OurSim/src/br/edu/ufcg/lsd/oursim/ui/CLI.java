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

package br.edu.ufcg.lsd.oursim.ui;

import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.formatSummaryStatistics;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.parseCommandLine;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.prepareOutputAccounting;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.showMessageAndExit;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.showSummaryStatistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.time.StopWatch;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.availability.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.MarkovModelAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.workload.IosupWorkload;
import br.edu.ufcg.lsd.oursim.io.input.workload.MarcusWorkload;
import br.edu.ufcg.lsd.oursim.io.input.workload.OnDemandBoTGWANorduGridWorkload;
import br.edu.ufcg.lsd.oursim.io.input.workload.OnDemandGWANorduGridWorkload;
import br.edu.ufcg.lsd.oursim.io.input.workload.OnDemandGWANorduGridWorkloadWithFilter;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.output.ComputingElementEventCounter;
import br.edu.ufcg.lsd.oursim.io.output.PrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.policy.NoFSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.OurGridPersistentScheduler;
import br.edu.ufcg.lsd.oursim.policy.OurGridReplicationScheduler;
import br.edu.ufcg.lsd.oursim.policy.OurGridScheduler;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityImp;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.util.AvailabilityTraceFormat;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class CLI {

	public static final Random RANDOM = new Random(9354269l);

	public static final String NOF = "nof";

	public static final String AVAILABILITY = "av";

	public static final String SYNTHETIC_AVAILABILITY = "synthetic_av";

	public static final String MACHINES_DESCRIPTION = "md";

	public static final String DEDICATED_RESOURCES = "d";

	public static final String VERBOSE = "v";

	public static final String WORKLOAD = "w";

	public static final String WORKLOAD_TYPE = "wt";

	public static final String NUM_PEERS = "np";

	public static final String NUM_RESOURCES_BY_PEER = "nr";

	public static final String PEERS_DESCRIPTION = "pd";

	public static final String NODE_MIPS_RATING = "r";

	public static final String SCHEDULER = "s";

	public static final String OUTPUT = "o";

	public static final String HELP = "help";

	public static final String USAGE = "usage";

	public static final String EXECUTION_LINE = "java -jar oursim.jar";

	/**
	 * Exemplo:
	 * 
	 * <pre>
	 *   java -jar oursim.jar -w resources/trace_filtrado_primeiros_1000_jobs.txt -m resources/hostinfo_sdsc.dat -synthetic_av -o oursim_trace.txt
	 *   -w resources/trace_filtrado_primeiros_1000_jobs.txt -s persistent -nr 20 -md resources/hostinfo_sdsc.dat -av resources/disponibilidade.txt -o oursim_trace.txt
	 *   -w resources/new_iosup_workload.txt -s persistent -pd resources/iosup_site_description.txt -wt iosup -nr 1 -synthetic_av -o oursim_trace.txt
	 *   -w resources/new_workload.txt -s persistent -pd resources/marcus_site_description.txt -wt marcus -nr 20 -d -o oursim_trace.txt
	 *   1 mês + 1 dia = 2678400 segundos
	 * </pre>
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws IOException {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		CommandLine cmd = parseCommandLine(args, prepareOptions(), HELP, USAGE, EXECUTION_LINE);

		PrintOutput printOutput = new PrintOutput((File) cmd.getOptionObject(OUTPUT), false);
		JobEventDispatcher.getInstance().addListener(printOutput);

		ComputingElementEventCounter computingElementEventCounter = prepareOutputAccounting(cmd, cmd.hasOption(VERBOSE));

		ResourceSharingPolicy sharingPolicy = cmd.hasOption(NOF) ? NoFSharingPolicy.getInstance() : FifoSharingPolicy.getInstance();

		Map<String, Peer> peersMap = prepareGrid(cmd, sharingPolicy);

		Input<? extends AvailabilityRecord> availability = defineAvailability(cmd, peersMap);

		long timeOfFirstSubmission = cmd.getOptionValue(WORKLOAD_TYPE).equals("gwa") ? GWAFormat
				.extractSubmissionTimeFromFirstJob(cmd.getOptionValue(WORKLOAD)) : 0;
		Workload workload = defineWorkloadType(cmd, cmd.getOptionValue(WORKLOAD), peersMap, timeOfFirstSubmission);

		ArrayList<Peer> peers = new ArrayList<Peer>(peersMap.values());

		JobSchedulerPolicy jobScheduler = defineScheduler(cmd, peers);

		OurSim oursim = new OurSim(EventQueue.getInstance(), peers, jobScheduler, workload, availability);

		oursim.setActiveEntity(new ActiveEntityImp());

		oursim.start();

		printOutput.close();

		FileWriter fw = new FileWriter(cmd.getOptionValue(OUTPUT), true);

		fw.write(formatSummaryStatistics(computingElementEventCounter) + ".\n");

		showSummaryStatistics(computingElementEventCounter);

		stopWatch.stop();
		fw.write("# Simulation                  duration:" + stopWatch + ".\n");

		fw.close();

	}

	private static Map<String, Peer> prepareGrid(CommandLine cmd, ResourceSharingPolicy sharingPolicy) throws FileNotFoundException {
		int numberOfResourcesByPeer = Integer.parseInt(cmd.getOptionValue(NUM_RESOURCES_BY_PEER, "0"));
		File peerDescriptionFile = (File) cmd.getOptionObject(PEERS_DESCRIPTION);
		Map<String, Peer> peersMap = SystemConfigurationCommandParser.readPeersDescription(peerDescriptionFile, numberOfResourcesByPeer, sharingPolicy);
		return peersMap;
	}

	private static Workload defineWorkloadType(CommandLine cmd, String workloadFilePath, Map<String, Peer> peersMap, long timeOfFirstSubmission)
			throws FileNotFoundException {
		Workload workload = null;
		if (cmd.hasOption(WORKLOAD_TYPE)) {
			String type = cmd.getOptionValue(WORKLOAD_TYPE);
			if (type.equals("bot")) {
				workload = new OnDemandBoTGWANorduGridWorkload(workloadFilePath, peersMap, timeOfFirstSubmission);
			} else if (type.equals("filter")) {
				workload = new OnDemandGWANorduGridWorkloadWithFilter(workloadFilePath, peersMap, timeOfFirstSubmission);
			} else if (type.equals("marcus")) {
				workload = new MarcusWorkload(workloadFilePath, peersMap, TimeUtil.ONE_MONTH);
				JobEventDispatcher.getInstance().addListener((MarcusWorkload) workload);
			} else if (type.equals("iosup")) {
				workload = new IosupWorkload(workloadFilePath, peersMap, 0);
			}
		} else {
			workload = new OnDemandGWANorduGridWorkload(workloadFilePath, peersMap, timeOfFirstSubmission);
		}

		return workload;
	}

	private static Input<AvailabilityRecord> defineAvailability(CommandLine cmd, Map<String, Peer> peersMap) throws FileNotFoundException {
		Input<AvailabilityRecord> availability = null;
		if (cmd.hasOption(DEDICATED_RESOURCES)) {
			availability = new DedicatedResourcesAvailabilityCharacterization(peersMap.values());
		} else if (cmd.hasOption(AVAILABILITY)) {
			long startingTime = AvailabilityTraceFormat.extractTimeFromFirstAvailabilityRecord(cmd.getOptionValue(AVAILABILITY), true);
			availability = new AvailabilityCharacterization(cmd.getOptionValue(AVAILABILITY), startingTime, true);
		} else if (cmd.hasOption(SYNTHETIC_AVAILABILITY)) {
			long availabilityThreshold = ((Number) cmd.getOptionObject(SYNTHETIC_AVAILABILITY)).longValue();
			availability = new MarkovModelAvailabilityCharacterization(peersMap, availabilityThreshold, 0);
		}

		if (availability == null) {
			showMessageAndExit("Combinação de parâmetros de availability inválida.");
		}

		return availability;
	}

	private static JobSchedulerPolicy defineScheduler(CommandLine cmd, ArrayList<Peer> peers) {
		JobSchedulerPolicy jobScheduler = null;
		if (cmd.hasOption(SCHEDULER)) {
			String scheduler = cmd.getOptionValue(SCHEDULER);
			if (scheduler.equals("persistent")) {
				jobScheduler = new OurGridPersistentScheduler(peers);
			} else if (scheduler.equals("replication") && cmd.getOptionValues(SCHEDULER).length == 2) {
				int numberOfReplicas = Integer.parseInt(cmd.getOptionValues(SCHEDULER)[1]);
				jobScheduler = new OurGridReplicationScheduler(peers, numberOfReplicas);
			}
		} else {
			jobScheduler = new OurGridScheduler(peers);
		}

		if (jobScheduler == null) {
			showMessageAndExit("Deve informar um tipo válido de scheduler.");
		}

		return jobScheduler;
	}

	public static Options prepareOptions() {
		Options options = new Options();

		Option availability = new Option(AVAILABILITY, "availability", true, "Arquivo com a caracterização da disponibilidade para todos os recursos.");
		Option dedicatedResources = new Option(DEDICATED_RESOURCES, "dedicated", false, "Indica que os recursos são todos dedicados.");
		Option syntAvail = new Option(SYNTHETIC_AVAILABILITY, "synthetic_availability", true, "Disponibilidade dos recursos deve ser gerada sinteticamente.");
		Option workload = new Option(WORKLOAD, "workload", true, "Arquivo com o workload no format GWA (Grid Workload Archive).");
		Option workloadType = new Option(WORKLOAD_TYPE, "workload_type", true, "The type of workload to read the workload file.");
		Option machinesDescription = new Option(MACHINES_DESCRIPTION, "machinesdescription", true, "Descrição das máquinas presentes em cada peer.");
		Option speedOption = new Option(NODE_MIPS_RATING, "speed", true, "A velocidade de cada máquina.");
		Option scheduler = new Option(SCHEDULER, "scheduler", true, "Indica qual scheduler deverá ser usado.");
		Option peersDescription = new Option(PEERS_DESCRIPTION, "peers_description", true, "Arquivo descrevendo os peers.");
		Option numResByPeer = new Option(NUM_RESOURCES_BY_PEER, "nresources", true, "O número de réplicas para cada task.");
		Option numPeers = new Option(NUM_PEERS, "npeers", true, "O número de peers do grid.");
		Option nofOption = new Option(NOF, "nof", false, "Utiliza a Rede de Favores (NoF).");
		Option output = new Option(OUTPUT, "output", true, "O nome do arquivo em que o output da simulação será gravado.");

		workload.setRequired(true);
		peersDescription.setRequired(true);
		output.setRequired(true);

		workload.setType(File.class);
		peersDescription.setType(File.class);
		machinesDescription.setType(File.class);
		availability.setType(File.class);
		output.setType(File.class);
		numResByPeer.setType(Number.class);
		numPeers.setType(Number.class);
		speedOption.setType(Number.class);
		syntAvail.setType(Number.class);

		scheduler.setArgs(2);

		OptionGroup availGroup = new OptionGroup();
		availGroup.addOption(availability);
		availGroup.addOption(dedicatedResources);
		availGroup.addOption(syntAvail);

		options.addOptionGroup(availGroup);

		options.addOption(workload);
		options.addOption(workloadType);
		options.addOption(machinesDescription);
		options.addOption(scheduler);
		options.addOption(output);
		options.addOption(numResByPeer);
		options.addOption(numPeers);
		options.addOption(peersDescription);
		options.addOption(speedOption);
		options.addOption(nofOption);

		options.addOption(VERBOSE, "verbose", false, "Informa todos os eventos importantes.");
		options.addOption(HELP, false, "Comando de ajuda.");
		options.addOption(USAGE, false, "Instruções de uso.");

		return options;
	}

}
