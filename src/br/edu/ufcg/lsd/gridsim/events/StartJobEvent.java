package br.edu.ufcg.lsd.gridsim.events;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;
import br.edu.ufcg.lsd.gridsim.output.DefaultOutput;

public class StartJobEvent extends TimedEvent {

    private Job job;
    private String source;
    public static int s = 0;
    public static int o = 0;

    public StartJobEvent(int time, Job job, String source) {
	super(time, job.getJobId(), job);
	this.job = job;
	this.job.setStartTime(time);
	this.job.setStartJobEvent(this);
	this.source = source;
    }

    @Override
    public void doAction() {
	DefaultOutput.getInstance().startJob(time, source, job);
	GlobalScheduler.getInstance().queueFinishJob(job);
    }

}
