package br.edu.ufcg.lsd.oursim.simulationevents.spotinstances;

import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPrice;
import br.edu.ufcg.lsd.oursim.simulationevents.TimedEventAbstract;

/**
 * 
 * The root class of all spot price's related events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 28/07/2010
 * 
 */
public abstract class SpotPriceTimedEvent extends TimedEventAbstract<SpotPrice> {

	public SpotPriceTimedEvent(long time, int priority, SpotPrice spotPrice) {
		super(time, priority, spotPrice);
	}

}
