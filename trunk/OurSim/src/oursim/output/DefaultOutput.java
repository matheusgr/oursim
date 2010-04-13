package oursim.output;

import oursim.entities.Job;

public class DefaultOutput implements Output {

    private static DefaultOutput instance;

    private Output output;

    static Output printOutput = new PrintOutput();

    public synchronized static DefaultOutput getInstance() {
	if (instance == null) {
	    instance = new DefaultOutput();
	    instance.output = printOutput;
	}
	return instance;
    }

    public static void configureInstance(Output output) {
	getInstance().setOutput(output);
    }

    private void setOutput(Output output) {
	this.output = output;
    }

    private DefaultOutput() {

    }

    @Override
    public void finishJob(long time,  Job job) {
	output.finishJob(time, job);
	printOutput.finishJob(time, job);
    }

    @Override
    public void submitJob(long time, Job job) {
	output.submitJob(time,  job);
	printOutput.submitJob(time,  job);
    }

    public void startJob(long time, Job job) {
	output.startJob(time,  job);
	printOutput.startJob(time,  job);
    }

}