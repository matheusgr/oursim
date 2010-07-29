package br.edu.ufcg.lsd.oursim.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPrice;

public class SpotInstaceTraceFormat {

	// 2009-11-30 19:33:57,0.039
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static SpotPrice createSpotPriceFromSpotTraceRecord(String nextLine, long startingTime) throws ParseException {
		Scanner scLine = new Scanner(nextLine);
		scLine.useDelimiter(",");
		String time = scLine.next();
		Double price = Double.parseDouble(scLine.next());
		Date instant = formatter.parse(time);
		return new SpotPrice(null, instant, price, startingTime);
	}

	public static long extractTimeFromFirstAvailabilityRecord(String spotTraceFilePath) throws ParseException, FileNotFoundException {
		Scanner sc = new Scanner(new File(spotTraceFilePath));
		SpotPrice firestSpotPriceRecord = SpotInstaceTraceFormat.createSpotPriceFromSpotTraceRecord(sc.nextLine(), 0);
		long startingTime = firestSpotPriceRecord.getTime();
		return startingTime;
	}

}
