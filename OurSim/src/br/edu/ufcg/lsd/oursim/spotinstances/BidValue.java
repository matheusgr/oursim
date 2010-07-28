package br.edu.ufcg.lsd.oursim.spotinstances;

public class BidValue implements Comparable<BidValue> {

	private String instance;

	private long time;

	private double value;

	public BidValue(String instance, long time, double value) {
		this.instance = instance;
		this.time = time;
		this.value = value;
	}

	public long getTime() {
		return time;
	}

	public double getValue() {
		return value;
	}

	public String getInstance() {
		return instance;
	}

	@Override
	public int compareTo(BidValue o) {
		return (int) (this.time - o.time);
	}

}
