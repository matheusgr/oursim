package br.edu.ufcg.lsd.oursim.policy;

import java.util.Iterator;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicyAbstract;

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
	 * @param peers
	 *            All the peers that compound of the grid.
	 */
	public OurGridScheduler(List<Peer> peers) {
		super(peers);
	}

	@Override
	public void schedule() {
		for (Iterator<Task> iterator = submittedTasks.iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			task.getSourcePeer().prioritizePeersToConsume(peers);
			for (Peer provider : peers) {
				boolean isTaskRunning = provider.executeTask(task);
				if (isTaskRunning) {
					this.addStartedTaskEvent(task);
					iterator.remove();
					break;
				}
			}
		}
	}

}
