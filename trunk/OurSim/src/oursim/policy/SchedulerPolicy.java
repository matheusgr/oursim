package oursim.policy;

import oursim.entities.Job;

public interface SchedulerPolicy {

    public void schedule(Job job);
    
}
