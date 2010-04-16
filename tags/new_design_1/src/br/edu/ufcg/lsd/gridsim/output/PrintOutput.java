package br.edu.ufcg.lsd.gridsim.output;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.SQLException;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;

public class PrintOutput implements InterfaceOutput {

    private static InterfaceOutput instance;
    
    private PrintStream out;

    private PrintOutput() {
	try {
	    this.out = new PrintStream("trace_simulation.txt");
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
    
    public synchronized static InterfaceOutput getInstance(String fileName) throws SQLException {
	if (instance == null) {
	    instance = new PrintOutput();
	}
	return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.edu.ufcg.lsd.gridsim.output.InterfaceOutput#finishJob(int,
     *      br.edu.ufcg.lsd.gridsim.SchedulerGLite, br.edu.ufcg.lsd.gridsim.Job,
     *      br.edu.ufcg.lsd.gridsim.ClusterIf)
     */
    @Override
    public void finishJob(int time, GlobalScheduler grid, Job job) {
	this.out.println("F:" + time + ":" + ":" + job.getJobId() + ":" + job.getSubmitTime() + ":" + job.getRunTime() + ":" + (time - job.getSubmitTime())
		+ ":" + job.getPreemptions());
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.edu.ufcg.lsd.gridsim.output.InterfaceOutput#submitJob(int,
     *      br.edu.ufcg.lsd.gridsim.GlobalScheduler,
     *      br.edu.ufcg.lsd.gridsim.Job)
     */
    @Override
    public void submitJob(int time, GlobalScheduler grid, Job job) {
	this.out.println("U:" + time + ":" + job.getJobId());
    }

    @Override
    public void startJob(int time, String source, Job job) {
	this.out.println("S:" + time + ":" + job.getJobId() );
    }

}