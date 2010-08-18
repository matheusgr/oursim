package br.edu.ufcg.lsd.spotinstancessimulator.simulationevents;

import java.util.Date;

import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntity;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityAbstract;
import br.edu.ufcg.lsd.spotinstancessimulator.entities.BidValue;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPrice;

/**
 * 
 * A default, convenient implementation of an {@link ActiveEntity}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 01/06/2010
 * 
 */
public class SpotInstancesActiveEntity extends ActiveEntityAbstract {

	public void addNewSpotPriceEvent(SpotPrice spotPrice) {
		this.getEventQueue().addEvent(new NewSpotPriceEvent(spotPrice));
	}

	public void addFullHourCompletedEvent(BidValue bidValue) {
		long oneHourFromNow = getCurrentTime() + (60 * 60);
		this.getEventQueue().addEvent(new FullHourCompletedEvent(oneHourFromNow, bidValue));
	}

	@Override
	public void addAvailabilityRecordEvent(long time, AvailabilityRecord avRecord) {
		if (avRecord instanceof SpotPrice) {
			this.getEventQueue().addEvent(new NewSpotPriceEvent((SpotPrice) avRecord));
		} else {
			this.addWorkerAvailableEvent(time, avRecord.getMachineName(), avRecord.getDuration());
		}
	}

	public static void main(String[] args) {
		AvailabilityRecord av = new AvailabilityRecord("", 1324l, 25);
		SpotPrice sp = new SpotPrice("", new Date(), 123d); 
		
		System.out.println(sp instanceof AvailabilityRecord);
		System.out.println(AvailabilityRecord.class.isInstance(sp));
		
		System.out.println(av.getClass().getCanonicalName().equals(AvailabilityRecord.class.getCanonicalName()));
		System.out.println(av.getClass() == AvailabilityRecord.class);
		
		System.out.println(sp.getClass().getCanonicalName().equals(AvailabilityRecord.class.getCanonicalName()));
		System.out.println(sp.getClass() == AvailabilityRecord.class);
		
		System.out.println(sp.getClass().getCanonicalName().equals(SpotPrice.class.getCanonicalName()));
		System.out.println(sp.getClass() == SpotPrice.class);
	}

}
