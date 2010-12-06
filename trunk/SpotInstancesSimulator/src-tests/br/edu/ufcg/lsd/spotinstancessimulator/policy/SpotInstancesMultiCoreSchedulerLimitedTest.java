package br.edu.ufcg.lsd.spotinstancessimulator.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.oursim.AbstractOurSimAPITest;
import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Grid;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Processor;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.InputAbstract;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.input.workload.WorkloadAbstract;
import br.edu.ufcg.lsd.oursim.io.output.TaskPrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;
import br.edu.ufcg.lsd.spotinstancessimulator.dispatchableevents.spotinstances.SpotPriceEventDispatcher;
import br.edu.ufcg.lsd.spotinstancessimulator.entities.EC2Instance;
import br.edu.ufcg.lsd.spotinstancessimulator.io.input.SpotPrice;
import br.edu.ufcg.lsd.spotinstancessimulator.io.output.SpotPricePrintOutput;
import br.edu.ufcg.lsd.spotinstancessimulator.simulationevents.SpotInstancesActiveEntity;

public class SpotInstancesMultiCoreSchedulerLimitedTest extends AbstractOurSimAPITest {

	private static final double SPOT_PRICE = 0.1;
	private static final String INSTANCE_NAME = "m1.small";
	protected static final long SIMULATION_TIME = TimeUtil.ONE_DAY;

	private SpotInstancesMultiCoreSchedulerLimited spotScheduler;

	private int speedByCore;
	private int numCores;
	private int limit;

