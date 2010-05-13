package oursim.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import oursim.dispatchableevents.jobevents.JobEvent;
import oursim.entities.Job;

public class PrintOutput implements Output {

	private PrintStream out;

	public PrintOutput() {
		this.out = System.out;
	}

	public PrintOutput(String fileName) {
		try {
			this.out = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jobFinished(JobEvent jobEvent) {

		Job job = (Job) jobEvent.getSource();

		long id = job.getId();
		long submissionTime = job.getSubmissionTime();
		long numberOfpreemptions = job.getNumberOfpreemptions();
		long runTimeDuration = job.getRunningTime();

		this.out.println("F:" + id + ":" + submissionTime + ":" + runTimeDuration + ":" + numberOfpreemptions);

	}

	@Override
	public void jobSubmitted(JobEvent jobEvent) {

		Job job = (Job) jobEvent.getSource();
		this.out.println("U:" + job.getSubmissionTime() + ":" + job.getId());

	}

	@Override
	public void jobStarted(JobEvent jobEvent) {

		Job job = (Job) jobEvent.getSource();
		this.out.println("S:" + job.getStartTime() + ":" + job.getId());

	}

	@Override
	public void jobPreempted(JobEvent jobEvent) {
		Job job = (Job) jobEvent.getSource();
		this.out.println("P:" + job.getStartTime() + ":" + job.getId() + ":" + jobEvent.getTime());

	}

	@Override
	public void close() {
		this.out.close();
	}

}