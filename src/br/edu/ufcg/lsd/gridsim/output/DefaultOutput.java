package br.edu.ufcg.lsd.gridsim.output;

import java.sql.SQLException;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;

public class DefaultOutput implements InterfaceOutput {

    private static DefaultOutput instance;

    private InterfaceOutput output;

    public synchronized static DefaultOutput getInstance() {
	if (instance == null) {
	    instance = new DefaultOutput();
	    try {
		instance.output = PrintOutput.getInstance("");
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
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
    }

    @Override
    public void submitJob(int time, GlobalScheduler grid, Job job) {
	output.submitJob(time, grid, job);
    }

    public void startJob(int time, String grid, Job job) {
	output.startJob(time, grid, job);
    }

}