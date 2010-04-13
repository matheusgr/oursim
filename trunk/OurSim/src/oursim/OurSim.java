package oursim;

import oursim.entities.Job;
import oursim.events.SubmitJobEvent;
import oursim.events.TimeQueue;
import oursim.input.Workload;
import br.edu.ufcg.lsd.gridsim.GlobalScheduler;

public class OurSim {

    public static void run() {

	Workload workload = null;
	TimeQueue queue = null;
	
	while (workload.peek() != null || queue.peek() != null) {
	    if (workload.peek() != null) {
		Job job = workload.poll();
		long lastTime = job.getSubmissionTime();
		queue.addEvent(new SubmitJobEvent(lastTime, job));
	    }
	    if (queue.peek() != null) {
		long time = queue.peek().getTime();
		if (queue.peek().getClass().isInstance(SubmitJobEvent.class)) {
		    GlobalScheduler.getInstance().scheduleNow();
		} else {
		    while (queue.peek() != null && queue.peek().getTime() == time) {
			queue.poll().action();
		    }
		    GlobalScheduler.getInstance().scheduleNow();
		}

	    }
	}
	
    }

}
