package oursim.output;

import oursim.entities.Job;

public interface Output {

    public abstract void submitJob(long time, Job job);

    public abstract void startJob(long time, Job job);

    public abstract void finishJob(long time, Job job);

}
