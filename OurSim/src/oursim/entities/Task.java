package oursim.entities;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Task extends ComputableElementAbstract implements ComputableElement, Comparable<Task> {

	/**
	 * The duration of this task in seconds on a reference machine.
	 * 
	 * TODO: specify what a reference machine is.
	 */
	private final long duration;

	private String executable;

	private List<File> inputs;

	private List<File> outputs;

	private final long id;

	private final long submissionTime;
	private Long startTime = null;
	private Long finishTime;

	private Job sourceJob;
	private Peer targetPeer;

	private TaskExecution taskExecution;

	private int numberOfpreemptions;

	public Task(long id, String executable, long duration, long submissionTime, Job sourceJob) {
		this.id = id;
		this.executable = executable;
		this.duration = duration;
		this.submissionTime = submissionTime;
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
	public List<Peer> getTargetPeers() {
		List<Peer> targetPeers = new ArrayList<Peer>();
		targetPeers.add(targetPeer);
		return targetPeers;
	}

	@Override
	public void setTargetPeer(Peer targetPeer) {
		this.targetPeer = targetPeer;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public long getSubmissionTime() {
		return submissionTime;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void finish(long time) {
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
		this.setTargetPeer(null);
	}

	@Override
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getWaitedTime() {
		return this.finishTime - (this.submissionTime + this.duration);
	}

	@Override
	public Long getEstimatedFinishTime() {
		assert startTime != null;
		// return this.getStartTime() + this.getDuration();
		assert taskExecution != null;
		return this.getStartTime() + taskExecution.getRemainingTimeToFinish();
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
	public String toString() {
		// [id, duration, submissionTime, startTime, finishTime,
		// numberOfpreemptions]
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
		.append("id", id)
		.append("duration", duration)
		.append("submissionTime", submissionTime)
		.append("startTime", startTime)
		.append("finishTime", finishTime)
		.append("numberOfpreemptions", numberOfpreemptions)
		.toString();
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

	public TaskExecution getTaskExecution() {
		return taskExecution;
	}

	public void setTaskExecution(TaskExecution taskExecution) {
		this.taskExecution = taskExecution;
	}

}
