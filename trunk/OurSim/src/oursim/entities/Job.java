package oursim.entities;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

import oursim.Parameters;
import oursim.policy.ranking.ResourceRankingPolicy;

/**
 * 
 * This class represents an Job, as usually treated in a bag of task (bot)
 * application. A Job is compound by a collection of independent {@link Task}
 * and its state is actually derived by the state of its internal tasks.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * @see Task
 * 
 */
public class Job extends ComputableElement implements Comparable<Job> {

	/**
	 * The peer to which this job belongs. The sourcePeer remains unchanged
	 * during its lifetime.
	 */
	private final Peer sourcePeer;

	/**
	 * The collection of tasks that compose this job.
	 */
	private final List<Task> tasks;

	private final ResourceRankingPolicy resourceRankingPolicy;

	private int replicationLevel = Parameters.NUMBER_OF_REPLIES;

	/**
	 * Field to assure the uniqueness of the id of each task.
	 */
	private static long nextTaskId = 0;

	/**
	 * 
	 * An ordinary constructor for a job. Important: Using this constructor the
	 * tasks must be added by calling the method {@link Job#addTask(Task)} or
	 * {@link Job#addTask(String, long)} to fulfill the creation of a job.
	 * 
	 * @param id
	 *            The identifier of this Job. The job ID is a positive long. The
	 *            job ID must be unique and remains unchanged during its
	 *            lifetime.
	 * @param submissionTime
	 *            The instant at which this job must be submitted.
	 * @param sourcePeer
	 *            The peer to which this job belongs.
	 */
	public Job(long id, long submissionTime, Peer sourcePeer) {
		super(id, submissionTime);
		this.sourcePeer = sourcePeer;

		sourcePeer.addJob(this);

		this.tasks = new ArrayList<Task>();

		this.resourceRankingPolicy = new ResourceRankingPolicy(this);

	}

	/**
	 * 
	 * Constructor intended for jobs with only one task. In this special case,
	 * the only task in this job share with it its id.
	 * 
	 * @param id
	 *            The identifier of this Job. The job ID is a positive long. The
	 *            job ID must be unique and remains unchanged during its
	 *            lifetime.
	 * @param submissionTime
	 *            The instant at which this job must be submitted.
	 * @param duration
	 *            The duration in unit of simulation (seconds) of the only task
	 *            contained in this job, considered when executed in an
	 *            reference machine.
	 * @param sourcePeer
	 *            The peer to which this job belongs.
	 */
	public Job(long id, long submissionTime, long duration, Peer sourcePeer) {
		this(id, submissionTime, sourcePeer);

		this.tasks.add(new Task(this.id, "executable.exe", duration, this.submissionTime, this));

	}

