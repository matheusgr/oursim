package br.edu.ufcg.lsd.oursim.ui;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import br.edu.ufcg.lsd.oursim.io.output.ComputingElementEventCounter;
import br.edu.ufcg.lsd.oursim.io.output.JobPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.TaskPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.WorkerEventsPrintOutput;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

public class CLIUTil {

	public static boolean hasOptions(CommandLine cmd, String... options) {
		for (String option : options) {
			if (!cmd.hasOption(option)) {
				return false;
			}
		}
		return true;
	}

	public static CommandLine parseCommandLine(String[] args, Options options, String HELP, String USAGE, String EXECUTION_LINE) {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			showMessageAndExit(e);
		}

		checkForHelpAsk(options, cmd, HELP, USAGE, EXECUTION_LINE);

		return cmd;
	}

	public static void showMessageAndExit(Exception e) {
		showMessageAndExit(e.getMessage());
	}

	public static void showMessageAndExit(String message) {
		System.err.println(message);
		System.exit(1);
	}

	public static String getSummaryStatistics(ComputingElementEventCounter computingElementEventCounter, int nMachines, double utilization,
			double realUtilization, long simulationDuration) {

		DecimalFormat dft = new DecimalFormat("000.00");

		// submitted - (finished + preempted)
		int notStarted = computingElementEventCounter.getNumberOfSubmittedJobs()
				- (computingElementEventCounter.getNumberOfFinishedJobs() + computingElementEventCounter.getNumberOfPreemptionsForAllJobs());

		StringBuilder sb = new StringBuilder(
				"# submitted finished preempted notStarted success finishedCost preemptedCost totalCost nMachines utilization realUtilization simulationDuration"
						+ "\n");
		sb.append("# " + computingElementEventCounter.getNumberOfSubmittedJobs()).append(" ").append(computingElementEventCounter.getNumberOfFinishedJobs())
				.append(" ").append(computingElementEventCounter.getNumberOfPreemptionsForAllJobs()).append(" ").append(notStarted).append(" ").append(
						dft.format(computingElementEventCounter.getNumberOfFinishedJobs() / (computingElementEventCounter.getNumberOfSubmittedJobs() * 1.0))
								.replace(",", ".")).append(" ").append(
						dft.format(computingElementEventCounter.getTotalCostOfAllFinishedJobs()).replace(",", ".")).append(" ").append(
						dft.format(computingElementEventCounter.getTotalCostOfAllPreemptedJobs()).replace(",", ".")).append(" ").append(
						dft
								.format(
										computingElementEventCounter.getTotalCostOfAllFinishedJobs()
												+ computingElementEventCounter.getTotalCostOfAllPreemptedJobs()).replace(",", ".")).append(" ").append(
						nMachines).append(" ").append(utilization).append(" ").append(realUtilization).append(" ").append(simulationDuration);

		// System.out.println(sb.toString());

		return sb.toString();
		// System.out.println("submitted finished preempted notStarted success
		// finishedCost preemptedCost totalCost");
		// System.out.print(computingElementEventCounter.getNumberOfSubmittedJobs());
		// System.out.print(" ");
		// System.out.print(computingElementEventCounter.getNumberOfFinishedJobs());
		// System.out.print(" ");
		// System.out.print(computingElementEventCounter.getNumberOfPreemptionsForAllJobs());
		// System.out.print(" ");
		// // submitted - (finished + preempted)
		// int notStarted =
		// computingElementEventCounter.getNumberOfSubmittedJobs()
		// - (computingElementEventCounter.getNumberOfFinishedJobs() +
		// computingElementEventCounter.getNumberOfPreemptionsForAllJobs());
		// System.out.print(notStarted);
		// System.out.print(" ");
		// System.out.print(dft.format(computingElementEventCounter.getNumberOfFinishedJobs()
		// / (computingElementEventCounter.getNumberOfSubmittedJobs() * 1.0))
		// .replace(",", "."));
		// System.out.print(" ");
		// System.out.print(dft.format(computingElementEventCounter.getTotalCostOfAllFinishedJobs()).replace(",",
		// "."));
		// System.out.print(" ");
		// System.out.print(dft.format(computingElementEventCounter.getTotalCostOfAllPreemptedJobs()).replace(",",
		// "."));
		// System.out.print(" ");
		// System.out
		// .print(dft.format(computingElementEventCounter.getTotalCostOfAllFinishedJobs()
		// + computingElementEventCounter.getTotalCostOfAllPreemptedJobs())
		// .replace(",", "."));
		// System.out.print("\n");

	}

	public static String formatSummaryStatistics(ComputingElementEventCounter computingElementEventCounter, int nMachines, double utilization,
			double realUtilization, long simulationDuration) {

		DecimalFormat dft = new DecimalFormat("000.00");

		String resume = "";

		resume += "# Total of submitted            jobs: " + computingElementEventCounter.getNumberOfSubmittedJobs() + ".\n";
		resume += "# Total of finished             jobs: " + computingElementEventCounter.getNumberOfFinishedJobs() + ".\n";
		resume += "# Total of preemptions for all  jobs: " + computingElementEventCounter.getNumberOfPreemptionsForAllJobs() + ".\n";
		resume += "# Total of finished            tasks: " + computingElementEventCounter.getNumberOfFinishedTasks() + ".\n";
		resume += "# Total of preemptions for all tasks: " + computingElementEventCounter.getNumberOfPreemptionsForAllTasks() + ".\n";
		resume += "# Total cost of all finished    jobs: " + dft.format(computingElementEventCounter.getTotalCostOfAllFinishedJobs()) + ".\n";
		resume += "# Total cost of all preempted   jobs: " + dft.format(computingElementEventCounter.getTotalCostOfAllPreemptedJobs()) + ".\n";
		resume += "# Total cost of all             jobs: "
				+ dft.format(computingElementEventCounter.getTotalCostOfAllFinishedJobs() + computingElementEventCounter.getTotalCostOfAllPreemptedJobs())
				+ ".\n";
		resume += "# Total of                    events: " + EventQueue.totalNumberOfEvents + ".\n";
		resume += getSummaryStatistics(computingElementEventCounter, nMachines, utilization, realUtilization, simulationDuration);
		;
		return resume;
	}

	public static ComputingElementEventCounter prepareOutputAccounting(CommandLine cmd, boolean verbose) {
		ComputingElementEventCounter computingElementEventCounter = new ComputingElementEventCounter();

		if (verbose) {
			JobEventDispatcher.getInstance().addListener(new JobPrintOutput());
			TaskEventDispatcher.getInstance().addListener(new TaskPrintOutput());
			WorkerEventDispatcher.getInstance().addListener(new WorkerEventsPrintOutput());
			EventQueue.LOG = true;
			EventQueue.LOG_FILEPATH = "events_oursim.txt";
		}

		JobEventDispatcher.getInstance().addListener(computingElementEventCounter);
		TaskEventDispatcher.getInstance().addListener(computingElementEventCounter);

		return computingElementEventCounter;
	}

	public static void treatWrongCommand(Options options, CommandLine cmd, String HELP, String USAGE, String EXECUTION_LINE) {
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

	public static void checkForHelpAsk(Options options, CommandLine cmd, String HELP, String USAGE, String EXECUTION_LINE) {
		HelpFormatter formatter = new HelpFormatter();
		if (cmd.hasOption(HELP)) {
			formatter.printHelp(EXECUTION_LINE, options);
		} else if (cmd.hasOption(USAGE)) {
			formatter.printHelp(EXECUTION_LINE, options, true);
		} else {
			return;
		}
		System.exit(1);
	}

	// options.addOption(AVAILABILITY, "availability", true, "Arquivo com a
	// caracterização da disponibilidade para todos os recursos.");
	// options.addOption(WORKLOAD, "workload", true, "Arquivo com o workload no
	// format GWA (Grid Workload Archive).");
	// options.addOption(WORKLOAD_TYPE, "workload_type", true, "The type of
	// workload to read the workload file.");
	// options.addOption(MACHINES_DESCRIPTION, "machinesdescription", true,
	// "Arquivo com a descrição das máquinas presentes em cada peer.");
	// options.addOption(SCHEDULER, "scheduler", true, "Indica qual scheduler
	// deverá ser usado.");
	// options.addOption(REPLIES, "replies", true, "O número de réplicas para
	// cada task.");
	// options.addOption(OUTPUT, "output", true, "O nome do arquivo em que o
	// output da simulação será gravado.");
	// options.addOption(NUM_RESOURCES_BY_PEER, "nresources", true, "O número de
	// réplicas para cada task.");
	// options.addOption(NUM_PEERS, "npeers", true, "O número de peers do
	// grid.");
	// options.addOption(PEERS_DESCRIPTION, "peers_description", true, "Arquivo
	// descrevendo os peers.");
	// options.addOption(NODE_MIPS_RATING, "speed", true, "A velocidade de cada
	// máquina.");
	// options.addOption(NOF, "nof", false, "Utiliza a Rede de Favores (NoF).");
	// options.addOption(DEDICATED_RESOURCES, "dedicated", false, "Indica que os
	// recursos são todos dedicados.");
	// options.addOption(SYNTHETIC_AVAILABILITY, "synthetic_availability",
	// false, "Indica que a disponibilidade dos recursos deve ser gerada
	// sinteticamente.");

	// long durationOfWorkloadInSeconds =
	// cmd.getOptionValue(WORKLOAD_TYPE).equals("gwa") ?
	// GWAFormat.extractDurationInSecondsOfWorkload(cmd
	// .getOptionValue(WORKLOAD)) : Long.MAX_VALUE;
	// // adiciona um dia além da duração do workload
	// durationOfWorkloadInSeconds = TimeUtil.ONE_MONTH + TimeUtil.ONE_DAY;

	// long timeOfFirstSubmission =
	// cmd.getOptionValue(WORKLOAD_TYPE).equals("gwa") ? GWAFormat
	// .extractSubmissionTimeFromFirstJob(cmd.getOptionValue(WORKLOAD)) : 0;

	public static void main(String[] args) throws IOException {
		String java = " $JAVACALL ";
		int nSites = 10;
		String cmd = "JAVACALL='java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar';";
		cmd += "SPT=resources/eu-west-1.linux.m1.small.csv;";
		cmd += "ISD=resources/iosup_site_description.txt;";
		String spt = " $SPT ";
		String isd = " $ISD ";
		String sep = "";
		for (int nRes = 1; nRes < 21; nRes++) {
			cmd += sep + java + "oursim.jar -w resources/iosup_workload_7_dias_" + nSites
					+ "_sites.txt -s persistent -pd "+isd+" -wt iosup -nr " + nRes + " -synthetic_av 2678400 -o oursim-trace-"
					+ nRes + "_7_dias_" + nSites + "_sites.txt";
			sep = " && ";
			cmd += sep + "sort -g oursim-trace-" + nRes + "_7_dias_" + nSites + "_sites.txt_spot_workload.txt > " + "oursim-trace-" + nRes + "_7_dias_"
					+ nSites + "_sites.txt_spot_workload_sorted.txt ";
			sep = " && ";
			cmd += sep + java + "spotsim.jar -spot -l 20 -bid max -w " + "oursim-trace-" + nRes + "_7_dias_" + nSites
					+ "_sites.txt_spot_workload_sorted.txt -av " + spt + " -o spot-trace-" + nRes + "_7_dias_" + nSites + "_sites.txt";
			sep = " && ";
		}
		FileUtils.writeStringToFile(new File("cmd.txt"), cmd);
		System.out.println(cmd);
	}

}
