package oursim;

import java.util.LinkedList;
import java.util.List;

import oursim.availability.AvailabilityRecord;
import oursim.dispatchableevents.Event;
import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import oursim.dispatchableevents.workerevents.WorkerEventFilter;
import oursim.entities.Job;
import oursim.entities.Machine;
import oursim.entities.Peer;
import oursim.input.Input;
import oursim.input.InputAbstract;
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

	public void run(List<Peer> peers, Workload workload) {
		Input<AvailabilityRecord> defaultResourceAvailability = generateDefaultResourceAvailability(peers);
		run(peers, workload, defaultResourceAvailability);
	}

	public void run(List<Peer> peers, Workload workload, Input<AvailabilityRecord> availability) {

		JobSchedulerPolicy jobScheduler = new OurGridScheduler(eventQueue, peers, workload);

		prepareListeners(peers, workload, availability, eventQueue, jobScheduler);

		// setUP the peers to the simulation
		for (Peer peer : peers) {
			// share the eventQueue with the peers.
			peer.setEventQueue(eventQueue);
			// adds the workload of all peers to the jobScheduler
			if (peer.getWorkload() != null) {
				jobScheduler.addWorkload(peer.getWorkload());
			}
		}

		scheduleWorkerEvents(eventQueue, availability);

		run(eventQueue, jobScheduler);

		eventQueue.close();

		clearListeners(peers, jobScheduler);

	}

	private static void run(EventQueue queue, JobSchedulerPolicy jobScheduler) {
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

		}
	}

	private static void scheduleWorkerEvents(EventQueue eq, Input<AvailabilityRecord> availability) {

		while (availability.peek() != null) {
			AvailabilityRecord av = availability.poll();
			eq.addWorkerAvailableEvent(av.getTime(), av.getMachineName(), av.getDuration());
		}

	}

	private static void prepareListeners(List<Peer> peers, Input<Job> workload, Input<AvailabilityRecord> availability, EventQueue eq, JobSchedulerPolicy sp) {
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

	private static Input<AvailabilityRecord> generateDefaultResourceAvailability(final List<Peer> peers) {

		Input<AvailabilityRecord> availability = new InputAbstract<AvailabilityRecord>() {
			@Override
			protected void setUp() {
				this.inputs = new LinkedList<AvailabilityRecord>();
				long timestamp = 0;
				long duration = Long.MAX_VALUE;
				for (Peer peer : peers) {
					for (Machine machine : peer.getResources()) {
						this.inputs.add(new AvailabilityRecord(machine.getName(), timestamp, duration));
					}
				}

			}
		};
		return availability;
	}

}
