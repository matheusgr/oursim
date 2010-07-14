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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

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
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.availability.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.MarkovModelAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.workload.GWANorduGridWorkload;
import br.edu.ufcg.lsd.oursim.io.input.workload.OnDemandGWANorduGridWorkload;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.output.PrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.WorkerEventsPrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.policy.NoFSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.OurGridPersistentScheduler;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.util.AvailabilityTraceFormat;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;

public class CLI {

	public static final Random RANDOM = new Random(9354269l);

	private static final String NOF = "nof";

	private static final String AVAILABILITY = "av";

	private static final String SYNTHETIC_AVAILABILITY = "synthetic_av";

	private static final String MACHINES = "m";

	private static final String DEDICATED_RESOURCES = "d";

	private static final String WORKLOAD = "w";

	private static final String REPLIES = "r";

	private static final String NUM_PEERS = "np";

	private static final String NUM_RESOURCES_BY_PEER = "n";

	private static final String NODE_MIPS_RATING = "s";

	private static final String HELP = "help";

	private static final String USAGE = "usage";

	private static final String EXECUTION_LINE = "java -jar oursim.jar";

	private static final boolean USE_NOF = false;

	/**
	 * Exemplo:
	 * 
	 * <pre>
	 *   java -jar oursim.jar -w trace_filtrado1.txt -n 20 -d 
	 *   java -jar oursim.jar -w trace_filtrado1.txt -np 75 -m hostinfo_sdsc.dat -av disponibilidade.txt
	 * </pre>
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws IOException {

//		 args = "-w resources/trace_filtrado_primeiros_10000_jobs.txt -np 75 -m resources/hostinfo_sdsc.dat -av resources/disponibilidade.txt".split("\\s+");
		args = "-w resources/trace_filtrado_primeiros_10000_jobs.txt -np 75 -m resources/hostinfo_sdsc.dat -synthetic_av".split("\\s+");

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Options options = new Options();

		options.addOption(AVAILABILITY, "availability", true, "Arquivo com a caracterização da disponibilidade para todos os recursos.");
		options.addOption(WORKLOAD, "workload", true, "Arquivo com o workload no format GWA (Grid Workload Archive).");
		options.addOption(MACHINES, "machines", true, "Arquivo com a descrição das máquinas presentes em cada peer.");
		options.addOption(REPLIES, "replies", true, "O número de réplicas para cada task.");
		options.addOption(NUM_RESOURCES_BY_PEER, "nresources", true, "O número de réplicas para cada task.");
		options.addOption(NUM_PEERS, "npeers", true, "O número de peers do grid.");
		options.addOption(NODE_MIPS_RATING, "speed", true, "A velocidade de cada máquina.");
		options.addOption(NOF, "nof", false, "Utiliza a Rede de Favores (NoF).");
		options.addOption(DEDICATED_RESOURCES, "dedicated", false, "Indica que os recursos são todos dedicados.");
		options.addOption(SYNTHETIC_AVAILABILITY, "synthetic_availability", false, "Indica que a disponibilidade dos recursos deve ser gerada sinteticamente.");
		options.addOption(HELP, false, "Comando de ajuda.");
		options.addOption(USAGE, false, "Instruções de uso.");

		CommandLineParser parser = new PosixParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			showMessageAndExit(e);
		}

		JobEventDispatcher.getInstance().addListener(new PrintOutput("oursim_trace.txt"));
		JobEventDispatcher.getInstance().addListener(new PrintOutput());
		WorkerEventDispatcher.getInstance().addListener(new WorkerEventsPrintOutput());

		JobEventCounter jobEventCounter = new JobEventCounter();
		JobEventDispatcher.getInstance().addListener(jobEventCounter);

		TaskEventCounter taskEventCounter = new TaskEventCounter();
		TaskEventDispatcher.getInstance().addListener(taskEventCounter);

		OurSim oursim = null;
		Input<AvailabilityRecord> availability = null;
		Workload workload = null;
		JobSchedulerPolicy jobScheduler = null;
		Map<String, Peer> peersMap = null;

		ResourceSharingPolicy sharingPolicy = USE_NOF ? NoFSharingPolicy.getInstance() : FifoSharingPolicy.getInstance();

		if (cmd.hasOption(WORKLOAD) && cmd.hasOption(NUM_RESOURCES_BY_PEER)) {

			String workloadFilePath = cmd.getOptionValue(WORKLOAD);
			int numberOfResourcesByPeer = Integer.parseInt(cmd.getOptionValue(NUM_RESOURCES_BY_PEER));

			peersMap = GWAFormat.extractPeersFromGWAFile(workloadFilePath, numberOfResourcesByPeer, sharingPolicy);

			workload = new GWANorduGridWorkload(workloadFilePath, peersMap);

			availability = cmd.hasOption(DEDICATED_RESOURCES) ? new DedicatedResourcesAvailabilityCharacterization(peersMap.values())
					: new AvailabilityCharacterization(cmd.getOptionValue(AVAILABILITY));

		} else if (cmd.hasOption(WORKLOAD) && cmd.hasOption(MACHINES) && (cmd.hasOption(AVAILABILITY) || cmd.hasOption(SYNTHETIC_AVAILABILITY))
				&& cmd.hasOption(NUM_PEERS)) {

			String workloadFilePath = cmd.getOptionValue(WORKLOAD);
			String gridDescriptionFilePath = cmd.getOptionValue(MACHINES);
			String machineAvailabilityFilePath = cmd.getOptionValue(AVAILABILITY);
			int numberOfPeers = Integer.parseInt(cmd.getOptionValue(NUM_PEERS));

			peersMap = GWAFormat.extractPeersFromGWAFile(workloadFilePath, 0, sharingPolicy);

			AvailabilityTraceFormat.addResourcesToPeer(peersMap.values().iterator().next(), gridDescriptionFilePath);

			workload = new OnDemandGWANorduGridWorkload(workloadFilePath, peersMap, 1094090910);

			availability = cmd.hasOption(AVAILABILITY) ? new AvailabilityCharacterization(machineAvailabilityFilePath, 1062597562, true)
					: new MarkovModelAvailabilityCharacterization(peersMap, 100, 0);

		} else {
			if (cmd.hasOption(HELP)) {
				formatter.printHelp(EXECUTION_LINE, options);
			} else if (cmd.hasOption(USAGE)) {
				formatter.printHelp(EXECUTION_LINE, options, true);
			} else {
				formatter.printHelp(EXECUTION_LINE, options, true);
			}
			System.exit(1);
		}

		ArrayList<Peer> peers = new ArrayList<Peer>(peersMap.values());
		jobScheduler = new OurGridPersistentScheduler(peers);

		oursim = new OurSim(EventQueue.getInstance(), peers, jobScheduler, workload, availability);

		System.out.println("Starting Simulation...");

		oursim.start();

		System.out.println("Simulation ended.");

		System.out.println("# Total of finished             jobs: " + jobEventCounter.getNumberOfFinishedJobs());
		System.out.println("# Total of preemptions for all  jobs: " + jobEventCounter.getNumberOfPreemptionsForAllJobs());
		System.out.println("# Total of finished            tasks: " + taskEventCounter.getNumberOfFinishedTasks());
		System.out.println("# Total of preemptions for all tasks: " + taskEventCounter.getNumberOfPreemptionsForAllTasks());
		System.out.println("# Total of                    events: " + EventQueue.totalNumberOfEvents);

		stopWatch.stop();
		System.out.println("Simulation                  duration:" + stopWatch);
	}

	private static void showMessageAndExit(Exception e) {
		System.err.println(e.getMessage());
		System.exit(1);
	}

}
