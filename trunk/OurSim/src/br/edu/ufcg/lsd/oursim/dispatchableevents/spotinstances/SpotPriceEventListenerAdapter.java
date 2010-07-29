package br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListenerAdapter;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPrice;

/**
 * 
 * A default (empty) implementation of the listener.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 28/07/2010
 * 
 */
public abstract class SpotPriceEventListenerAdapter implements SpotPriceEventListener, EventListenerAdapter {

	@Override
	public void newSpotPrice(Event<SpotPrice> spotPriceEvent) {
	}

}
