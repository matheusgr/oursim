package oursim;

import java.util.List;

import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.events.EventQueue;
import oursim.input.Workload;
import oursim.policy.JobSchedulerPolicy;
import oursim.policy.OurGridScheduler;

public class OurSimAPI {

	private static void scheduleEvents(EventQueue eq, Workload workload,
			JobSchedulerPolicy sp) {

		while (workload.peek() != null) {
			Job job = workload.poll();
			long time = job.getSubmissionTime();
			eq.addSubmitJobEvent(time, job, sp);
		}

	}

	public static void run(List<Peer> peers, Workload workload) {
		EventQueue eq = new EventQueue();
		JobSchedulerPolicy sp = new OurGridScheduler(eq, peers);
		
		scheduleEvents(eq, workload, sp);
		
		while (eq.peek() != null) {
			long time = eq.peek().getTime();
			while (eq.peek() != null && eq.peek().getTime() == time) {
				eq.poll().action();
			}
			sp.scheduleJobs();
		}
	}

}
