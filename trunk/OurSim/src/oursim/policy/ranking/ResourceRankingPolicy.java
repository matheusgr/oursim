package oursim.policy.ranking;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import oursim.entities.Job;
import oursim.entities.Machine;

/**
 * 
 * An policy to prioritize the resources that will be consumed.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class ResourceRankingPolicy extends RankingPolicy<Job, Machine> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param job
	 *            the job who are are requesting.
	 */
	public ResourceRankingPolicy(Job job) {
		super(job);
	}

	@Override
	public void rank(List<Machine> machines) {
		// Gets best speed first
		Collections.sort(machines, new Comparator<Machine>() {
			@Override
			public int compare(Machine o1, Machine o2) {
				return (int) (o2.getDefaultProcessor().getSpeed() - o1.getDefaultProcessor().getSpeed());
			}
		});
	}

}
