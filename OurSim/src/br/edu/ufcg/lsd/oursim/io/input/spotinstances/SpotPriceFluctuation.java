package br.edu.ufcg.lsd.oursim.io.input.spotinstances;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.PriorityQueue;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.io.input.InputAbstract;
import br.edu.ufcg.lsd.oursim.spotinstances.SpotPrice;
import br.edu.ufcg.lsd.oursim.util.SpotInstaceTraceFormat;

public class SpotPriceFluctuation extends InputAbstract<SpotPrice> {

	private long startingTime;
	private boolean hasHeader;

	public SpotPriceFluctuation(String spotPriceFluctuationFilePath, long startingTime) throws FileNotFoundException, ParseException {
		this(spotPriceFluctuationFilePath, startingTime, false);
	}

	public SpotPriceFluctuation(String spotPriceFluctuationFilePath) throws FileNotFoundException, ParseException {
		this(spotPriceFluctuationFilePath, 0, false);
	}

	public SpotPriceFluctuation(String availabilityFilePath, long startingTime, boolean hasHeader) throws FileNotFoundException, ParseException {
		assert startingTime >= 0;
		this.inputs = new PriorityQueue<SpotPrice>();
		this.startingTime = startingTime;
		this.hasHeader = hasHeader;
		Scanner sc = new Scanner(new File(availabilityFilePath));
		if (this.hasHeader) {
			sc.nextLine();// TODO desconsidera a primeira linha (cabeçalho)
		}
		long previousTime = -1;
		while (sc.hasNextLine()) {
			SpotPrice spotPriceRecord = SpotInstaceTraceFormat.createSpotPriceFromSpotTraceRecord(sc.nextLine(), this.startingTime);
			assert spotPriceRecord.getTime() >= previousTime;
			previousTime = spotPriceRecord.getTime();
			this.inputs.add(spotPriceRecord);
		}
	}

}