	/**
	 * Adds a task to this job. The task to be added mustn't belong to any other
	 * job.
	 * 
	 * @param task
	 *            The task to be added.
	 */
	public boolean addTask(Task task) {
		assert task.getSourceJob() == null;
		if (task.getSourceJob() == null) {
			task.setSourceJob(this);
			this.tasks.add(task);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a task to this job by the information of its executing parameters.
	 * 
	 * @param executable
	 *            The name of the executable of the task.
	 * @param duration
	 *            The duration in unit of simulation (seconds) of the task to be
	 *            added, considered when executed in an reference machine.
	 */
	public void addTask(String executable, long duration) {
		this.tasks.add(new Task(nextTaskId, executable, duration, submissionTime, this));
		nextTaskId++;
	}

	/**
	 * 
	 * Gets the tasks that compound this job.
	 * 
	 * @return the tasks that compound this job.
	 */
	public List<Task> getTasks() {
		return tasks;
	}

	@Override
	public void finish(long time) {
		// TODO: no esquema atual esse método não tem serventia, pois um job só
		// termina quando todas as suas tasks tiverem terminado.
		for (Task task : tasks) {
			if (!task.isFinished()) {
				task.finish(time);
			}
		}
	}

	@Override
	public long getDuration() {
		// a job's duration is a duration of its longest task
		// TODO: another possibility is the sum of all of its tasks
		long longestTaskDuration = Long.MIN_VALUE;
		for (Task task : tasks) {
			longestTaskDuration = Math.max(longestTaskDuration, task.getDuration());
		}
		return longestTaskDuration;
	}

	@Override
	public Long getStartTime() {
		// a job's start time is the start time of its earlier started task
		long earliestTaskStartTime = Long.MAX_VALUE;
		for (Task task : tasks) {
			if (task.isRunning() || task.isFinished()) {
				earliestTaskStartTime = Math.min(earliestTaskStartTime, task.getStartTime());
			}
		}
		return earliestTaskStartTime != Long.MAX_VALUE ? earliestTaskStartTime : null;
	}

	@Override
	public boolean isRunning() {
		// a job is running if at least one of its tasks is running
		for (Task task : tasks) {
			if (task.isRunning()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Peer getSourcePeer() {
		return sourcePeer;
	}

	/**
	 * 
	 * Gets all the peers that are running tasks from this job.
	 * 
	 * @return all the peers that are running tasks from this job.
	 */
	public List<Peer> getTargetPeers() {
		List<Peer> targetPeers = new ArrayList<Peer>();
		for (Task task : tasks) {
			targetPeers.add(task.getTargetPeer());
		}
		return targetPeers;
	}

	@Override
	public void setTargetPeer(Peer targetPeer) {
		for (Task task : tasks) {
			task.setTargetPeer(targetPeer);
		}
	}

	/**
	 * Performs a preemption in all the tasks that compound this job.
	 * 
	 * @see oursim.entities.ComputableElement#preempt(long)
	 */
	@Override
	public void preempt(long time) {
		for (Task task : tasks) {
			task.preempt(time);
		}
	}

	@Override
	public void setStartTime(long startTime) {
		for (Task task : tasks) {
			task.setStartTime(startTime);
		}
	}

	@Override
	public Long getEstimatedFinishTime() {
		long lastTaskEstimatedFinishTime = Long.MIN_VALUE;
		boolean allTasksAreRunning = true;
		for (Task task : tasks) {
			if (allTasksAreRunning &= task.isRunning()) {
				lastTaskEstimatedFinishTime = Math.max(lastTaskEstimatedFinishTime, task.getEstimatedFinishTime());
			} else {
				return null;
			}
		}
		return lastTaskEstimatedFinishTime;
	}

	@Override
	public Long getFinishTime() {
		long lastFinishTime = Long.MIN_VALUE;
		// TODO verificar se isFinished() antes
		for (Task task : tasks) {
			if (task.hasAnyReplyFinished()) {
				lastFinishTime = Math.max(lastFinishTime, task.getAnyReplyFinishTime());
			} else {
				return null;
			}
		}
		return lastFinishTime;
	}

	/**
	 * Gets the sum of the preemptions in all tasks that compound this job.
	 * 
	 * @see oursim.entities.ComputableElement#getNumberOfpreemptions()
	 */
	@Override
	public long getNumberOfpreemptions() {
		long totalOfPreemptions = 0;
		for (Task task : tasks) {
			totalOfPreemptions += task.getNumberOfpreemptions();
		}
		return totalOfPreemptions;
	}

	@Override
	public boolean isFinished() {
		return getFinishTime() != null;
	}

	@Override
	public int compareTo(Job j) {
		long diffTime = this.submissionTime - j.getSubmissionTime();
		// o tempo de submission é o mesmo?
		if (diffTime == 0) {
			if (id > j.getId()) {
				return 2;
			} else if (id == j.getId()) {
				assert false;
				return this.hashCode() == j.hashCode() ? 0 : (this.hashCode() > j.hashCode() ? 1 : -1);
			} else {
				return -2;
			}
		} else if (diffTime > 0) { // tempos de sumissão diferentes
			// o meu veio depois?
			return 5;
		} else {
			// o meu veio antes
			return -5;
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("submissionTime", submissionTime).append("sourcePeer",
				sourcePeer.getName()).append("#tasks", tasks.size()).toString();
	}

	public ResourceRankingPolicy getResourceRequestPolicy() {
		return resourceRankingPolicy;
	}

	public int getReplicationLevel() {
		return replicationLevel;
	}

}
