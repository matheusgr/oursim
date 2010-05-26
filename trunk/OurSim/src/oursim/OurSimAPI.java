package oursim;

import java.util.List;

import oursim.availability.AvailabilityRecord;
import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import oursim.dispatchableevents.workerevents.WorkerEventFilter;
import oursim.entities.Peer;
import oursim.input.DedicatedResourcesAvailabilityCharacterization;
import oursim.input.Input;
import oursim.input.Workload;
import oursim.policy.JobSchedulerPolicy;
import oursim.policy.OurGridScheduler;
import oursim.simulationevents.EventQueue;
import oursim.simulationevents.TimedEvent;

public class OurSimAPI {

	private EventQueue eventQueue;

	public OurSimAPI(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	public void run(List<Peer> peers, Workload workload, JobSchedulerPolicy jobScheduler) {
		Input<AvailabilityRecord> defaultResourceAvailability = new DedicatedResourcesAvailabilityCharacterization(peers);
		run(peers, workload, defaultResourceAvailability, jobScheduler);
	}

	public void run(List<Peer> peers, Workload workload, Input<AvailabilityRecord> availability, JobSchedulerPolicy jobScheduler) {

		prepareListeners(peers, jobScheduler);

		// setUP the peers to the simulation
		for (Peer peer : peers) {
			// shpeersntQueue with the peers.
			peer.setEventQueue(eventQueue);
			// adds the workload of all peers to the jobScheduler
			if (peer.getWorkload() != null) {
				jobScheduler.addWorkload(peer.getWorkload());
			}
		}

		run(eventQueue, jobScheduler, availability);

		clearListeners(peers, jobScheduler);

	}

	private void run(EventQueue queue, JobSchedulerPolicy jobScheduler, Input<AvailabilityRecord> availability) {
		addFutureWorkerEventsToEventQueue(availability);
		while (queue.peek() != null) {

			long currentTime = queue.peek().getTime();

			// dispatch all the events in current time
			while (queue.peek() != null && queue.peek().getTime() == currentTime) {
				TimedEvent nextEventInCurrentTime = queue.poll();
				nextEventInCurrentTime.action();
			}

			// after the invocation of the actions of all events in current
			// time, the scheduler must be invoked
			jobScheduler.schedule();

			addFutureWorkerEventsToEventQueue(availability);

		}
	}

	void addFutureWorkerEventsToEventQueue(Input<AvailabilityRecord> availability) {
		long nextAvRecordTime = (availability.peek() != null) ? availability.peek().getTime() : -1;
		while (availability.peek() != null && availability.peek().getTime() == nextAvRecordTime) {
			AvailabilityRecord av = availability.poll();
			eventQueue.addWorkerAvailableEvent(av.getTime(), av.getMachineName(), av.getDuration());
		}
	}

	private static void prepareListeners(List<Peer> peers, JobSchedulerPolicy sp) {
		JobEventDispatcher.getInstance().addListener(sp);
		TaskEventDispatcher.getInstance().addListener(sp);

		// the peers must be the first added to the WorkerEventDispatcher
		for (final Peer peer : peers) {
			WorkerEventDispatcher.getInstance().addListener(peer, new WorkerEventFilter() {

				@Override
				public boolean accept(Event<String> workerEvent) {
					String machineName = (String) workerEvent.getSource();
					return peer.hasResource(machineName);
				}

			});
		}

		// the scheduler must be added to WorkerEventDispatcher always
		// after of all peers
		WorkerEventDispatcher.getInstance().addListener(sp);

	}

	private static void clearListeners(List<Peer> peers, JobSchedulerPolicy sp) {

		for (Peer peer : peers) {
			WorkerEventDispatcher.getInstance().removeListener(peer);
		}

		JobEventDispatcher.getInstance().removeListener(sp);
		TaskEventDispatcher.getInstance().removeListener(sp);
		WorkerEventDispatcher.getInstance().removeListener(sp);

	}

}