	private Peer spotInstancesPeer;
	private SpotPrice initialSpotPrice;
	private EC2Instance ec2Instance;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		super.setUp();
		this.spotInstancesPeer = new Peer("SpotInstancesPeer", FifoSharingPolicy.getInstance());
		initialSpotPrice = new SpotPrice("m1.small", new Date(), SPOT_PRICE);
		this.limit = 2;
		this.speedByCore = 1;
		this.numCores = 1;
		this.ec2Instance = new EC2Instance();
		this.ec2Instance.numCores = numCores;
		this.ec2Instance.speedByCore = Math.round(speedByCore * Processor.EC2_COMPUTE_UNIT.getSpeed());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRunSingleCoreMachines() {
		assertTrue(true);
		assertEquals(true, true);

		final int numberOfPeers = 1;
		final int numberOfResources = 6;
		final int numOfJobs = 10;
		final long jobRuntime = TimeUtil.FIFTEEN_MINUTES;

		peers = new ArrayList<Peer>(numberOfPeers);

		final Peer peer = new Peer("the_peer", numberOfResources, Processor.EC2_COMPUTE_UNIT.getSpeed(), FifoSharingPolicy.getInstance());
		peers.add(peer);

		Input<SpotPrice> availability = new InputAbstract<SpotPrice>() {
			@Override
			protected void setUp() {
				long period = TimeUtil.FIFTEEN_MINUTES;
				long time = 0;
				while (time <= SIMULATION_TIME) {
					this.inputs.add(new SpotPrice(INSTANCE_NAME, time, SPOT_PRICE));
					time += period;
				}
			}
		};

		jobs = new ArrayList<Job>(numOfJobs);

		Workload workload = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				for (int i = 0; i < numOfJobs; i++) {
					Job job = addJob(nextJobId++, 0, jobRuntime, peer, this.inputs, jobs);
					job.setUserId("default_user");
					for (Task Task : job.getTasks()) {
						Task.setBidValue(SPOT_PRICE);
					}
				}
			}
		};

		Grid grid = new Grid(peers);

		this.spotScheduler = new SpotInstancesMultiCoreSchedulerLimited(spotInstancesPeer, initialSpotPrice, ec2Instance, limit, true);
		SpotPriceEventDispatcher.getInstance().addListener(this.spotScheduler);

		oursim = new OurSim(EventQueue.getInstance(), grid, spotScheduler, workload, availability);
		oursim.setActiveEntity(new SpotInstancesActiveEntity());
		oursim.start();

		double totalCost = 0.0;
		for (Job job : jobs) {
			totalCost += job.getCost();
		}

		// sem colocar o delta fica bugado
		assertEquals(4 * SPOT_PRICE, totalCost, 0);
		assertEquals(numOfJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(0, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numOfJobs, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(0, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

		TaskEventDispatcher.getInstance().clear();
		SpotPriceEventDispatcher.getInstance().clear();

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRunDualCoreMachines() {

		final int numberOfPeers = 1;
		final int numberOfResources = 6;
		final int numOfJobs = 10;
		final long jobRuntime = TimeUtil.FIFTEEN_MINUTES;

		peers = new ArrayList<Peer>(numberOfPeers);

		final Peer peer = new Peer("the_peer", numberOfResources, Processor.EC2_COMPUTE_UNIT.getSpeed(), FifoSharingPolicy.getInstance());
		peers.add(peer);

		Input<SpotPrice> availability = new InputAbstract<SpotPrice>() {
			@Override
			protected void setUp() {
				long period = TimeUtil.FIFTEEN_MINUTES;
				long time = 0;
				while (time <= SIMULATION_TIME) {
					this.inputs.add(new SpotPrice(INSTANCE_NAME, time, SPOT_PRICE));
					time += period;
				}
			}
		};

		jobs = new ArrayList<Job>(numOfJobs);

		Workload workload = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				for (int i = 0; i < numOfJobs; i++) {
					Job job = addJob(nextJobId++, 0, jobRuntime, peer, this.inputs, jobs);
					job.setUserId("default_user");
					for (Task Task : job.getTasks()) {
						Task.setBidValue(SPOT_PRICE);
					}
				}
			}
		};

		Grid grid = new Grid(peers);

		this.ec2Instance.numCores = 2;
		this.spotScheduler = new SpotInstancesMultiCoreSchedulerLimited(spotInstancesPeer, initialSpotPrice, ec2Instance, limit, true);
		SpotPriceEventDispatcher.getInstance().addListener(this.spotScheduler);

		oursim = new OurSim(EventQueue.getInstance(), grid, spotScheduler, workload, availability);
		oursim.setActiveEntity(new SpotInstancesActiveEntity());
		oursim.start();

		double totalCost = 0.0;
		for (Job job : jobs) {
			totalCost += job.getCost();
		}

		// sem colocar o delta fica bugado
		assertEquals(2 * SPOT_PRICE, totalCost, 0);
		assertEquals(numOfJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(0, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numOfJobs, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(0, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

		TaskEventDispatcher.getInstance().clear();
		SpotPriceEventDispatcher.getInstance().clear();

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRunDualCoreMachines_2() {

		final int numberOfPeers = 1;
		final int numberOfResources = 6;
		final int numOfJobs = 10;
		final long jobRuntime = 10 * TimeUtil.ONE_MINUTE;

		peers = new ArrayList<Peer>(numberOfPeers);

		final Peer peer = new Peer("the_peer", numberOfResources, Processor.EC2_COMPUTE_UNIT.getSpeed(), FifoSharingPolicy.getInstance());
		peers.add(peer);

		Input<SpotPrice> availability = new InputAbstract<SpotPrice>() {
			@Override
			protected void setUp() {
				long period = TimeUtil.FIFTEEN_MINUTES;
				long time = 0;
				while (time <= SIMULATION_TIME) {
					this.inputs.add(new SpotPrice(INSTANCE_NAME, time, SPOT_PRICE));
					time += period;
				}
			}
		};

		jobs = new ArrayList<Job>(numOfJobs);

		Workload workload = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				for (int i = 0; i < numOfJobs; i++) {
					Job job = addJob(nextJobId++, 0, jobRuntime, peer, this.inputs, jobs);
					job.setUserId("default_user");
					for (Task Task : job.getTasks()) {
						Task.setBidValue(SPOT_PRICE);
					}
				}
			}
		};

		Grid grid = new Grid(peers);

		this.ec2Instance.numCores = 2;
		this.spotScheduler = new SpotInstancesMultiCoreSchedulerLimited(spotInstancesPeer, initialSpotPrice, ec2Instance, limit, true);
		SpotPriceEventDispatcher.getInstance().addListener(this.spotScheduler);

		oursim = new OurSim(EventQueue.getInstance(), grid, spotScheduler, workload, availability);
		oursim.setActiveEntity(new SpotInstancesActiveEntity());
		oursim.start();

		double totalCost = 0.0;
		for (Job job : jobs) {
			totalCost += job.getCost();
		}

		// sem colocar o delta fica bugado
		assertEquals(2 * SPOT_PRICE, totalCost, 0);
		assertEquals(numOfJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(0, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numOfJobs, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(0, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

		TaskEventDispatcher.getInstance().clear();
		SpotPriceEventDispatcher.getInstance().clear();

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRunDualCoreMachines_3() {

		final int numberOfPeers = 1;
		final int numberOfResources = 6;
		final int numOfJobs = 10;
		final long jobRuntime = 20 * TimeUtil.ONE_MINUTE - 1;

		peers = new ArrayList<Peer>(numberOfPeers);

		final Peer peer = new Peer("the_peer", numberOfResources, Processor.EC2_COMPUTE_UNIT.getSpeed(), FifoSharingPolicy.getInstance());
		peers.add(peer);

		Input<SpotPrice> availability = new InputAbstract<SpotPrice>() {
			@Override
			protected void setUp() {
				long period = TimeUtil.FIFTEEN_MINUTES;
				long time = 0;
				while (time <= SIMULATION_TIME) {
					this.inputs.add(new SpotPrice(INSTANCE_NAME, time, SPOT_PRICE));
					time += period;
				}
			}
		};

		jobs = new ArrayList<Job>(numOfJobs);

		Workload workload = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				for (int i = 0; i < numOfJobs; i++) {
					Job job = addJob(nextJobId++, 0, jobRuntime, peer, this.inputs, jobs);
					job.setUserId("default_user");
					for (Task Task : job.getTasks()) {
						Task.setBidValue(SPOT_PRICE);
					}
				}
			}
		};

		Grid grid = new Grid(peers);

		this.ec2Instance.numCores = 2;
		this.spotScheduler = new SpotInstancesMultiCoreSchedulerLimited(spotInstancesPeer, initialSpotPrice, ec2Instance, limit, true);
		SpotPriceEventDispatcher.getInstance().addListener(this.spotScheduler);

		oursim = new OurSim(EventQueue.getInstance(), grid, spotScheduler, workload, availability);
		oursim.setActiveEntity(new SpotInstancesActiveEntity());
		oursim.start();

		double totalCost = 0.0;
		for (Job job : jobs) {
			System.out.println(job.getCost());
			totalCost += job.getCost();
		}

		// sem colocar o delta fica bugado
		assertEquals(2 * SPOT_PRICE, totalCost, 0);
		assertEquals(numOfJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(0, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numOfJobs, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(0, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

		TaskEventDispatcher.getInstance().clear();
		SpotPriceEventDispatcher.getInstance().clear();

	}

}
