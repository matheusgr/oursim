package br.edu.ufcg.lsd.gridsim;

import java.util.Iterator;
import java.util.TreeSet;

import br.edu.ufcg.lsd.gridsim.events.FinishedJobEvent;
import br.edu.ufcg.lsd.gridsim.events.SubmitJobEvent;
import br.edu.ufcg.lsd.gridsim.events.TimeQueue;

public class GlobalScheduler {

	private static GlobalScheduler instance;
	private TimeQueue q;
	private SchedulerOurGrid og;
	private TreeSet<Job> submittedJobs;

	public GlobalScheduler(TimeQueue q, SchedulerOurGrid og, TreeSet<Job> submittedJobs) {
		this.q = q;
		this.og = og;
		this.submittedJobs = submittedJobs;
	}
	
	public void schedule(Job job) {
		q.addEvent(new SubmitJobEvent(q.currentTime(), job));
	}

	public void addJob(Job job) {
		this.submittedJobs.add(job);
		this.scheduleNow();
	}

	public void scheduleNow() {
		if (submittedJobs.isEmpty()) { // Nothing to schedule
			return;
		}
		String source = submittedJobs.last().getSource();
		if (source.equals(Job.SOURCE_OG)) {
			og.schedule();	
		}
	}

	public void finishJob(Job job, int currentTime) {
		if (job.getPeer() != null) {
			og.finishJob(job);
			return;
		}
		assert false;
	}

	public Double getUtilization() {
		return 0.0;
	}

	public static void prepareGlobalScheduler(TimeQueue q, SchedulerOurGrid og, TreeSet<Job> submittedJobs) {
		instance = new GlobalScheduler(q, og, submittedJobs);
	}
	
	public static GlobalScheduler getInstance() {
		return instance;
	}

	public void queueFinishJob(Job job) {
        int wastedTime = job.getStartTime() + job.getRunTime();
        if (Configuration.getInstance().checkpointEnabled()) {
            wastedTime -= job.getWastedTime();
        }
        FinishedJobEvent finishedJobEvent = new FinishedJobEvent(
                wastedTime, job);
        job.setFinishedJobEvent(finishedJobEvent);
        job.finishJob(wastedTime);
        q.addEvent(finishedJobEvent);
	}
}
