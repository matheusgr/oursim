package br.edu.ufcg.lsd.gridsim.output;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;

public interface InterfaceOutput {

	public abstract void finishJob(int time, GlobalScheduler grid, Job job);

	public abstract void submitJob(int time, GlobalScheduler grid, Job job);

	public abstract void startJob(int time, String source, Job job);

}