package br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventDispatcher;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPrice;

/**
 * 
 * A dispatcher to the spot price's related events.
 *
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 28/07/2010
 * 
 * @see {@link SpotPriceEventListener}
 * @see {@link SpotPriceEventFilter}
 *
 */
public class SpotPriceEventDispatcher extends EventDispatcher<SpotPrice, SpotPriceEventListener, SpotPriceEventFilter> {

	private static SpotPriceEventDispatcher instance = null;

	private SpotPriceEventDispatcher() {
		super();
	}

	public static SpotPriceEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new SpotPriceEventDispatcher();
	}

	@Override
	public void addListener(SpotPriceEventListener listener) {
		if (!this.getListeners().contains(listener)) {
			this.getListeners().add(listener);
			this.getListenerToFilter().put(listener, SpotPriceEventFilter.ACCEPT_ALL);
		} else {
			assert false;
		}
		assert this.getListenerToFilter().get(listener) != null;
	}

	@Override
	public boolean removeListener(SpotPriceEventListener listener) {
		return this.getListeners().remove(listener);
	}

	/**
	 * @see {@link SpotPriceEventListener#newSpotPrice(Event)
	 * @param machineName
	 * @param time
	 */
	public void dispatchNewSpotPrice(SpotPrice spotPrice) {
		dispatch(null, new Event<SpotPrice>(spotPrice));
	}

	@Override
	protected void dispatch(Enum type, Event<SpotPrice> spotPriceEvent) {
		for (SpotPriceEventListener listener : this.getListeners()) {
			if (this.getListenerToFilter().get(listener).accept(spotPriceEvent)) {
				listener.newSpotPrice(spotPriceEvent);
			}
		}
	}
}
