package oursim.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;
import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.entities.Task;
import oursim.policy.NoFSharingPolicy;

public class NoFSharingPolicyTest extends TestCase {

	private NoFSharingPolicy nof;
	private Peer p1;
	private Peer p2;
	private Peer p3;
	private Peer p4;

	protected void setUp() throws Exception {
		super.setUp();
		nof = NoFSharingPolicy.getInstance();
		p1 = new Peer("p1", 10, 1, nof);
		p2 = new Peer("p2", 10, 1, nof);
		p3 = new Peer("p3", 10, 1, nof);
		p4 = new Peer("p4", 10, 1, nof);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// No balance between peers yet
	public final void testOnePeerNoBalance() {
		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 10);
		Job job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 10; id++) {
			Task task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p3,
				resourcesBeingConsumed, runningElements);

		assertEquals(1, preemptablePeers.size());

		assertEquals(p2, preemptablePeers.get(0));

	}

	// No balance, two peers, one consuming more resources
	public final void testTwoPeersSameBalanceOneUsingMoreResources() {

		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 4);
		resourcesBeingConsumed.put(p3, 6);

		Job job;

		job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 4; id++) {
			Task task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}

		job = new Job(1, 10, p3);
		job.setStartTime(10);
		for (int id = 0; id < 6; id++) {
			Task task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p4,
				resourcesBeingConsumed, runningElements);

		assertEquals(1, preemptablePeers.size());

		assertEquals(p3, preemptablePeers.get(0));
	}

	// No balance, two peers using the same amount of resources, recently job
	// first
	public final void testTwoPeersSameBalanceSameResourceUseOneWithMostRecentJob() {

		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 5);
		resourcesBeingConsumed.put(p3, 5);

		Job job;

		job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			Task task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}

		job = new Job(1, 10, p3);
		job.setStartTime(10);
		for (int id = 0; id < 4; id++) {
			Task task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}
		
		Task task = new Task(4, "", 10, 10, job);
		task.setStartTime(20);
		runningElements.add(task);

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p4,
				resourcesBeingConsumed, runningElements);

		assertEquals(1, preemptablePeers.size());

		assertEquals(p3, preemptablePeers.get(0));
	}
	
	public final void testTwoPeersDifferentBalances() {

		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 5);
		resourcesBeingConsumed.put(p3, 5);

		Job job = new Job(0, 10, p1);
		
		Task task = new Task(4, "", 10, 0, job);
		task.setStartTime(0);
		task.finish(10);
		
		nof.increaseBalance(p1, p2, task);
		
		job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}

		job = new Job(1, 10, p3);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p2,
				resourcesBeingConsumed, runningElements);

		assertEquals(1, preemptablePeers.size());

		assertEquals(p3, preemptablePeers.get(0));
	}
	
	public final void testTwoPeersSameBalances() {

		HashMap<Peer, Integer> resourcesBeingConsumed = new HashMap<Peer, Integer>();
		HashSet<Task> runningElements = new HashSet<Task>();

		resourcesBeingConsumed.put(p2, 5);
		resourcesBeingConsumed.put(p3, 5);

		Job job = new Job(0, 10, p1);
		
		Task task = new Task(4, "", 10, 0, job);
		task.setStartTime(0);
		task.finish(10);
		
		nof.increaseBalance(p1, p2, task);
		nof.increaseBalance(p1, p3, task);
		
		job = new Job(0, 10, p2);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}

		job = new Job(1, 10, p3);
		job.setStartTime(10);
		for (int id = 0; id < 5; id++) {
			task = new Task(id, "", 10, 10, job);
			task.setStartTime(10);
			runningElements.add(task);
		}

		List<Peer> preemptablePeers = nof.getPreemptablePeers(p1, p2,
				resourcesBeingConsumed, runningElements);

		assertTrue(preemptablePeers.isEmpty());
	}

}
