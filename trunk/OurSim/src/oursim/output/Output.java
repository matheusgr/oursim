package oursim.output;

import br.edu.ufcg.lsd.gridsim.Job;

public interface Output {

    public abstract void finishJob(Job job);

    public abstract void submitJob(Job job);

    public abstract void startJob(Job job);

}
