package oursim.entities;

import java.util.ArrayList;
import java.util.List;

public class Job implements ComputableElement {

	private final long id;

	private final long submissionTime;
	private long startTime;
	private long finishTime;
	private long runTimeDuration;
	private int wastedTime;

	private final Peer sourcePeer;
	private Peer targetPeer;

	private final List<Task> tasks;

	private int numberOfpreemptions;
	public static int numberOfPreemptionsForAllJobs = 0;

	public Job(long id, int submissionTime, Peer sourcePeer) {

		this.id = id;
		this.submissionTime = submissionTime;
		this.sourcePeer = sourcePeer;

		this.tasks = new ArrayList<Task>();

	}

	public Job(long id, int submissionTime, int runTimeDuration, Peer sourcePeer) {
		this(id, submissionTime, sourcePeer);
		this.runTimeDuration = runTimeDuration;

		this.tasks.add(new Task(this.id, "executable.exe", this.runTimeDuration, this.submissionTime, this));

	}

	public void addTask(Task task) {
		task.setSourceJob(this);
		this.tasks.add(task);
	}

	public Task getFirstTask() {
		return this.tasks.get(0);
	}

	@Override
	public long getSubmissionTime() {
		return getFirstTask().getSubmissionTime();
	}

	@Override
	public long getId() {
		return this.getFirstTask().getId();
		// TODO:JOB return id;
	}

	public void finishJob(long time) {
		this.getFirstTask().finishTask(time);
		// TODO:JOB this.finishTime = time;
	}

	@Override
	public long getRunTimeDuration() {
		return this.getFirstTask().getRunTimeDuration();
		// TODO:JOB return runTimeDuration;
	}

	@Override
	public long getStartTime() {
		return this.getFirstTask().getStartTime();
		// TODO:JOB return startTime;
	}

	@Override
	public Peer getSourcePeer() {
		return sourcePeer;
	}

	@Override
	public Peer getTargetPeer() {
		return this.getFirstTask().getTargetPeer();
		// TODO:JOB return targetPeer;
	}

	@Override
	public void setTargetPeer(Peer targetPeer) {
		this.getFirstTask().setTargetPeer(targetPeer);
		// TODO:JOB this.targetPeer = targetPeer;
	}

	@Override
	public void preempt(long time) {
		this.getFirstTask().preempt(time);
		// TODO:JOB
		// assert this.startTime != -1;
		// this.numberOfpreemptions++;
		numberOfPreemptionsForAllJobs++;
		this.wastedTime += (time - this.startTime);
		// this.startTime = -1;
		// this.setTargetPeer(null);
	}

	@Override
	public void setStartTime(long startTime) {
		this.getFirstTask().setStartTime(startTime);
		// TODO:JOB this.startTime = startTime;
	}

	@Deprecated
	public int getWastedTime() {
		return wastedTime;
	}

	@Deprecated
	public long getWaitedTime() {
		return this.finishTime - (this.submissionTime + this.runTimeDuration);
	}

	@Override
	public long getEstimatedFinishTime() {
		return this.getFirstTask().getEstimatedFinishTime();
		// TODO:JOB return finishTime = this.getStartTime() +
		// this.getRunTimeDuration();
	}

	@Override
	public long getFinishTime() {
		return this.getFirstTask().getFinishTime();
		// TODO:JOB return finishTime;
	}

	@Override
	public int getNumberOfpreemptions() {
		return this.getFirstTask().getNumberOfpreemptions();
		// TODO:JOB return numberOfpreemptions;
	}

	@Override
	public int compareTo(ComputableElement o) {
		return this.getFirstTask().compareTo(o);
		// TODO:JOB
		// long diffTime = this.submissionTime - o.getSubmissionTime();
		// if (diffTime == 0) {
		// if (id > o.getId()) {
		// return 2;
		// } else if (id == o.getId()) {
		// return this.hashCode() == o.hashCode() ? 0 : (this.hashCode() >
		// o.hashCode() ? 1 : -1);
		// } else {
		// return -2;
		// }
		// } else if (diffTime > 0) {
		// return 5;
		// } else {
		// return -5;
		// }
	}

}
