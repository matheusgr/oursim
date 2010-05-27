package oursim.policy;

import java.util.Iterator;
import java.util.List;

import oursim.dispatchableevents.Event;
import oursim.entities.Peer;
import oursim.entities.Task;

/**
 * 
 * An reference implementation of a {@link JobSchedulerPolicy}.
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
	public void taskSubmitted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		this.submittedTasks.add(task);
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		task.getTargetPeer().finishTask(task);
		if (task.getSourceJob().isFinished()) {
			// TODO: colocar essa ação em taskfinished event. Refatorar o pacote
			// simulationsevents para que os eventos tenham acesso indiscrimado
			// à fila de eventos para poderem gerar eventos secundários.
			this.getEventQueue().addFinishJobEvent(this.getCurrentTime(), task.getSourceJob());
		}
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		// TODO: Política: o que fazer quando uma task for preemptada.
		// se pelo menos um das replicas já tiver terminado, não tem problemas.
		this.rescheduleTask(task);
	}

	@Override
	public void workerAvailable(Event<String> workerEvent) {
		this.schedule();
	}

}