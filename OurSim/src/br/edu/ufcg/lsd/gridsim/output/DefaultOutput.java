package br.edu.ufcg.lsd.gridsim.output;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;

public class DefaultOutput implements InterfaceOutput {

    private static DefaultOutput instance;

    private InterfaceOutput output;

    static InterfaceOutput printOutput = new PrintOutput();
    
    public synchronized static DefaultOutput getInstance() {
        if (instance == null) {
            instance = new DefaultOutput();
            instance.output = printOutput;
        }
        return instance;
    }

    public static void configureInstance(InterfaceOutput output) {
        getInstance().setOutput(output);
    }

    private void setOutput(InterfaceOutput output) {
        this.output = output;
    }

    private DefaultOutput() {

    }

    @Override
    public void finishJob(int time, GlobalScheduler grid, Job job) {
        output.finishJob(time, grid, job);
        printOutput.finishJob(time, grid, job);
    }

    @Override
    public void submitJob(int time, GlobalScheduler grid, Job job) {
        output.submitJob(time, grid, job);
        printOutput.submitJob(time, grid, job);
    }

	public void startJob(int time, String grid, Job job) {
		output.startJob(time, grid, job);
		printOutput.startJob(time, grid, job);
	}

}