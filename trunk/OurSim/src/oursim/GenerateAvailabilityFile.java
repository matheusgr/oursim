package oursim;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;

import oursim.availability.HostAvailabilityGenerator;
import oursim.availability.HostAvailabilityGeneratorImp;

public class GenerateAvailabilityFile {

	private static void generateAvailabilityFile(int quantMachines, int periodoDeObservacaoEmHoras, String outputFileName) throws java.io.IOException {

		StringBuilder sb = new StringBuilder();

		String[] header = "machine_name  time  duration  type  description".split("\\s+");

		for (String columnName : header) {
			sb.append(columnName).append(HostAvailabilityGenerator.TAB);
		}

		sb.append(HostAvailabilityGenerator.EOL);

		for (int i = 0; i < quantMachines; i++) {
			HostAvailabilityGenerator ma = new HostAvailabilityGeneratorImp("m_" + i, periodoDeObservacaoEmHoras, sb, false);
			ma.generateAvailabilityObservations();
		}

		FileUtils.writeStringToFile(new File(outputFileName), outputFileName, sb.toString());

	}

	private static void generateMachinesDescription(int quantMachines, String outputFileName) throws java.io.IOException {

		StringBuilder sb = new StringBuilder();

		String[] header = "//name memory storage bandwidth mem_provisioner bw_rovisioner allocation_policy processor_elements".split("\\s+");

		for (String columnName : header) {
			sb.append(columnName).append(HostAvailabilityGenerator.TAB);
		}

		sb.append(HostAvailabilityGenerator.EOL);

		String hostPattern = "\"%s\" 2048 1000000 100000000 simple simple space_shared [ 1000 ;  ]\n";

		for (int i = 0; i < quantMachines; i++) {
			sb.append(String.format(hostPattern, "m_" + i));
		}

		System.out.println(sb);

		FileUtils.writeStringToFile(new File(outputFileName), sb.toString());
	}

	public static void main(String[] args) throws IOException {
		StopWatch c = new StopWatch();
		c.start();

		int quantMachines = Parameters.NUM_PEERS * Parameters.NUM_RESOURCES_BY_PEER;
		int periodoDeObservacaoEmHoras = 10;
		String outputFileName = String.format("trace_mutka_%s-machines_%s-hours.txt", quantMachines, periodoDeObservacaoEmHoras);

		generateAvailabilityFile(quantMachines, periodoDeObservacaoEmHoras, outputFileName);

		generateMachinesDescription(quantMachines, "machinesDescription");

		c.stop();
		System.out.println(c);
	}

}
