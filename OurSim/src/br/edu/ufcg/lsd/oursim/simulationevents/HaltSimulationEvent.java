package br.edu.ufcg.lsd.oursim.simulationevents;

public class HaltSimulationEvent extends TimedEvent {

	public static final int PRIORITY = Integer.MIN_VALUE;

	public HaltSimulationEvent(long finishTime) {
		super(finishTime, PRIORITY);
	}

	@Override
	protected final void doAction() {
	}

}
