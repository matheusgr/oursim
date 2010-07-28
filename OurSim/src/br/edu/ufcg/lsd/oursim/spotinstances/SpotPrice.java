package br.edu.ufcg.lsd.oursim.spotinstances;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SpotPrice implements Comparable<SpotPrice> {

	private String instance;

	private Date time;

	private long simulationTime;

	private double price;

	public SpotPrice(String instance, Date time, double price) {
		this(instance, time, price, 0);
	}

	public SpotPrice(String instance, Date time, double price, long simulationStartingTime) {
		assert simulationStartingTime >= 0;
		this.instance = instance;
		this.time = time;
		this.price = price;
		this.simulationTime = (this.time.getTime() / 1000) - simulationStartingTime;
	}

	public Date getTime() {
		return time;
	}

	public double getPrice() {
		return price;
	}

	public String getInstance() {
		return instance;
	}

	public long getSimulationTime() {
		return this.simulationTime;
	}

	@Override
	public int compareTo(SpotPrice o) {
		return (int) (this.simulationTime - o.simulationTime);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("instance", instance).append("time", time).append("simulationTime",
				simulationTime).append("price", price).toString();
	}

}
