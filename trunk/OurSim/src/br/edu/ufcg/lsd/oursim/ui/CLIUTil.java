package br.edu.ufcg.lsd.oursim.ui;

import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.loadEC2InstancesTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances.SpotPriceEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.spotinstances.EC2Instance;
import br.edu.ufcg.lsd.oursim.entities.spotinstances.EC2InstanceBadge;
import br.edu.ufcg.lsd.oursim.entities.spotinstances.parser.Ec2InstanceParser;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPrice;
import br.edu.ufcg.lsd.oursim.io.input.workload.OnDemandGWANorduGridWorkloadWithBidValue;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.output.ComputingElementEventCounter;
import br.edu.ufcg.lsd.oursim.io.output.JobPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.TaskPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.WorkerEventsPrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.policy.SpotInstancesScheduler;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;
import br.edu.ufcg.lsd.oursim.util.SpotInstaceTraceFormat;

public class CLIUTil {

	static boolean hasOptions(CommandLine cmd, String... options) {
		for (String option : options) {
			if (!cmd.hasOption(option)) {
				return false;
			}
		}
		return true;
	}

	static CommandLine parseCommandLine(String[] args, Options options) {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			showMessageAndExit(e);
		}
		return cmd;
	}

	static void showMessageAndExit(Exception e) {
		showMessageAndExit(e.getMessage());
	}

	static void showMessageAndExit(String message) {
		System.err.println(message);
		System.exit(1);
	}

	static void printOutput(ComputingElementEventCounter computingElementEventCounter) {

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

	static String printResume(ComputingElementEventCounter computingElementEventCounter) {

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

	static Workload defineWorkloadToSpotInstances(CommandLine cmd, String workloadFilePath, Workload workload, Map<String, Peer> peersMap,
			String spotTraceFilePath, String BID_VALUE) throws IOException, java.text.ParseException, FileNotFoundException {
		if (hasOptions(cmd, BID_VALUE)) {
			long timeOfFirstSubmission = GWAFormat.extractSubmissionTimeFromFirstJob(workloadFilePath);
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
			workload = new OnDemandGWANorduGridWorkloadWithBidValue(workloadFilePath, peersMap, timeOfFirstSubmission, bidValue);
		} else {
			System.err.println("Combinação de parâmetros de spot-instances inválida.");
		}
		return workload;
	}

	static ComputingElementEventCounter prepareOutputAccounting(CommandLine cmd, String VERBOSE) {
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

	static void treatWrongCommand(Options options, CommandLine cmd, String HELP, String USAGE, String EXECUTION_LINE) {
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
		jobScheduler = new SpotInstancesScheduler(spotInstancesPeer, initialSpotPrice, ec2Instance.speed);
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

}
