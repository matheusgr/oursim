package oursim.policy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import oursim.entities.Peer;
import oursim.entities.Task;

/**
 * 
 * An policy to prioritize the peers from which the resources will be consumed.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class TaskPreemptionRankingPolicy extends RankingPolicy<Peer, Task> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param peer
	 *            the peer who are are requesting.
	 */
	public TaskPreemptionRankingPolicy(Peer peer) {
		super(peer);
	}

	@Override
	public void rank(List<Task> tasks) {
		// get recently started job first
		// XXX Política para preemptar task por peer
		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
				return (int) (t2.getStartTime() - t1.getStartTime());
			}
		});

	}

}
