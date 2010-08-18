package br.edu.ufcg.lsd.spotinstancessimulator.ui;

import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.hasOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.workload.OnDemandGWANorduGridWorkloadWithBidValue;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;
import br.edu.ufcg.lsd.spotinstancessimulator.dispatchableevents.spotinstances.SpotPriceEventDispatcher;
import br.edu.ufcg.lsd.spotinstancessimulator.entities.EC2Instance;
import br.edu.ufcg.lsd.spotinstancessimulator.entities.EC2InstanceBadge;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPrice;
import br.edu.ufcg.lsd.spotinstancessimulator.parser.Ec2InstanceParser;
import br.edu.ufcg.lsd.spotinstancessimulator.policy.SpotInstancesScheduler;
import br.edu.ufcg.lsd.spotinstancessimulator.util.SpotInstaceTraceFormat;

public class SpotCLIUTil {

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
