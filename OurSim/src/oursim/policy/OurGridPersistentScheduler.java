package oursim.policy;

import java.util.Iterator;
import java.util.List;

import oursim.dispatchableevents.Event;
import oursim.entities.Peer;
import oursim.entities.Task;

/**
 * 
 * An implementation of a {@link JobSchedulerPolicy} that persistently resubmits
 * the tasks that have been preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class OurGridPersistentScheduler extends JobSchedulerPolicyAbstract {

	/**
	 * An ordinary constructor.
	 * 
	 * @param peers
	 *            All the peers that compound of the grid.
	 */
	public OurGridPersistentScheduler(List<Peer> peers) {
		super(peers);
	}

	@Override
	protected void performScheduling() {
		for (Iterator<Task> iterator = submittedTasks.iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			task.getSourcePeer().prioritizePeersToConsume(peers);
			for (Peer provider : peers) {
				boolean isTaskRunning = provider.executeTask(task);
				if (isTaskRunning) {
					this.getEventQueue().addStartedTaskEvent(task);
					iterator.remove();
					break;
				}
			}
		}
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		this.rescheduleTask(task);
	}

}
