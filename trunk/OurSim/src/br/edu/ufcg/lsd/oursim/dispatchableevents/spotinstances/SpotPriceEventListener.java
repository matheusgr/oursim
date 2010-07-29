package br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListener;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotValue;

/**
 *
 *
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 28/07/2010
 *
 */
public interface SpotPriceEventListener extends EventListener {

	void newSpotPrice(Event<SpotValue> spotPriceEvent);

	void fullHourCompleted(Event<SpotValue> spotPriceEvent);

}
