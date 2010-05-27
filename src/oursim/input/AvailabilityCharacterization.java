package oursim.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
import java.util.Scanner;

import oursim.availability.AvailabilityRecord;

public class AvailabilityCharacterization extends InputAbstract<AvailabilityRecord> {

	public AvailabilityCharacterization(String fileName) throws FileNotFoundException {
		this.inputs = new PriorityQueue<AvailabilityRecord>();
		Scanner sc = new Scanner(new File(fileName));
		sc.nextLine();// TODO desconsidera a primeira linha (cabeçalho)
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			String machineName = scLine.next();
			long timestamp = scLine.nextLong();
			long duration = scLine.nextLong();
			if (duration > 0) {
				this.inputs.add(new AvailabilityRecord(machineName, timestamp, duration));
			}
		}
	}

}
