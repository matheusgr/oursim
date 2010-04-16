package br.edu.ufcg.lsd.gridsim.events;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;
import br.edu.ufcg.lsd.gridsim.output.DefaultOutput;

public class SubmitJobEvent extends TimedEvent {

    private Job job;

    public SubmitJobEvent(int time, Job job) {
	super(time, job.getJobId(), job);
	this.job = job;
    }

    @Override
    public void doAction() {
	DefaultOutput.getInstance().submitJob(time, GlobalScheduler.getInstance(), job);
	GlobalScheduler.getInstance().addJob(job);
    }

}
