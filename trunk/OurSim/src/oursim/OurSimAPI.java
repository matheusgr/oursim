package oursim;

import java.util.List;

import oursim.availability.AvailabilityRecord;
import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.EventQueue;
import oursim.input.Input;
import oursim.input.Workload;
import oursim.jobevents.JobEventDispatcher;
import oursim.jobevents.TaskEventDispatcher;
import oursim.policy.JobSchedulerPolicy;
import oursim.policy.OurGridScheduler;
import oursim.workerevents.WorkerEventDispatcher;

public class OurSimAPI {

	private static void scheduleEvents(EventQueue eq, Workload workload) {

		while (workload.peek() != null) {
			Job job = workload.poll();
			long time = job.getSubmissionTime();
			eq.addSubmitJobEvent(time, job);
		}

	}

	private static void scheduleEvents(EventQueue eq, Input<AvailabilityRecord> availability) {

		while (availability.peek() != null) {
			AvailabilityRecord av = availability.poll();
			eq.addWorkerAvailableEvent(av.machineName,av.time, av.duration);
		}		
		
	}
	
	public static void run(List<Peer> peers, Workload workload, Input<AvailabilityRecord> availability) {

		EventQueue eq = EventQueue.getInstance();
		JobSchedulerPolicy sp = new OurGridScheduler(eq, peers);

		JobEventDispatcher.getInstance().addListener(sp);
		TaskEventDispatcher.getInstance().addListener(sp);
		for (Peer peer : peers) {
			WorkerEventDispatcher.getInstance().addListener(peer);
		}
		
		scheduleEvents(eq, workload);
		scheduleEvents(eq, availability);

		while (eq.peek() != null) {
			long time = eq.peek().getTime();
			while (eq.peek() != null && eq.peek().getTime() == time) {
				eq.poll().action();
			}
			sp.scheduleJobs();
		}

		eq.close();

	}

}
