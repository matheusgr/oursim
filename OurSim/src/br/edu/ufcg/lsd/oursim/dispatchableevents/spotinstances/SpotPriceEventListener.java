package br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListener;
import br.edu.ufcg.lsd.oursim.spotinstances.SpotPrice;

/**
 *
 *
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 28/07/2010
 *
 */
public interface SpotPriceEventListener extends EventListener {

	void newSpotPrice(Event<SpotPrice> spotPriceEvent);

}
