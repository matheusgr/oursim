package br.edu.ufcg.lsd.oursim.ui;

import java.text.DecimalFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

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

	public static CommandLine parseCommandLine(String[] args, Options options) {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			showMessageAndExit(e);
		}
		return cmd;
	}

	public static void showMessageAndExit(Exception e) {
		showMessageAndExit(e.getMessage());
	}

	public static void showMessageAndExit(String message) {
		System.err.println(message);
		System.exit(1);
	}

	public static void printOutput(ComputingElementEventCounter computingElementEventCounter) {

		DecimalFormat dft = new DecimalFormat("000.00");

		// System.out.println("submitted finished preempted notStarted
		// success finishedCost preemptedCost totalCost");
		System.out.print(computingElementEventCounter.getNumberOfSubmittedJobs());
		System.out.print(" ");
		System.out.print(computingElementEventCounter.getNumberOfFinishedJobs());
		System.out.print(" ");
		System.out.print(computingElementEventCounter.getNumberOfPreemptionsForAllJobs());
		System.out.print(" ");
		// submitted - (finished + preempted)
		int notStarted = computingElementEventCounter.getNumberOfSubmittedJobs()
				- (computingElementEventCounter.getNumberOfFinishedJobs() + computingElementEventCounter.getNumberOfPreemptionsForAllJobs());
		System.out.print(notStarted);
		System.out.print(" ");
		System.out.print(dft.format(computingElementEventCounter.getNumberOfFinishedJobs() / (computingElementEventCounter.getNumberOfSubmittedJobs() * 1.0))
				.replace(",", "."));
		System.out.print(" ");
		System.out.print(dft.format(computingElementEventCounter.getTotalCostOfAllFinishedJobs()).replace(",", "."));
		System.out.print(" ");
		System.out.print(dft.format(computingElementEventCounter.getTotalCostOfAllPreemptedJobs()).replace(",", "."));
		System.out.print(" ");
		System.out
				.print(dft.format(computingElementEventCounter.getTotalCostOfAllFinishedJobs() + computingElementEventCounter.getTotalCostOfAllPreemptedJobs())
						.replace(",", "."));
		System.out.print("\n");
	}

	public static String printResume(ComputingElementEventCounter computingElementEventCounter) {

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
		resume += "# Total of                    events: " + EventQueue.totalNumberOfEvents;
		return resume;
	}

	public static ComputingElementEventCounter prepareOutputAccounting(CommandLine cmd, String VERBOSE) {
		ComputingElementEventCounter computingElementEventCounter = new ComputingElementEventCounter();

		if (cmd.hasOption(VERBOSE)) {
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

}
