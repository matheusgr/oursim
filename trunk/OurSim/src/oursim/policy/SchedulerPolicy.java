package oursim.policy;

import oursim.entities.Job;

public interface SchedulerPolicy {

    public void schedule(Job job);

    public void scheduleNow();

    public void addJob(Job job);
    
    public void finishJob(Job job);

}
