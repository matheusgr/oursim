package oursim.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import oursim.entities.Job;

public class PrintOutput implements Output {

    private static Output instance;

    private PrintStream out;
    
    private PrintOutput() {
	this.out = System.out;
    }

    private PrintOutput(String fileName) {
	try {
	    this.out = new PrintStream(new File(fileName));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    public synchronized static Output getInstance() {
	if (instance == null) {
	    instance = new PrintOutput("trace_oursim.txt");
	}
	return instance;
    }

    @Override
    public void finishJob(long time, Job job) {
	this.out.println("F:" + time + ":" + ":" + job.getId() + ":" + job.getSubmissionTime() + ":" + job.getRunTimeDuration() + ":"
		+ (time - job.getSubmissionTime() + ":" + job.getNumberOfpreemptions()));
    }

    @Override
    public void submitJob(long time, Job job) {
	this.out.println("U:" + time + ":" + job.getId());
    }

    @Override
    public void startJob(long time, Job job) {
	this.out.println("S:" + time + ":" + job.getId());
    }

}