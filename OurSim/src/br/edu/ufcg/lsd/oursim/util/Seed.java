package br.edu.ufcg.lsd.oursim.util;

import java.util.Random;

public class Seed {

	// public static Random PeerRankingPolicyRANDOM = new Random(9354269l);
	private static Random PeerRankingPolicyRANDOM;

	// the first 3 values of the seed must all be less than m1 = 4294967087, and
	// not all 0; and the last 3 values must all be less than m2 = 4294944443,
	// and not all 0.
	// public static int[] OurGridAvailabilityCharacterizationSEED = new int[] { 1234, 13455, 5566, 6548, 8764, 5674 };
	private static int[] OurGridAvailabilityCharacterizationSEED;

	public Seed(Long PeerRankingPolicySEED, int[] OurGridAvailabilityCharacterizationSEED) {
		this.PeerRankingPolicyRANDOM = new Random(PeerRankingPolicySEED);
		this.OurGridAvailabilityCharacterizationSEED = OurGridAvailabilityCharacterizationSEED;
	}
	
	public static Random getPeerRankingPolicyRANDOM() {
		return PeerRankingPolicyRANDOM;
	}

	public static int[] getOurGridAvailabilityCharacterizationSEED() {
		return OurGridAvailabilityCharacterizationSEED;
	}

}
