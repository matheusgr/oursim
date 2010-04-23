package oursim.entities;

import java.util.List;

public class Task implements ComputableElement {

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
	private long startTime;
	private long finishTime;

	private Job sourceJob;
	private Peer targetPeer;

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

	@Override
	public Peer getTargetPeer() {
		return targetPeer;
	}

	@Override
	public void setTargetPeer(Peer targetPeer) {
		this.targetPeer = targetPeer;
	}

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

	public void finishTask(long time) {
		this.finishTime = time;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public void preempt(long time) {
		assert this.startTime != -1;
		this.numberOfpreemptions++;
		this.startTime = -1;
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
	public long getEstimatedFinishTime() {
		return this.getStartTime() + this.getDuration();
	}

	@Override
	public long getFinishTime() {
		return finishTime;
	}

	@Override
	public int getNumberOfpreemptions() {
		return numberOfpreemptions;
	}

	@Override
	public int compareTo(ComputableElement t) {
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
	public long getRunTimeDuration() {
		return duration;
	}

	@Override
	public Peer getSourcePeer() {
		return this.sourceJob.getSourcePeer();
	}

}
