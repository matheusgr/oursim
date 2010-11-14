package br.edu.ufcg.lsd.oursim.io.output;

import java.io.BufferedWriter;
import java.io.IOException;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventListenerAdapter;

public class WorkerEventsPrintOutput extends WorkerEventListenerAdapter {

	@Override
	public void workerAvailable(Event<String> workerEvent) {

		try {
			if (bw != null) {
				bw.append(workerEvent.getTime() + ":AV:" + workerEvent.getSource()).append("\n");
			} else {
				System.out.println(workerEvent.getTime() + " : AV : " + workerEvent.getSource());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
		try {
			if (bw != null) {
				bw.append(workerEvent.getTime() + ":NA:" + workerEvent.getSource()).append("\n");
			} else {
				System.out.println(workerEvent.getTime() + " : NA : " + workerEvent.getSource());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedWriter bw = null;

	public void setBuffer(BufferedWriter utilizationBuffer) {
		this.bw = utilizationBuffer;
		try {
			this.bw.append("time:event:machine\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
