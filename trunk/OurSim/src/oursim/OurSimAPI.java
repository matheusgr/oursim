package oursim;

import java.util.List;

import oursim.availability.AvailabilityRecord;
import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.dispatchableevents.workerevents.WorkerEvent;
import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import oursim.dispatchableevents.workerevents.WorkerEventFilter;
import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.input.Input;
import oursim.policy.JobSchedulerPolicy;
import oursim.policy.OurGridScheduler;
import oursim.simulationevents.EventQueue;

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

		prepareListeners(peers, workload, availability, eq, sp);

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

		clearListeners(peers, sp);

	}

	private void prepareListeners(List<Peer> peers, Input<Job> workload, Input<AvailabilityRecord> availability, EventQueue eq, JobSchedulerPolicy sp) {
		JobEventDispatcher.getInstance().addListener(sp);
		TaskEventDispatcher.getInstance().addListener(sp);

		for (final Peer peer : peers) {
			WorkerEventDispatcher.getInstance().addListener(peer, new WorkerEventFilter() {

				@Override
				public boolean accept(WorkerEvent workerEvent) {
					String machineName = (String) workerEvent.getSource();
					return peer.hasResource(machineName);
				}

			});
			scheduleJobEvents(eq, peer.getWorkload());
		}

		WorkerEventDispatcher.getInstance().addListener(sp);

	}

	private void clearListeners(List<Peer> peers, JobSchedulerPolicy sp) {
		JobEventDispatcher.getInstance().removeListener(sp);
		TaskEventDispatcher.getInstance().removeListener(sp);

		for (Peer peer : peers) {
			WorkerEventDispatcher.getInstance().removeListener(peer);
		}

		WorkerEventDispatcher.getInstance().removeListener(sp);
	}

}
