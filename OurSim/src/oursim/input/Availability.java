package oursim.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import oursim.availability.AvailabilityRecord;

public class Availability implements Input<AvailabilityRecord> {

	private LinkedList<AvailabilityRecord> avRecords;

	public Availability(String fileName) throws FileNotFoundException {
		this.avRecords = new LinkedList<AvailabilityRecord>();
		Scanner sc = new Scanner(new File(fileName));
		sc.nextLine();// TODO desconsidera a primeira linha (cabeçalho)
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			String machineName = scLine.next();
			long timestamp = scLine.nextLong();
			long duration = scLine.nextLong();
			if (duration > 0) {
				this.avRecords.add(new AvailabilityRecord(machineName, timestamp, duration));
			}
		}
	}

	@Override
	public void close() {
	}

	@Override
	public AvailabilityRecord peek() {
		return avRecords.peekFirst();
	}

	@Override
	public AvailabilityRecord poll() {
		return avRecords.pollFirst();
	}

}
