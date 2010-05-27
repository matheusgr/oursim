package oursim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import oursim.dispatchableevents.jobevents.JobEventCounter;
import oursim.dispatchableevents.jobevents.JobEventDispatcher;
import oursim.dispatchableevents.taskevents.TaskEventCounter;
import oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import oursim.entities.Job;
import oursim.entities.Peer;
import oursim.input.Workload;
import oursim.input.WorkloadAbstract;
import oursim.policy.ResourceSharingPolicy;
import oursim.simulationevents.EventQueue;
import oursim.simulationevents.FinishJobEvent;
import oursim.simulationevents.FinishTaskEvent;
import oursim.simulationevents.StartedTaskEvent;
import oursim.simulationevents.SubmitJobEvent;
import oursim.simulationevents.SubmitTaskEvent;
import oursim.simulationevents.WorkerAvailableEvent;
import oursim.simulationevents.WorkerUnavailableEvent;

public abstract class AbstractOurSimAPITest {

	OurSimAPI oursim;

	JobEventCounter jobEventCounter;

	TaskEventCounter taskEventCounter;

	@SuppressWarnings("unchecked")
	Set<Class> jobEvents;

	@SuppressWarnings("unchecked")
	Set<Class> taskEvents;

	@SuppressWarnings("unchecked")
	Set<Class> workerEvents;

	final int NUMBER_OF_JOBS_BY_PEER = 10;
	final int NUMBER_OF_TASKS_BY_JOB = 1;
	final int NUMBER_OF_PEERS = 10;
	final int NUMBER_OF_RESOURCES_BY_PEER = 10;
	final int RESOURCE_MIPS_RATING = 3000;
	final int TOTAL_OF_JOBS = NUMBER_OF_PEERS * NUMBER_OF_JOBS_BY_PEER;

	final long JOB_LENGTH = 100;
	final long JOB_SUBMISSION_TIME = 0;

	List<Job> jobs;
	List<Peer> peers;

	long nextJobId = 0;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {

		jobEventCounter = new JobEventCounter();
		JobEventDispatcher.getInstance().addListener(jobEventCounter);

		taskEventCounter = new TaskEventCounter();
		TaskEventDispatcher.getInstance().addListener(taskEventCounter);

		jobEvents = new HashSet<Class>();
		jobEvents.add(SubmitJobEvent.class);
		jobEvents.add(FinishJobEvent.class);

		taskEvents = new HashSet<Class>();
		taskEvents.add(SubmitTaskEvent.class);
		taskEvents.add(StartedTaskEvent.class);
		taskEvents.add(FinishTaskEvent.class);

		workerEvents = new HashSet<Class>();
		workerEvents.add(WorkerAvailableEvent.class);
		workerEvents.add(WorkerUnavailableEvent.class);

		jobs = new ArrayList<Job>(TOTAL_OF_JOBS);
		peers = new ArrayList<Peer>(NUMBER_OF_PEERS);

		for (int i = 0; i < NUMBER_OF_PEERS; i++) {
			peers.add(new Peer("p_" + i, NUMBER_OF_RESOURCES_BY_PEER, RESOURCE_MIPS_RATING, ResourceSharingPolicy.DEFAULT_SHARING_POLICY));
		}

	}

	@After
	public void tearDown() throws Exception {

		EventQueue.getInstance().clear();
		JobEventDispatcher.getInstance().removeListener(jobEventCounter);
		TaskEventDispatcher.getInstance().removeListener(taskEventCounter);
		EventQueue.totalNumberOfEvents = 0;
		nextJobId = 0;

	}

	protected Workload generateDefaultWorkload() {
		return generateWorkload(NUMBER_OF_JOBS_BY_PEER, NUMBER_OF_TASKS_BY_JOB, JOB_SUBMISSION_TIME, JOB_LENGTH);
	}

	protected Workload generateWorkload(final int numberOfJobsByPeer, final int numberOfTasksByJob, final long jobsSubmissionTime, final long jobLength) {

		Workload allWorkloads = new WorkloadAbstract() {
			@Override
			protected void setUp() {
			}
		};

		for (final Peer peer : peers) {

			WorkloadAbstract workloadForPeer = new WorkloadAbstract() {
				@Override
				protected void setUp() {
					for (int i = 0; i < numberOfJobsByPeer; i++) {
						Job job = new Job(nextJobId, jobsSubmissionTime, peer);
						for (int j = 0; j < numberOfTasksByJob; j++) {
							job.addTask("", jobLength);
						}
						this.inputs.add(job);
						nextJobId++;
						jobs.add(job);
					}
				}
			};

			peer.setWorkload(workloadForPeer);
			allWorkloads.merge(workloadForPeer);
		}

		return allWorkloads;
	}

	public static void addJob(long jobId, long submissionTime, long duration, final Peer peer, Collection<Job>... collectionsOfJob) {
		Job job = new Job(jobId, submissionTime, duration, peer);
		for (Collection<Job> collection : collectionsOfJob) {
			collection.add(job);
		}
	}

}