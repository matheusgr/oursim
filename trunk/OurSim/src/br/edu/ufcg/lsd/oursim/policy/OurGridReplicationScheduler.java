package br.edu.ufcg.lsd.oursim.policy;

import java.util.Iterator;
import java.util.List;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * An implementation of a {@link JobSchedulerPolicy} that replies tasks
 * intending reduce the job's makespan.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class OurGridReplicationScheduler extends JobSchedulerPolicyAbstract {

	/**
	 * the level of replication of the tasks that comprise this job. A <i>value</i>
	 * less than or equal 1 means no replication. A <i>value</i> greater than 1
	 * means that <i>value</i> replies will be created for each task.
	 */
	private int replicationLevel;

	/**
	 * An ordinary constructor.
	 * 
	 * @param peers
	 *            All the peers that compound of the grid.
	 * @param replicationLevel
	 *            the level of replication of the tasks that comprise this job.
	 *            A <i>value</i> less than or equal 1 means no replication. A
	 *            <i>value</i> greater than 1 means that <i>value</i> replies
	 *            will be created for each task.
	 */
	public OurGridReplicationScheduler(List<Peer> peers, int replicationLevel) {
		super(peers);
		this.replicationLevel = replicationLevel;
	}

	@Override
	public final void schedule() {
		for (Iterator<Task> iterator = this.getSubmittedTasks().iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			task.getSourcePeer().prioritizePeersToConsume(this.getPeers());
			for (Peer provider : this.getPeers()) {
				boolean isTaskRunning = provider.executeTask(task);
				if (isTaskRunning) {
					this.addStartedTaskEvent(task);
					iterator.remove();
					break;
				}
			}
		}
	}

	@Override
	public final void addJob(Job job) {
		job.setReplicationLevel(this.replicationLevel);
		super.addJob(job);
	}

	@Override
	public final void taskSubmitted(Event<Task> taskEvent) {
		super.taskSubmitted(taskEvent);
		addReplies(taskEvent.getSource());
	}

	@Override
	public final void taskFinished(Event<Task> taskEvent) {
		super.taskFinished(taskEvent);
		stopRemainderReplies(taskEvent.getSource());
	}

	private void addReplies(Task task) {
		for (int i = 0; i < task.getSourceJob().getReplicationLevel() - 1; i++) {
			this.getSubmittedTasks().add(task.clone());
		}
	}

	private void stopRemainderReplies(Task task) {
		for (Task reply : task.getActiveReplies()) {
			// para as replicas que ainda estiverem rodando
			if (reply.isRunning()) {
				reply.getTargetPeer().preemptTask(reply);
			} else if (this.getSubmittedTasks().contains(reply)) {// está
				// aguardando
				this.getSubmittedTasks().remove(reply);
			} else {
				assert false;
			}
		}
	}

}