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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.time.StopWatch;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances.SpotPriceEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Processor;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.availability.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.MarkovModelAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPrice;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPriceFluctuation;
import br.edu.ufcg.lsd.oursim.io.input.workload.OnDemandGWANorduGridWorkload;
import br.edu.ufcg.lsd.oursim.io.input.workload.OnDemandGWANorduGridWorkloadWithBidValue;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.output.JobPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.PrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.TaskPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.WorkerEventsPrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.policy.NoFSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.OurGridPersistentScheduler;
import br.edu.ufcg.lsd.oursim.policy.OurGridReplicationScheduler;
import br.edu.ufcg.lsd.oursim.policy.OurGridScheduler;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.SpotInstancesScheduler;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.util.AvailabilityTraceFormat;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;
import br.edu.ufcg.lsd.oursim.util.SpotInstaceTraceFormat;

public class CLI {

	public static final Random RANDOM = new Random(9354269l);

	private static final String NOF = "nof";

	private static final String AVAILABILITY = "av";

	private static final String SYNTHETIC_AVAILABILITY = "synthetic_av";

	private static final String MACHINES_DESCRIPTION = "md";

	private static final String DEDICATED_RESOURCES = "d";

	private static final String VERBOSE = "v";

	private static final String WORKLOAD = "w";

	private static final String REPLIES = "r";

	private static final String NUM_PEERS = "np";

	private static final String NUM_RESOURCES_BY_PEER = "nr";

	private static final String PEERS_DESCRIPTION = "pd";

	private static final String NODE_MIPS_RATING = "r";

	private static final String SCHEDULER = "s";

	private static final String OUTPUT = "o";

	private static final String SPOT_INSTANCES = "spot";

	private static final String BID_VALUE = "bid";

	private static final String HELP = "help";

	private static final String USAGE = "usage";

	private static final String EXECUTION_LINE = "java -jar oursim.jar";

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

		args = "-v -w resources/nordugrid_janeiro_2006.txt -s replication -r 3 -pd resources/nordugrid_site_description.txt -d -o oursim_trace.txt"
				.split("\\s+");
		args = "-w resources/nordugrid_janeiro_2006.txt -s replication -r 3 -pd resources/nordugrid_site_description.txt -synthetic_av -o oursim_trace.txt"
				.split("\\s+");
		args = "-spot -bid 0.042 -md 3000 -w resources/nordugrid_setembro_2005.txt -av /home/edigley/local/traces/spot_instances/spot-instance-prices/eu-west-1.linux.m1.small.csv -o oursim_trace.txt"
				.split("\\s+");

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Options options = prepareOptions();

		CommandLine cmd = parseCommandLine(args, options);

