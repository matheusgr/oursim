package br.edu.ufcg.lsd.oursim.simulationevents.spotinstances;

import br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances.SpotPriceEventDispatcher;
import br.edu.ufcg.lsd.oursim.spotinstances.SpotPrice;

public class NewSpotPriceEvent extends SpotPriceTimedEvent {

	public static final int PRIORITY = 1;

	public NewSpotPriceEvent(SpotPrice spotPrice) {
		super(spotPrice.getSimulationTime(), PRIORITY, spotPrice);
	}

	@Override
	protected void doAction() {
		SpotPrice spotPrice = (SpotPrice) source;
		SpotPriceEventDispatcher.getInstance().dispatchNewSpotPrice(spotPrice);
	}

}
