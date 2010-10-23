package br.edu.ufcg.lsd.spotinstancessimulator.ui;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		args = "-spot -l 20 -bid max -w spot_workload_0.txt -av resources/eu-west-1.linux.m1.small.csv -o spot-trace-20_7_dias_10_sites.txt".split("\\s+");
		SpotCLI.main(args);

	}

}