		if (hasOptions(cmd, OUTPUT, WORKLOAD)) {

			String workloadFilePath = cmd.getOptionValue(WORKLOAD);
			// JobEventDispatcher.getInstance().addListener(new
			// PrintOutput(cmd.getOptionValue(OUTPUT)));
			JobEventDispatcher.getInstance().addListener(new PrintOutput());

			if (cmd.hasOption(VERBOSE)) {
				JobEventDispatcher.getInstance().addListener(new JobPrintOutput());
				TaskEventDispatcher.getInstance().addListener(new TaskPrintOutput());
				WorkerEventDispatcher.getInstance().addListener(new WorkerEventsPrintOutput());
				EventQueue.LOG = true;
				EventQueue.LOG_FILEPATH = "events_oursim.txt";
			}

			JobEventCounter jobEventCounter = new JobEventCounter();
			JobEventDispatcher.getInstance().addListener(jobEventCounter);

			TaskEventCounter taskEventCounter = new TaskEventCounter();
			TaskEventDispatcher.getInstance().addListener(taskEventCounter);

			OurSim oursim = null;
			Input<? extends AvailabilityRecord> availability = null;
			Workload workload = null;
			JobSchedulerPolicy jobScheduler = null;
			Map<String, Peer> peersMap = null;

			if (!cmd.hasOption(SPOT_INSTANCES)) {
				ResourceSharingPolicy sharingPolicy = cmd.hasOption(NOF) ? NoFSharingPolicy.getInstance() : FifoSharingPolicy.getInstance();
				int numberOfResourcesByPeer = cmd.hasOption(NUM_RESOURCES_BY_PEER) ? Integer.parseInt(cmd.getOptionValue(NUM_RESOURCES_BY_PEER)) : 0;
				peersMap = GWAFormat.extractPeersFromGWAFile(workloadFilePath, numberOfResourcesByPeer, sharingPolicy);
				if (numberOfResourcesByPeer == 0 && cmd.hasOption(PEERS_DESCRIPTION)) {
					String peersDescriptionFilePath = cmd.getOptionValue(PEERS_DESCRIPTION);
					addResourcesToPeers(peersMap, peersDescriptionFilePath);
				}
				long durationOfWorkloadInSeconds = GWAFormat.extractDurationInSecondsOfWorkload(workloadFilePath);
				int amountOfSecondsInADay = 60 * 60 * 24;
				// adiciona um dia além da duração do workload
				durationOfWorkloadInSeconds += amountOfSecondsInADay;
				availability = defineAvailability(cmd, peersMap, durationOfWorkloadInSeconds);
				long timeOfFirstSubmission = GWAFormat.extractSubmissionTimeFromFirstJob(workloadFilePath);
				workload = new OnDemandGWANorduGridWorkload(workloadFilePath, peersMap, timeOfFirstSubmission);
			} else {
				peersMap = GWAFormat.extractPeersFromGWAFile(workloadFilePath, 0, FifoSharingPolicy.getInstance());
				String spotTraceFilePath = cmd.getOptionValue(AVAILABILITY);
				long timeOfFirstSpotPrice = SpotInstaceTraceFormat.extractTimeFromFirstSpotPrice(spotTraceFilePath);
				availability = new SpotPriceFluctuation(spotTraceFilePath, timeOfFirstSpotPrice);
				long timeOfFirstSubmission = GWAFormat.extractSubmissionTimeFromFirstJob(workloadFilePath);
				double bidValue = Double.parseDouble(cmd.getOptionValue(BID_VALUE));
				workload = new OnDemandGWANorduGridWorkloadWithBidValue(workloadFilePath, peersMap, timeOfFirstSubmission, bidValue);

			}

			if (availability == null) {
				System.err.println("Combinação de parâmetros de availability inválida.");
				System.exit(10);
			}

			ArrayList<Peer> peers = new ArrayList<Peer>(peersMap.values());

			jobScheduler = defineScheduler(cmd, peers);

			if (jobScheduler == null) {
				System.err.println("Deve informar um tipo válido de scheduler.");
				System.exit(1);
			}

			oursim = new OurSim(EventQueue.getInstance(), peers, jobScheduler, workload, availability);

			System.out.println("Starting Simulation...");

			oursim.start();

			System.out.println("Simulation ended.");

			System.out.println("# Total of submitted            jobs: " + jobEventCounter.getNumberOfSubmittedJobs());
			System.out.println("# Total of finished             jobs: " + jobEventCounter.getNumberOfFinishedJobs());
			System.out.println("# Total of preemptions for all  jobs: " + jobEventCounter.getNumberOfPreemptionsForAllJobs());
			System.out.println("# Total of finished            tasks: " + taskEventCounter.getNumberOfFinishedTasks());
			System.out.println("# Total of preemptions for all tasks: " + taskEventCounter.getNumberOfPreemptionsForAllTasks());
			System.out.println("# Total of                    events: " + EventQueue.totalNumberOfEvents);

			stopWatch.stop();
			System.out.println("Simulation                  duration:" + stopWatch);

		} else {
			System.err.println("Informe todos os parâmetros obrigatórios.");
			HelpFormatter formatter = new HelpFormatter();
			if (cmd.hasOption(HELP)) {
				formatter.printHelp(EXECUTION_LINE, options);
			} else if (cmd.hasOption(USAGE)) {
				formatter.printHelp(EXECUTION_LINE, options, true);
			} else {
				formatter.printHelp(EXECUTION_LINE, options, true);
			}
			System.exit(1);
		}
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
		return availability;
	}

	private static void addResourcesToPeers(Map<String, Peer> peersMap, String peersDescriptionFilePath) throws FileNotFoundException {
		// AvailabilityTraceFormat.addResourcesToPeer(peersMap.values().iterator().next(),
		// cmd.getOptionValue(MACHINES_DESCRIPTION));
		// if ((workload.peek() == null && jobScheduler.isFinished()) ||
		// availability.peek() == null) {
		// availability.stop();
		// workload.stop();
		// queue.clear();
		// System.out.println("--------------------------------------------------------------");
		// }
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

	private static JobSchedulerPolicy defineScheduler(CommandLine cmd, ArrayList<Peer> peers) throws FileNotFoundException, java.text.ParseException {
		JobSchedulerPolicy jobScheduler = null;
		if (cmd.hasOption(SPOT_INSTANCES)) {
			// SpotPrice initialSpotPrice = new SpotPrice("", new Date(), 0.1);
			SpotPrice initialSpotPrice = SpotInstaceTraceFormat.extractFirstSpotPrice(cmd.getOptionValue(AVAILABILITY));
			long machineSpeed = Long.parseLong(cmd.getOptionValue(MACHINES_DESCRIPTION));
			jobScheduler = new SpotInstancesScheduler(peers.get(0), initialSpotPrice, machineSpeed);
			SpotPriceEventDispatcher.getInstance().addListener((SpotInstancesScheduler) jobScheduler);
		} else {
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
		}
		return jobScheduler;
	}

	private static Options prepareOptions() {
		Options options = new Options();

		options.addOption(AVAILABILITY, "availability", true, "Arquivo com a caracterização da disponibilidade para todos os recursos.");
		options.addOption(WORKLOAD, "workload", true, "Arquivo com o workload no format GWA (Grid Workload Archive).");
		options.addOption(MACHINES_DESCRIPTION, "machinesdescription", true, "Arquivo com a descrição das máquinas presentes em cada peer.");
		options.addOption(SCHEDULER, "scheduler", true, "Indica qual scheduler deverá ser usado.");
		options.addOption(REPLIES, "replies", true, "O número de réplicas para cada task.");
		options.addOption(OUTPUT, "output", true, "O nome do arquivo em que o output da simulação será gravado.");
		options.addOption(NUM_RESOURCES_BY_PEER, "nresources", true, "O número de réplicas para cada task.");
		options.addOption(NUM_PEERS, "npeers", true, "O número de peers do grid.");
		options.addOption(PEERS_DESCRIPTION, "peers_description", true, "Arquivo descrevendo os peers.");
		options.addOption(NODE_MIPS_RATING, "speed", true, "A velocidade de cada máquina.");
		options.addOption(NOF, "nof", false, "Utiliza a Rede de Favores (NoF).");
		options.addOption(SPOT_INSTANCES, "spot_instances", false, "Simular modelo amazon spot instances.");
		options.addOption(BID_VALUE, "bid_value", true, "Valor do bid para alocação de instâncias no modelo amazon spot instances..");
		options.addOption(DEDICATED_RESOURCES, "dedicated", false, "Indica que os recursos são todos dedicados.");
		options.addOption(VERBOSE, "verbose", false, "Informa todos os eventos importantes.");
		options.addOption(SYNTHETIC_AVAILABILITY, "synthetic_availability", false, "Indica que a disponibilidade dos recursos deve ser gerada sinteticamente.");
		options.addOption(HELP, false, "Comando de ajuda.");
		options.addOption(USAGE, false, "Instruções de uso.");
		return options;
	}

	private static boolean hasOptions(CommandLine cmd, String... options) {
		for (String option : options) {
			if (!cmd.hasOption(option)) {
				return false;
			}
		}
		return true;
	}

	private static CommandLine parseCommandLine(String[] args, Options options) {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			showMessageAndExit(e);
		}
		return cmd;
	}

	private static void showMessageAndExit(Exception e) {
		System.err.println(e.getMessage());
		System.exit(1);
	}

}
