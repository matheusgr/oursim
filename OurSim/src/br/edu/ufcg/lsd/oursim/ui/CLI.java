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

import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.hasOptions;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.parseCommandLine;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.prepareOutputAccounting;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.printOutput;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.printResume;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.showMessageAndExit;
import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.treatWrongCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.time.StopWatch;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Processor;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.availability.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.MarkovModelAvailabilityCharacterization;
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
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityAbstract;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.util.AvailabilityTraceFormat;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;

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

	public static final String REPLIES = "r";

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
	 * </pre>
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws Exception {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Options options = prepareOptions();

		CommandLine cmd = parseCommandLine(args, options);

		if (hasOptions(cmd, OUTPUT, WORKLOAD)) {

			PrintOutput printOutput = new PrintOutput(cmd.getOptionValue(OUTPUT), true);
			JobEventDispatcher.getInstance().addListener(printOutput);

			ComputingElementEventCounter computingElementEventCounter = prepareOutputAccounting(cmd, VERBOSE);

			ResourceSharingPolicy sharingPolicy = cmd.hasOption(NOF) ? NoFSharingPolicy.getInstance() : FifoSharingPolicy.getInstance();

			Map<String, Peer> peersMap = prepareGrid(cmd, sharingPolicy);

			Input<? extends AvailabilityRecord> availability = prepareSystemAvailability(cmd, peersMap);

			Workload workload = prepareWorkload(cmd, peersMap);

			ArrayList<Peer> peers = new ArrayList<Peer>(peersMap.values());

			JobSchedulerPolicy jobScheduler = defineScheduler(cmd, peers);

			OurSim oursim = new OurSim(EventQueue.getInstance(), peers, jobScheduler, workload, availability);

			oursim.setActiveEntity(new ActiveEntityAbstract());

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

	private static Workload prepareWorkload(CommandLine cmd, Map<String, Peer> peersMap) throws IOException, FileNotFoundException {
		Workload workload;
		long timeOfFirstSubmission = GWAFormat.extractSubmissionTimeFromFirstJob(cmd.getOptionValue(WORKLOAD));
		workload = defineWorkloadType(cmd, cmd.getOptionValue(WORKLOAD), peersMap, timeOfFirstSubmission);
		return workload;
	}

	private static Input<? extends AvailabilityRecord> prepareSystemAvailability(CommandLine cmd, Map<String, Peer> peersMap) throws IOException,
			FileNotFoundException {
		Input<? extends AvailabilityRecord> availability;
		long durationOfWorkloadInSeconds = GWAFormat.extractDurationInSecondsOfWorkload(cmd.getOptionValue(WORKLOAD));
		int amountOfSecondsInADay = 60 * 60 * 24;
		// adiciona um dia além da duração do workload
		durationOfWorkloadInSeconds += amountOfSecondsInADay;
		availability = defineAvailability(cmd, peersMap, durationOfWorkloadInSeconds);
		return availability;
	}

	private static Map<String, Peer> prepareGrid(CommandLine cmd, ResourceSharingPolicy sharingPolicy) throws FileNotFoundException {
		Map<String, Peer> peersMap;
		int numberOfResourcesByPeer = cmd.hasOption(NUM_RESOURCES_BY_PEER) ? Integer.parseInt(cmd.getOptionValue(NUM_RESOURCES_BY_PEER)) : 0;
		peersMap = GWAFormat.extractPeersFromGWAFile(cmd.getOptionValue(WORKLOAD), numberOfResourcesByPeer, sharingPolicy);
		if (numberOfResourcesByPeer == 0 && cmd.hasOption(PEERS_DESCRIPTION)) {
			String peersDescriptionFilePath = cmd.getOptionValue(PEERS_DESCRIPTION);
			addResourcesToPeers(peersMap, peersDescriptionFilePath);
		}
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
			}
		} else {
			workload = new OnDemandGWANorduGridWorkload(workloadFilePath, peersMap, timeOfFirstSubmission);
		}

		return workload;
	}

	private static Input<AvailabilityRecord> defineAvailability(CommandLine cmd, Map<String, Peer> peersMap, long durationOfWorkload)
			throws FileNotFoundException {
		Input<AvailabilityRecord> availability = null;
		if (cmd.hasOption(DEDICATED_RESOURCES)) {
			availability = new DedicatedResourcesAvailabilityCharacterization(peersMap.values());
		} else if (cmd.hasOption(AVAILABILITY)) {
			long startingTime = AvailabilityTraceFormat.extractTimeFromFirstAvailabilityRecord(cmd.getOptionValue(AVAILABILITY), true);
			availability = new AvailabilityCharacterization(cmd.getOptionValue(AVAILABILITY), startingTime, true);
		} else if (cmd.hasOption(SYNTHETIC_AVAILABILITY)) {
			availability = new MarkovModelAvailabilityCharacterization(peersMap, durationOfWorkload, 0);
		}

		if (availability == null) {
			showMessageAndExit("Combinação de parâmetros de availability inválida.");
		}

		return availability;
	}

	private static JobSchedulerPolicy defineScheduler(CommandLine cmd, ArrayList<Peer> peers) throws FileNotFoundException, java.text.ParseException {
		JobSchedulerPolicy jobScheduler = null;
		if (cmd.hasOption(SCHEDULER)) {
			String scheduler = cmd.getOptionValue(SCHEDULER);
			if (scheduler.equals("persistent")) {
				jobScheduler = new OurGridPersistentScheduler(peers);
			} else if (scheduler.equals("replication") && cmd.hasOption(REPLIES)) {
				int numberOfReplies = Integer.parseInt(cmd.getOptionValue(REPLIES));
				jobScheduler = new OurGridReplicationScheduler(peers, numberOfReplies);
			}
		} else {
			jobScheduler = new OurGridScheduler(peers);
		}

		if (jobScheduler == null) {
			showMessageAndExit("Deve informar um tipo válido de scheduler.");
		}

		return jobScheduler;
	}

	private static void addResourcesToPeers(Map<String, Peer> peersMap, String peersDescriptionFilePath) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(peersDescriptionFilePath));
		sc.nextLine();// desconsidera o cabeçalho
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			String peerName = scLine.next();
			if (peersMap.containsKey(peerName)) {
				int peerSize = scLine.nextInt();
				Peer peer = peersMap.get(peerName);
				for (int i = 0; i < peerSize; i++) {
					String machineFullName = peer.getName() + "_m_" + i;
					peer.addMachine(new Machine(machineFullName, Processor.EC2_COMPUTE_UNIT.getSpeed()));
				}
			}
		}
	}

	public static Options prepareOptions() {
		Options options = new Options();

		options.addOption(AVAILABILITY, "availability", true, "Arquivo com a caracterização da disponibilidade para todos os recursos.");
		options.addOption(WORKLOAD, "workload", true, "Arquivo com o workload no format GWA (Grid Workload Archive).");
		options.addOption(WORKLOAD_TYPE, "workload_type", true, "The type of workload to read the workload file.");
		options.addOption(MACHINES_DESCRIPTION, "machinesdescription", true, "Arquivo com a descrição das máquinas presentes em cada peer.");
		options.addOption(SCHEDULER, "scheduler", true, "Indica qual scheduler deverá ser usado.");
		options.addOption(REPLIES, "replies", true, "O número de réplicas para cada task.");
		options.addOption(OUTPUT, "output", true, "O nome do arquivo em que o output da simulação será gravado.");
		options.addOption(NUM_RESOURCES_BY_PEER, "nresources", true, "O número de réplicas para cada task.");
		options.addOption(NUM_PEERS, "npeers", true, "O número de peers do grid.");
		options.addOption(PEERS_DESCRIPTION, "peers_description", true, "Arquivo descrevendo os peers.");
		options.addOption(NODE_MIPS_RATING, "speed", true, "A velocidade de cada máquina.");
		options.addOption(NOF, "nof", false, "Utiliza a Rede de Favores (NoF).");
		options.addOption(DEDICATED_RESOURCES, "dedicated", false, "Indica que os recursos são todos dedicados.");
		options.addOption(VERBOSE, "verbose", false, "Informa todos os eventos importantes.");
		options.addOption(SYNTHETIC_AVAILABILITY, "synthetic_availability", false, "Indica que a disponibilidade dos recursos deve ser gerada sinteticamente.");
		options.addOption(HELP, false, "Comando de ajuda.");
		options.addOption(USAGE, false, "Instruções de uso.");

		return options;
	}

}
