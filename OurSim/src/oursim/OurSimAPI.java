package oursim;

import java.util.List;

import oursim.availability.AvailabilityRecord;
import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.events.EventQueue;
import oursim.events.FinishTaskEvent;
import oursim.events.TimedEvent;
import oursim.input.Input;
import oursim.jobevents.JobEventDispatcher;
import oursim.jobevents.TaskEventDispatcher;
import oursim.policy.JobSchedulerPolicy;
import oursim.policy.OurGridScheduler;
import oursim.workerevents.WorkerEventDispatcher;

public class OurSimAPI {

	public OurSimAPI() {
	}

	private static void scheduleJobEvents(EventQueue eq, Input<Job> workload) {

		while (workload.peek() != null) {
			Job job = workload.poll();
			long time = job.getSubmissionTime();
			eq.addSubmitJobEvent(time, job);
		}

	}

	private static void scheduleWorkerEvents(EventQueue eq, Input<AvailabilityRecord> availability) {

		while (availability.peek() != null) {
			AvailabilityRecord av = availability.poll();
			eq.addWorkerAvailableEvent(av.machineName, av.time, av.duration);
		}

	}

	public void run(List<Peer> peers, Input<Job> workload, Input<AvailabilityRecord> availability) {

		EventQueue eq = EventQueue.getInstance();
		JobSchedulerPolicy sp = new OurGridScheduler(eq, peers);

		JobEventDispatcher.getInstance().addListener(sp);
		TaskEventDispatcher.getInstance().addListener(sp);

		for (Peer peer : peers) {
			WorkerEventDispatcher.getInstance().addListener(peer);
		}

		WorkerEventDispatcher.getInstance().addListener(sp);

		scheduleJobEvents(eq, workload);
		scheduleWorkerEvents(eq, availability);

		while (eq.peek() != null) {
			long time = eq.peek().getTime();
			while (eq.peek() != null && eq.peek().getTime() == time) {
				eq.poll().action();

			}
			sp.scheduleTasks();
		}

		eq.close();

		JobEventDispatcher.getInstance().removeListener(sp);
		TaskEventDispatcher.getInstance().removeListener(sp);

		for (Peer peer : peers) {
			WorkerEventDispatcher.getInstance().removeListener(peer);
		}

		WorkerEventDispatcher.getInstance().removeListener(sp);

	}

}
