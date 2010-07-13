package br.edu.ufcg.lsd.oursim.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;

public final class AvailabilityTraceFormat {

	public final static boolean validate() {
		return false;
	}

	public final static void addResourcesToPeer(Peer peer, String gridDescriptionFilePath) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(gridDescriptionFilePath));
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			String machineName = scLine.next();
			int clockRate = scLine.nextInt();
			String next = scLine.next();
			double nextDouble = Double.parseDouble(next);
			long mipsRating = Math.round(nextDouble);
			String machineFullName = machineName;
			peer.addMachine(new Machine(machineFullName, mipsRating));
		}

	}

}
