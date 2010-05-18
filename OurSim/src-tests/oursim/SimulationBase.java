package oursim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import oursim.dispatchableevents.jobevents.JobEvent;
import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.dispatchableevents.jobevents.JobEventListener;
import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.input.Workload;
import oursim.policy.NoFSharingPolicy;

public class SimulationBase {

	private Workload workload;
	private List<Peer> peers;

	public class TestOutput implements JobEventListener {

		private int expectedJobs;
		private long expectedLastTime;
		private long currentLastTime;
		private int expectedStartJobs;
		private int expectedSubmitJobs;

		public TestOutput(long expectedLastTime, int expectedFinishedJobs, int expectedStartJobs, int expectedSubmitJobs) {
			this.expectedJobs = expectedFinishedJobs;
			this.expectedStartJobs = expectedStartJobs;
			this.expectedLastTime = expectedLastTime;
			this.expectedSubmitJobs = expectedSubmitJobs;
			this.currentLastTime = -1;
		}

		public void verify() {
			assertEquals("Last job finished at expected time", this.currentLastTime, this.expectedLastTime);
		}

		@Override
		public void jobFinished(JobEvent jobEvent) {
			this.expectedJobs -= 1;
			assertTrue(this.expectedJobs >= 0);
			Job j = ((Job) jobEvent.getSource());
			assertTrue(this.currentLastTime <= j.getFinishTime());
			this.currentLastTime = j.getFinishTime();
			System.out.println("--- " + expectedJobs);
			System.out.println(jobEvent.getSource());
		}

		@Override
		public void jobStarted(JobEvent jobEvent) {
			System.out.println("*** " + this.expectedStartJobs);
			System.out.println(jobEvent.getSource());
			this.expectedStartJobs -= 1;
			assertTrue(this.expectedStartJobs >= 0);
		}

		@Override
		public void jobSubmitted(JobEvent jobEvent) {
			System.out.println("--- " + expectedSubmitJobs);
			System.out.println(jobEvent.getSource());
			this.expectedSubmitJobs -= 1;
			assertTrue(this.expectedSubmitJobs >= 0);
		}

		@Override
		public void jobPreempted(JobEvent jobEvent) {
			fail("Doesn't expect preemption");
		}

	}

	public class TestWorkload implements Workload {

		private Deque<Job> jobs;

		public TestWorkload(Deque<Job> jobs) {
			this.jobs = jobs;
		}

		@Override
		public void close() {
			assertTrue(jobs.isEmpty());
		}

		@Override
		public Job peek() {
			return jobs.peek();
		}

		@Override
		public Job poll() {
			return jobs.poll();
		}

		@Override
		public boolean merge(Workload other) {
			return false;
		}

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		int numberOfPeers = 10;

		peers = new ArrayList<Peer>(10);

		int i = 0;

		for (i = 0; i < numberOfPeers; i++) {
			peers.add(new Peer(i + "", 1, NoFSharingPolicy.getInstance()));
		}

		Deque<Job> jobs = new LinkedList<Job>();

		for (i = 0; i < 100; i += 10) {
			for (int j = 0; j < numberOfPeers; j++) {
				jobs.add(new Job(i * 100 + j, i, 10, peers.get(j)));
			}
		}

		workload = new TestWorkload(jobs);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testRun() {
		TestOutput to = new TestOutput(100, 100, 100, 100);
		JobEventDispatcher.getInstance().addListener(to);
		new OurSimAPI().run(peers, workload);
		to.verify();
	}

}