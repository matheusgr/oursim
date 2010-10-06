package br.edu.ufcg.lsd.oursim.simulationevents;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.FinishJobEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.PreemptedJobEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.StartedJobEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.SubmitJobEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.CancelledTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.FinishTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.PreemptedTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.StartedTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.SubmitTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.workerevents.WorkerAvailableEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.workerevents.WorkerUnavailableEvent;

/**
 * 
 * A default, convenient implementation of an {@link ActiveEntity}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 01/06/2010
 * 
 */
public class ActiveEntityImp implements ActiveEntity {

	/**
	 * The event queue that will be processed.
	 */
	private EventQueue eventQueue;

	@Override
	public final void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	@Override
	public EventQueue getEventQueue() {
		return eventQueue;
	}

	@Override
	public long getCurrentTime() {
		return eventQueue.getCurrentTime();
	}

	@Override
	public void addSubmitJobEvent(long submitTime, Job job) {
		assert submitTime >= getCurrentTime() : submitTime + " >= " + getCurrentTime();
		this.getEventQueue().addEvent(new SubmitJobEvent(submitTime, job));
	}

	@Override
	@Deprecated
	public void addStartedJobEvent(Job job) {
		this.getEventQueue().addEvent(new StartedJobEvent(job));
		this.addFinishJobEvent(job.getEstimatedFinishTime(), job);
	}

	@Override
	public void addPreemptedJobEvent(long preemptionTime, Job job) {
		this.getEventQueue().addEvent(new PreemptedJobEvent(preemptionTime, job));
	}

	@Override
	public void addFinishJobEvent(long finishTime, Job job) {
		assert finishTime >= this.getCurrentTime();
		FinishJobEvent finishJobEvent = new FinishJobEvent(finishTime, job);
		this.getEventQueue().addEvent(finishJobEvent);
	}

	@Override
	public void addSubmitTaskEvent(long submitTime, Task task) {
		this.getEventQueue().addEvent(new SubmitTaskEvent(submitTime, task));
	}

	@Override
	public void addStartedTaskEvent(Task task) {
		this.getEventQueue().addEvent(new StartedTaskEvent(task));
		this.addFinishTaskEvent(task.getEstimatedFinishTime(), task);
	}

	@Override
	public void addPreemptedTaskEvent(long preemptionTime, Task task) {
		this.getEventQueue().addEvent(new PreemptedTaskEvent(preemptionTime, task));
	}

	@Override
	public void addPreemptedTaskEvent(Task task) {
		addPreemptedTaskEvent(getCurrentTime(), task);
	}

	@Override
	public void addCancelledTaskEvent(long cancellingTime, Task task) {
		this.getEventQueue().addEvent(new CancelledTaskEvent(cancellingTime, task));
	}

	@Override
	public void addCancelledTaskEvent(Task task) {
		addCancelledTaskEvent(getCurrentTime(), task);
	}

	@Override
	public void addFinishTaskEvent(long finishTime, Task task) {
		assert finishTime > this.getCurrentTime();
		FinishTaskEvent finishTaskEvent = new FinishTaskEvent(finishTime, task);
		this.getEventQueue().addEvent(finishTaskEvent);
	}

	@Override
	public void addWorkerAvailableEvent(long time, String machineName, long duration) {
		assert duration > 0 && time >= 0;
		this.getEventQueue().addEvent(new WorkerAvailableEvent(time, machineName));
		this.getEventQueue().addEvent(new WorkerUnavailableEvent(time + duration, machineName));
	}

	@Override
	public void addAvailabilityRecordEvent(long time, AvailabilityRecord avRecord) {
		// XXX if (avRecord instanceof SpotPrice) {
		// this.getEventQueue().addEvent(new NewSpotPriceEvent((SpotPrice)
		// avRecord));
		// } else {
		// this.addWorkerAvailableEvent(time, avRecord.getMachineName(),
		// avRecord.getDuration());
		// }
		if (avRecord.getClass() == AvailabilityRecord.class) {
			this.addWorkerAvailableEvent(time, avRecord.getMachineName(), avRecord.getDuration());
		} else {
			System.out.println("(avRecord.getClass() == AvailabilityRecord.class) -----> " + (avRecord.getClass() == AvailabilityRecord.class));
		}
	}

}
