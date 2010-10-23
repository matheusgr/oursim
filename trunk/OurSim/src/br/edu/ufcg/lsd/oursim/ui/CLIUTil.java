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

		return sb.toString();
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

	public static void main(String[] args) throws IOException {
		String setUp = "cd /tmp && mkdir -p playpen/oursim && scp cororoca:~/workspace/OurSim/dist/oursim.zip . && unzip -o oursim.zip -d playpen/oursim && scp cororoca:~/workspace/SpotInstancesSimulator/dist/spotsim.zip . && unzip -o spotsim.zip -d playpen/oursim && cd playpen/oursim;";
		String tearDown = "";
		String cmd = "";
		cmd += setUp;
		String java = " $JAVACALL ";
		cmd += "JAVACALL='java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar'";
		cmd += ";SPT=resources/eu-west-1.linux.m1.small.csv";
		// int[] nSitesV = new int[] { 10, 100, 1000, 10000 };
		int[] nSitesV = new int[] { 50 };
		for (int nSites : nSitesV) {
			int spotLimit = 100;
			cmd += ";ISD=resources/iosup_site_description_" + nSites + "_sites.txt;";
			String spt = " $SPT ";
			String isd = " $ISD ";
			String sep = "";
			int nRes = 25;
			cmd += sep + java + "oursim.jar -w resources/iosup_workload_7_dias_" + nSites + "_sites.txt -s persistent -pd " + isd + " -wt iosup -nr " + nRes
					+ " -synthetic_av 2678400 -o oursim-trace-" + nRes + "_7_dias_" + nSites + "_sites.txt";
			sep = " && ";
			cmd += sep + "sort -g oursim-trace-" + nRes + "_7_dias_" + nSites + "_sites.txt_spot_workload.txt > " + "oursim-trace-" + nRes + "_7_dias_"
					+ nSites + "_sites.txt_spot_workload_sorted.txt ";
			sep = " && ";
			cmd += sep + java + "spotsim.jar -spot -l " + spotLimit + " -bid max -w " + "oursim-trace-" + nRes + "_7_dias_" + nSites
					+ "_sites.txt_spot_workload_sorted.txt -av " + spt + " -o spot-trace-" + nRes + "_7_dias_" + nSites + "_sites_" + spotLimit
					+ "_spotLimit.txt";
			sep = " && ";

			StringBuilder sb = new StringBuilder("#site	num_cpus\n");
			for (int i = 1; i <= nSites; i++) {
				sb.append(i).append(" ").append(nRes).append("\n");
			}
			FileUtils.writeStringToFile(new File("resources/iosup_site_description_" + nSites + "_sites.txt"), sb.toString());

			tearDown = " && scp oursim-trace-25_7_dias_" + nSites + "_sites.txt oursim-trace-25_7_dias_" + nSites
					+ "_sites.txt_spot_workload_sorted.txt spot-trace-25_7_dias_" + nSites
					+ "_sites_100_spotLimit.txt cororoca:/local/edigley/traces/oursim/trace_media_15s_100_peers";
		}

		cmd += tearDown;

		FileUtils.writeStringToFile(new File("cmd.txt"), cmd);

		System.out.println(cmd);
	}

}
