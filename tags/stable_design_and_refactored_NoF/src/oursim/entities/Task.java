package oursim.entities;

import java.util.List;

class Task implements Comparable<Task> {

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

	private final Peer sourcePeer;
	private Peer targetPeer;

	private int numberOfpreemptions;

	public Task(String executable, long duration, long id, int submissionTime, Peer sourcePeer) {
		this.id = id;
		this.executable = executable;
		this.duration = duration;
		this.submissionTime = submissionTime;
		this.sourcePeer = sourcePeer;
	}

	public void addInputFile(String name, long size) {
		this.inputs.add(new File(name, size));
	}

	public void addOutputFile(String name, long size) {
		this.outputs.add(new File(name, size));
	}

	public Peer getSourcePeer() {
		return sourcePeer;
	}

	public Peer getTargetPeer() {
		return targetPeer;
	}

	public void setTargetPeer(Peer targetPeer) {
		this.targetPeer = targetPeer;
	}

	public long getDuration() {
		return duration;
	}

	public long getSubmissionTime() {
		return submissionTime;
	}

	public long getId() {
		return id;
	}

	public void finishJob(long time) {
		this.finishTime = time;
	}

	public long getStartTime() {
		return startTime;
	}

	public void preempt(long time) {
		assert this.startTime != -1;
		this.numberOfpreemptions++;
		this.startTime = -1;
		this.setTargetPeer(null);
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getWaitedTime() {
		return this.finishTime - (this.submissionTime + this.duration);
	}

	public long getEstimatedFinishTime() {
		return this.getStartTime() + this.getDuration();
	}

	public long getFinishTime() {
		return finishTime;
	}

	public int getNumberOfpreemptions() {
		return numberOfpreemptions;
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

}
