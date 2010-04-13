package oursim.events;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import oursim.entities.Job;

public class SubmitJobEvent extends TimedEvent {

    private Job job;

    public SubmitJobEvent(long time, Job job) {
	super(time, job.getId());
	this.job = job;
    }

    @Override
    public void doAction() {
    }

}
