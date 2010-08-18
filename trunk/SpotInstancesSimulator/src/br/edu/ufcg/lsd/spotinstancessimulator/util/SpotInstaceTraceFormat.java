package br.edu.ufcg.lsd.spotinstancessimulator.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPrice;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPriceFluctuation;

public class SpotInstaceTraceFormat {

	// 2009-11-30 19:33:57,0.039
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static SpotPrice createSpotPriceFromSpotTraceRecord(String nextLine, long startingTime) throws ParseException {
		Scanner scLine = new Scanner(nextLine);
		scLine.useDelimiter(",");
		String time = scLine.next();
		Double price = Double.parseDouble(scLine.next());
		Date instant = formatter.parse(time);
		return new SpotPrice("nome da máquina", instant, price, startingTime);
	}

	public static long extractTimeFromFirstSpotPrice(String spotTraceFilePath) throws ParseException, FileNotFoundException {
		return extractFirstSpotPrice(spotTraceFilePath).getTime();
	}

	public static SpotPrice extractFirstSpotPrice(String spotTraceFilePath) throws ParseException, FileNotFoundException {
		Scanner sc = new Scanner(new File(spotTraceFilePath));
		SpotPrice firestSpotPriceRecord = SpotInstaceTraceFormat.createSpotPriceFromSpotTraceRecord(sc.nextLine(), 0);
		return firestSpotPriceRecord;
	}

	public static SpotPrice extractHighestSpotPrice(String spotTraceFilePath) throws ParseException, FileNotFoundException {
		SpotPriceFluctuation spotTrace = new SpotPriceFluctuation(spotTraceFilePath);
		SpotPrice highestSpotPrice = new SpotPrice("", 0, Double.MIN_VALUE, 0);
		while (spotTrace.peek() != null) {
			SpotPrice polledSpotPrice = spotTrace.poll();
			if (polledSpotPrice.getPrice() > highestSpotPrice.getPrice()) {
				highestSpotPrice = polledSpotPrice;
			}
		}
		return highestSpotPrice;
	}

	public static SpotPrice extractlowestSpotPrice(String spotTraceFilePath) throws ParseException, FileNotFoundException {
		SpotPriceFluctuation spotTrace = new SpotPriceFluctuation(spotTraceFilePath);
		SpotPrice lowestSpotPrice = new SpotPrice("", 0, Double.MAX_VALUE, 0);
		while (spotTrace.peek() != null) {
			SpotPrice polledSpotPrice = spotTrace.poll();
			if (polledSpotPrice.getPrice() < lowestSpotPrice.getPrice()) {
				lowestSpotPrice = polledSpotPrice;
			}
		}
		return lowestSpotPrice;
	}

}
