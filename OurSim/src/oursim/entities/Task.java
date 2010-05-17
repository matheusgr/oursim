package oursim.entities;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Task extends ComputableElement implements Comparable<Task> {

	/**
	 * The duration in unit of simulation (seconds) of this Task, considered
	 * when executed in an reference machine.
	 * 
	 * TODO: specify what a reference machine is.
	 * 
	 */
	private final long duration;

	private File executable;

	private List<File> inputs;

	private List<File> outputs;

	private Long startTime = null;
	private Long finishTime = null;

	private Job sourceJob;
	private Peer targetPeer;

	private TaskExecution taskExecution;

	private int numberOfpreemptions;

	public Task(long id, String executable, long duration, long submissionTime, Job sourceJob) {
		super(id, submissionTime);
		this.executable = new File(executable, -1);
		this.duration = duration;
		this.sourceJob = sourceJob;
	}

	public void addInputFile(String name, long size) {
		this.inputs.add(new File(name, size));
	}

	public void addOutputFile(String name, long size) {
		this.outputs.add(new File(name, size));
	}

	public Job getSourceJob() {
		return sourceJob;
	}

	void setSourceJob(Job sourceJob) {
		this.sourceJob = sourceJob;
	}

	public Peer getTargetPeer() {
		return targetPeer;
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

	@Override
	public void setStartTime(long startTime) {
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

	public TaskExecution getTaskExecution() {
		return taskExecution;
	}

	public void setTaskExecution(TaskExecution taskExecution) {
		this.taskExecution = taskExecution;
	}

	@Override
	public int compareTo(Task t) {
		long diffTime = this.submissionTime - t.getSubmissionTime();
		if (diffTime == 0) {
			if (id > t.getId()) {
				return 2;
			} else if (id == t.getId()) {
				return this.hashCode() == t.hashCode() ? 0 : (this.hashCode() > t.hashCode() ? 1 : -1);
			} else {
				return -2;
			}
		} else if (diffTime > 0) {
			return 5;
		} else {
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
