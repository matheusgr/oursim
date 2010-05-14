package oursim.simulationevents;

import oursim.entities.Job;

public abstract class JobTimedEvent extends TimedEventAbstract<Job> {

	JobTimedEvent(long time, int priority, Job job) {
		super(time, priority, job);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String type = this.getType();
		String time = Long.toString(this.getTime());
		String peer = this.content.getSourcePeer().getName();
		String taskId = null;
		String jobId = Long.toString(this.content.getId());
		String makespan = this.content.getMakeSpan() + "";
		String runningTime = this.content.getRunningTime() + "";
		String queuingTime = this.content.getQueueingTime() + "";
		sb.append(type).append(" ")
		  .append(time).append(" ")
		  .append(taskId).append(" ")
		  .append(jobId).append(" ")
		  .append(peer).append(" ")
		  .append(makespan).append(" ")
		  .append(runningTime).append(" ")
		  .append(queuingTime);
		return sb.toString();

	}

}
