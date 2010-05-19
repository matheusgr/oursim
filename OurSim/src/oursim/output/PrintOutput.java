package oursim.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import oursim.dispatchableevents.Event;
import oursim.entities.Job;

/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class PrintOutput implements Output {

	/**
	 * the stream where the results will be printed out.
	 */
	private PrintStream out;

	/**
	 * An default constructor. Using this constructor the results will be
	 * printed out in the default output.
	 */
	public PrintOutput() {
		this.out = System.out;
	}

	/**
	 * Using this constructor the results will be printed out in the file called
	 * <code>fileName</code>.
	 * 
	 * @param fileName
	 *            The name of the file where the results will be printed out.
	 */
	public PrintOutput(String fileName) {
		try {
			this.out = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {

		Job job = jobEvent.getSource();

		long id = job.getId();
		long submissionTime = job.getSubmissionTime();
		long numberOfpreemptions = job.getNumberOfpreemptions();
		long runTimeDuration = job.getRunningTime();

		this.out.println("F:" + id + ":" + submissionTime + ":" + runTimeDuration + ":" + numberOfpreemptions);

	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();
		this.out.println("U:" + job.getSubmissionTime() + ":" + job.getId());
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();
		this.out.println("S:" + job.getStartTime() + ":" + job.getId());
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();
		this.out.println("P:" + job.getStartTime() + ":" + job.getId() + ":" + jobEvent.getTime());
	}

	@Override
	public void close() {
		this.out.close();
	}

}