package oursim.policy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import oursim.entities.Job;
import oursim.entities.Machine;

public class ResourceRequestPolicy extends RequestPolicy<Job, Machine> {

	public ResourceRequestPolicy(Job requester) {
		super(requester);
	}

	@Override
	public void prioritize(List<Machine> machines) {
		// Getting best speed first
		Collections.sort(machines, new Comparator<Machine>() {
			@Override
			public int compare(Machine o1, Machine o2) {
				return (int) (o2.getDefaultProcessor().getSpeed() - o1.getDefaultProcessor().getSpeed());
			}
		});
	}

}
