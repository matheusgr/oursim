package br.edu.ufcg.lsd.gridsim.events;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;
import br.edu.ufcg.lsd.gridsim.output.DefaultOutput;

public class FinishedJobEvent extends TimedEvent {

    private Job job;
    public static int o = 0;

    public FinishedJobEvent(int time, Job job) {
	super(time, -job.getJobId(), job);
	this.job = job;
    }

    @Override
    public void doAction() {
	this.job.finishJob(time);
	DefaultOutput.getInstance().finishJob(time, GlobalScheduler.getInstance(), job);
	o++;
	GlobalScheduler.getInstance().finishJob(job, time);
    }

}
