package br.edu.ufcg.lsd.oursim.io.output;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventListenerAdapter;

public class WorkerEventsPrintOutput extends WorkerEventListenerAdapter {

	@Override
	public void workerAvailable(Event<String> workerEvent) {
		System.out.println(workerEvent.getTime() + " : AV : " + workerEvent.getSource());
	}

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
		System.out.println(workerEvent.getTime() + " : NA : " + workerEvent.getSource());
	}

}
