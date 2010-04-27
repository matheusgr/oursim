package oursim.entities;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Job extends ComputableElementAbstract implements ComputableElement, Comparable<Job> {

	private final long id;

	private final long submissionTime;

	private final Peer sourcePeer;

	private final List<Task> tasks;

	private static long nextTaskId = 0;

	public Job(long id, long submissionTime, Peer sourcePeer) {

		this.id = id;
		this.submissionTime = submissionTime;
		this.sourcePeer = sourcePeer;

		this.tasks = new ArrayList<Task>();

	}

	public Job(long id, long submissionTime, long runTimeDuration, Peer sourcePeer) {
		this(id, submissionTime, sourcePeer);

		this.tasks.add(new Task(this.id, "executable.exe", runTimeDuration, this.submissionTime, this));

	}

	@Override
	public long getId() {
		return id;
	}

	public void addTask(Task task) {
		assert task.getSourceJob() == null;
		task.setSourceJob(this);
		this.tasks.add(task);
	}

	public void addTask(String executable, long duration) {
		this.tasks.add(new Task(nextTaskId, executable, duration, submissionTime, this));
		nextTaskId++;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	@Override
	public long getSubmissionTime() {
		return submissionTime;
	}

	@Override
	public void finish(long time) {
		for (Task task : tasks) {
			task.finish(time);
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

	@Override
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
		boolean allTasksAreFinished = true;
		for (Task task : tasks) {
			if (allTasksAreFinished &= task.isFinished()) {
				lastFinishTime = Math.max(lastFinishTime, task.getFinishTime());
			} else {
				return null;
			}
		}
		return lastFinishTime;
	}

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
		if (diffTime == 0) {
			if (id > j.getId()) {
				return 2;
			} else if (id == j.getId()) {
				return this.hashCode() == j.hashCode() ? 0 : (this.hashCode() > j.hashCode() ? 1 : -1);
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
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("submissionTime", submissionTime).append("sourcePeer",
				sourcePeer.getName()).append("#tasks", tasks.size()).toString();
	}

}
