package br.edu.ufcg.lsd.oursim.spotinstances;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;

public class SpotPrice extends AvailabilityRecord {

	private Date dateTime;

	private double price;

	public SpotPrice(String instanceType, Date dateTime, double price) {
		this(instanceType, dateTime, price, 0);
	}

	public SpotPrice(String instanceType, Date dateTime, double price, long simulationStartingTime) {
		this(instanceType, (dateTime.getTime() / 1000), price, simulationStartingTime);
		this.dateTime = dateTime;
	}

	public SpotPrice(String instanceType, long time, double price, long simulationStartingTime) {
		super(instanceType, time - simulationStartingTime, Long.MAX_VALUE);
		assert simulationStartingTime >= 0;
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("instanceType", getMachineName()).append("time", getTime()).append("price",
				price).append("dateTime", dateTime).toString();
	}

}
