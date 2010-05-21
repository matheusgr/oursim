package oursim.policy;

import java.util.Iterator;
import java.util.List;

import oursim.dispatchableevents.Event;
import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.input.Workload;
import oursim.simulationevents.EventQueue;

/**
 * 
 * An reference implementation of a {@link JobSchedulerPolicy}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class OurGridScheduler extends JobSchedulerPolicyAbstract {

	/**
	 * An ordinary constructor.
	 * 
	 * @param eventQueue
	 *            The queue with the events to be processed.
	 * @param peers
	 *            All the peers that compound of the grid.
	 */
	public OurGridScheduler(EventQueue eventQueue, List<Peer> peers, Workload workload) {
		super(eventQueue, peers, workload);
	}

	@Override
	protected void performScheduling() {
		for (Iterator<Task> iterator = submittedTasks.iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			task.getSourcePeer().prioritizePeersToConsume(peers);
			for (Peer provider : peers) {
				boolean isTaskRunning = provider.executeTask(task);
				if (isTaskRunning) {
					eventQueue.addStartedTaskEvent(task);
					iterator.remove();
					break;
				}
			}
		}
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		this.submittedTasks.add(task);
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		task.getTargetPeer().finishTask(task);
		if (task.getSourceJob().isFinished()) {
			eventQueue.addFinishJobEvent(eventQueue.getCurrentTime(), task.getSourceJob());
		}
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		this.rescheduleTask(task);
	}

	@Override
	public void workerAvailable(Event<String> workerEvent) {
		this.schedule();
	}

}
