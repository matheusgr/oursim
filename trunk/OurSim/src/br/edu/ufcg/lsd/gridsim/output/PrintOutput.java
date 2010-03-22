package br.edu.ufcg.lsd.gridsim.output;

import java.sql.SQLException;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;

public class PrintOutput implements InterfaceOutput {

    private static InterfaceOutput instance;

    public PrintOutput() {

    }

    public synchronized static InterfaceOutput getInstance(String fileName)
            throws SQLException {
        if (instance == null) {
            instance = new PrintOutput();
        }
        return instance;
    }

    /* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.gridsim.output.InterfaceOutput#finishJob(int, br.edu.ufcg.lsd.gridsim.SchedulerGLite, br.edu.ufcg.lsd.gridsim.Job, br.edu.ufcg.lsd.gridsim.ClusterIf)
	 */
    @Override
    public void finishJob(int time, GlobalScheduler grid, Job job) {
        System.out.println("F:" + time + ":" + ":" + job.getJobId()
                + ":" + job.getSubmitTime() + ":" + job.getRunTime() + ":" + (time - job.getSubmitTime()) + ":" + job.getPreemptions() + ":" + job.getSource());
    }
    
    /* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.gridsim.output.InterfaceOutput#submitJob(int, br.edu.ufcg.lsd.gridsim.GlobalScheduler, br.edu.ufcg.lsd.gridsim.Job)
	 */
    @Override
    public void submitJob(int time, GlobalScheduler grid, Job job) {
        System.out.println("U:" + time + ":" + job.getJobId()
                + ":" + job.getSource());
    }

	@Override
	public void startJob(int time, String source, Job job) {
		System.out.println("S:" + time + ":" + job.getJobId()
                + ":" + source);
	}

}