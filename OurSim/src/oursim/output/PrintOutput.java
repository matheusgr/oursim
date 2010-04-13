package oursim.output;

import java.sql.SQLException;

import oursim.entities.Job;

public class PrintOutput implements Output {

    private static Output instance;

    public PrintOutput() {

    }

    public synchronized static Output getInstance(String fileName) throws SQLException {
	if (instance == null) {
	    instance = new PrintOutput();
	}
	return instance;
    }

    @Override
    public void finishJob(long time, Job job) {
	System.out.println("F:" + time + ":" + ":" + job.getId() + ":" + job.getSubmissionTime()+ ":" + job.getRunTimeDuration() + ":" + (time - job.getSubmissionTime()
		+ ":" + job.getNumberOfpreemptions()));
    }

    @Override
    public void submitJob(long time, Job job) {
	System.out.println("U:" + time + ":" + job.getId());
    }

    @Override
    public void startJob(long time, Job job) {
	System.out.println("S:" + time + ":" + job.getId());
    }

}