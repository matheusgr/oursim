package oursim.entities;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * This class represents an Task, as usually treated in a bag of task (bot)
 * application. A task is a unit of computation in a bot application and is
 * intended to be processed in only one {@link Processor}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * @see Job
 * @see Processor
 */
public class Task extends ComputableElement implements Comparable<Task> {

	/**
	 * The duration in unit of simulation (seconds) of this Task, considered
	 * when executed in an reference machine.
	 * 
	 * TODO: specify what a reference machine is.
	 * 
	 */
	private final long duration;

	/**
	 * The executable of this task.
	 */
	private File executable;

	/**
	 * The collection of input to a task. ideally, this mustn't be empty.
	 */
	private List<File> inputs;

	/**
	 * The collection of output of a task. ideally, this mustn't be empty.
	 */
	private List<File> outputs;

	/**
	 * The instant at which this task started to running. Through its lifetime a
	 * task may have several start times, but only the latter represents the
	 * definite initial time. If this task is running this field holds a valid
	 * long value, otherwise this field must remains <code>null</code>.
	 */
	private Long startTime = null;

	/**
	 * The instant at which this task has been finished. Through its lifetime a
	 * task have only one finishTime. As long this task is unfinished, its
	 * finishTime must remains <code>null</code>.
	 */
	private Long finishTime = null;

	/**
	 * The {@link Job} that contains this task.
	 */
	private Job sourceJob;

	/**
	 * The {@link Peer} that holds the {@link Machine} in which this task is
	 * running or have been processed, in case it have been finished. If this
	 * task is not running and hasn't been finished yet, this field remains
	 * <code>null</code>.
	 */
	private Peer targetPeer = null;

	/**
	 * The convenient object that is responsible by the execution of this task.
	 * In the same way of the field {@link #targetPeer}, if this task is not
	 * running and hasn't been finished yet, this field remains
	 * <code>null</code>.
	 */
	private TaskExecution taskExecution;

	/**
	 * The total of preemptions suffered by this task.
	 */
	private int numberOfpreemptions;

	public Task(long id, String executable, long duration, long submissionTime, Job sourceJob) {
		super(id, submissionTime);
		this.executable = new File(executable, -1);
		this.duration = duration;
		this.sourceJob = sourceJob;
	}

	/**
	 * Adds an input file to this task.
	 * 
	 * @param name
	 *            The name of the file, actually this could represent an path.
	 * @param size
	 *            Size in bytes of this File.
	 */
	public void addInputFile(String name, long size) {
		this.inputs.add(new File(name, size));
	}

	/**
	 * Adds an output file to this task.
	 * 
	 * @param name
	 *            The name of the file, actually this could represent an path.
	 * @param size
	 *            Size in bytes of this File.
	 */
	public void addOutputFile(String name, long size) {
		this.outputs.add(new File(name, size));
	}

	/**
	 * @return The job that contains this task.
	 */
	public Job getSourceJob() {
		return sourceJob;
	}

	/**
	 * Sets The job that contains this task.
	 * 
	 * @param sourceJob
	 *            The job that contains this task.
	 */
	void setSourceJob(Job sourceJob) {
		this.sourceJob = sourceJob;
	}

	/**
	 * Gets the peer that holds the {@link Machine} in which this task is
	 * running or have been processed, in case it have been finished. If this
	 * task is not running and hasn't been finished yet, this field remains
	 * <code>null</code>.
	 * 
	 * @return Gets the peer that holds the {@link Machine} in which this task
	 *         is running or have been processed, otherwise <code>null</code>
	 *         is returned.
	 */
	public Peer getTargetPeer() {
		return targetPeer;
	}

	/**
	 * Gets the responsible by the execution of this task.
	 * 
	 * @return the responsible by the execution of this task or
	 *         <code>null</code> if this task is not running and hasn't been
	 *         finished yet.
	 */
	public TaskExecution getTaskExecution() {
		return taskExecution;
	}

	/**
	 * Sets the responsible by the execution of this task.
	 * 
	 * @param taskExecution
	 *            the responsible by the execution of this task.
	 */
	public void setTaskExecution(TaskExecution taskExecution) {
		this.taskExecution = taskExecution;
	}

	public void prioritizeResourcesToConsume(List<Machine> resources) {
		this.getSourceJob().getResourceRequestPolicy().rank(resources);
	}

	@Override
	public void setTargetPeer(Peer targetPeer) {
		assert this.targetPeer == null;
		this.targetPeer = targetPeer;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public void finish(long time) {
		assert this.finishTime == null;
		this.finishTime = time;
	}

	@Override
	public Long getStartTime() {
		return startTime;
	}

	@Override
	public void preempt(long time) {
		assert this.startTime != null;
		this.numberOfpreemptions++;
		this.startTime = null;
		this.targetPeer = null;
	}

	/**
	 * Invoking this method means this task is ready to be executed, that is,
	 * there is already a {@link #taskExecution} seted in this task.
	 * 
	 * @see oursim.entities.ComputableElement#setStartTime(long)
	 */
	@Override
	public void setStartTime(long startTime) {
		assert this.taskExecution != null;
		this.startTime = startTime;
	}

	@Override
	public Long getEstimatedFinishTime() {
		assert startTime != null;
		assert taskExecution != null;
		if (startTime != null) {
			return this.getStartTime() + taskExecution.getRemainingTimeToFinish();
		} else {
			return null;
		}
	}

	@Override
	public Long getFinishTime() {
		return finishTime;
	}

	@Override
	public long getNumberOfpreemptions() {
		return numberOfpreemptions;
	}

	@Override
	public Peer getSourcePeer() {
		return this.sourceJob.getSourcePeer();
	}

	@Override
	public boolean isRunning() {
		return this.startTime != null;
	}

	@Override
	public boolean isFinished() {
		return finishTime != null;
	}

	@Override
	public int compareTo(Task t) {
		long diffTime = this.submissionTime - t.getSubmissionTime();
		// os tempos de submissão são iguais?
		if (diffTime == 0) {
			if (id > t.getId()) {
				return 2;
			} else if (id == t.getId()) {
//				assert false : "\n" + this + "\n" + t;
				return this.hashCode() == t.hashCode() ? 0 : (this.hashCode() > t.hashCode() ? 1 : -1);
			} else {
				return -2;
			}
		} else if (diffTime > 0) { // os tempos de submissão são diferentes
			// o meu veio depois?
			return 5;
		} else {
			// o meu veio antes
			return -5;
		}
	}

	@Override
	public String toString() {
		// [id, duration, submissionTime, startTime, finishTime,
		// numberOfpreemptions]
		// return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("sourceJob", sourceJob.getId()).append("sourcePeer",
				getSourcePeer().getName()).append("duration", duration)
		// .append("executable", executable)
				// .append("inputs",inputs)
				// .append("outputs", outputs)
				.append("submissionTime", submissionTime).append("startTime", startTime).append("finishTime", finishTime).append("targetPeer",
						targetPeer != null ? targetPeer.getName() : "").append("numberOfpreemptions", numberOfpreemptions).toString();
	}

}
