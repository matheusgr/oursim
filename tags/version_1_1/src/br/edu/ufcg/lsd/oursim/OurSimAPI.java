package br.edu.ufcg.lsd.oursim;

import java.util.List;

import br.edu.ufcg.lsd.oursim.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventFilter;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.input.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.input.Input;
import br.edu.ufcg.lsd.oursim.input.Workload;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.simulationevents.TimedEvent;


/**
 * 
 * The base class to a simulation. This is intended to be a seamless class, so
 * ui facilities should be implemented in client's classes.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 27/05/2010
 * 
 */
public class OurSimAPI {

	/**
	 * The event queue to drive the simulation.
	 */
	private EventQueue eventQueue;

	/**
	 * the peers that comprise the grid.
	 */
	private List<Peer> peers;

	/**
	 * the scheduler of the jobs.
	 */
	private JobSchedulerPolicy jobScheduler;

	/**
	 * the workload to be processed by the resources of the peers.
	 */
	private Workload workload;

	/**
	 * the characterization of the availability of all resources belonging to
	 * the peers.
	 */
	private Input<AvailabilityRecord> availabilityCharacterization;

	/**
	 * An convenient constructor to simulations that deals <b>only</b> with
	 * dedicated resources.
	 * 
	 * @param eventQueue
	 *            The event queue to drive the simulation.
	 * @param peers
	 *            the peers that comprise the grid.
	 * @param jobScheduler
	 *            the scheduler of the jobs.
	 * @param workload
	 *            the workload to be processed by the resources of the peers.
	 */
	public OurSimAPI(EventQueue eventQueue, List<Peer> peers, JobSchedulerPolicy jobScheduler, Workload workload) {
		this(eventQueue, peers, jobScheduler, workload, new DedicatedResourcesAvailabilityCharacterization(peers));
	}

	/**
	 * An ordinary constructor to simulations that deals with resources possibly
	 * volatile.
	 * 
	 * @param peers
	 *            the peers that comprise the grid.
	 * @param jobScheduler
	 *            the scheduler of the jobs.
	 * @param workload
	 *            the workload to be processed by the resources of the peers.
	 * @param availabilityCharacterization
	 *            the characterization of the availability of all resources
	 *            belonging to the peers.
	 */
	public OurSimAPI(EventQueue eventQueue, List<Peer> peers, JobSchedulerPolicy jobScheduler, Workload workload,
			Input<AvailabilityRecord> availabilityCharacterization) {
		this.eventQueue = eventQueue;
		this.peers = peers;
		this.jobScheduler = jobScheduler;
		this.workload = workload;
		this.availabilityCharacterization = availabilityCharacterization;
	}

	/**
	 * Starts the simulation.
	 */
	public void start() {
		prepareListeners(peers, jobScheduler);

		// shares the eventQueue with the scheduler
		this.jobScheduler.setEventQueue(eventQueue);

		// setUP the peers to the simulation
		for (Peer peer : peers) {
			// shares the eventQueue with the peers.
			peer.setEventQueue(eventQueue);
			// adds the workload of all peers to the jobScheduler
			if (peer.getWorkload() != null) {
				jobScheduler.addWorkload(peer.getWorkload());
			}
		}

		// adds the workload to the scheduler
		this.jobScheduler.addWorkload(workload);

		run(eventQueue, jobScheduler, availabilityCharacterization);

		clearListeners(peers, jobScheduler);
	}

	/**
	 * the method that effectively performs the simulation. This method contains
	 * the main loop guiding the whole simulation.
	 * 
	 * @param queue
	 *            The event queue to drive the simulation.
	 * @param jobScheduler
	 *            the scheduler of the jobs.
	 * @param availability
	 *            the characterization of the availability of all resources
	 *            belonging to the peers.
	 */
	private static void run(EventQueue queue, JobSchedulerPolicy jobScheduler, Input<AvailabilityRecord> availability) {
		addFutureWorkerEventsToEventQueue(queue, availability);
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

			addFutureWorkerEventsToEventQueue(queue, availability);

		}
	}

	private static void addFutureWorkerEventsToEventQueue(EventQueue eventQueue, Input<AvailabilityRecord> availability) {
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
		// after of all peers because of the preference at the process
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
