package br.edu.ufcg.lsd.oursim.io.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.availability.AvailabilityRecord;

public class AvailabilityCharacterization extends InputAbstract<AvailabilityRecord> {

	private long startingTime;
	private boolean hasHeader;

	public AvailabilityCharacterization(String availabilityFilePath) throws FileNotFoundException {
		this(availabilityFilePath, 0, false);
	}

	public AvailabilityCharacterization(String availabilityFilePath, long startingTime, boolean hasHeader) throws FileNotFoundException {
		assert startingTime >= 0;
		this.inputs = new PriorityQueue<AvailabilityRecord>();
		this.startingTime = startingTime;
		this.hasHeader = hasHeader;
		Scanner sc = new Scanner(new File(availabilityFilePath));
		if (hasHeader) {
			sc.nextLine();// TODO desconsidera a primeira linha (cabeçalho)
		}
		long previousTime = -1;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			String machineName = scLine.next();
			long timestamp = scLine.nextLong() - startingTime;
			long duration = scLine.nextLong();
			assert timestamp >= previousTime;
			if (duration > 0) {
				this.inputs.add(new AvailabilityRecord(machineName, timestamp, duration));
			}
			previousTime = timestamp;
		}
	}

}
