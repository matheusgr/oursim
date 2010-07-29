package br.edu.ufcg.lsd.oursim.simulationevents.spotinstances;

import br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances.SpotPriceEventDispatcher;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.BidValue;
import br.edu.ufcg.lsd.oursim.simulationevents.TimedEventAbstract;

public class FullHourCompletedEvent extends TimedEventAbstract<BidValue> {

	public static final int PRIORITY = 0;

	public FullHourCompletedEvent(long time, BidValue bidValue) {
		super(time, PRIORITY, bidValue);
	}

	@Override
	protected void doAction() {
		SpotPriceEventDispatcher.getInstance().dispatchFullHourCompleted(source);
	}

}
