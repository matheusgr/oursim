package br.edu.ufcg.lsd.oursim.io.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;


/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class JobPrintOutput extends OutputAdapter {

	/**
	 * the stream where the results will be printed out.
	 */
	private PrintStream out;

	/**
	 * An default constructor. Using this constructor the results will be
	 * printed out in the default output.
	 */
	public JobPrintOutput() {
		this.out = System.out;
	}

	/**
	 * Using this constructor the results will be printed out in the file called
	 * <code>fileName</code>.
	 * 
	 * @param fileName
	 *            The name of the file where the results will be printed out.
	 */
	public JobPrintOutput(String fileName) {
		try {
			this.out = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();

		long id = job.getId();
		long submissionTime = job.getSubmissionTime();

		this.out.println("U:" + submissionTime + ":" + id);
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();

		long id = job.getId();
		long startTime = job.getStartTime();

		this.out.println("S:" + startTime + ":" + id);
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();

		long id = job.getId();
		long preemptionTime = jobEvent.getTime();

		this.out.println("P:" + preemptionTime + ":" + id);
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {

		Job job = jobEvent.getSource();

		long id = job.getId();
		long finishTime = job.getFinishTime();
		long submissionTime = job.getSubmissionTime();
		long numberOfpreemptions = job.getNumberOfPreemptions();
		long runTimeDuration = job.getRunningTime();

		this.out.println("F:" + finishTime + ":" + id + ":" + submissionTime + ":" + runTimeDuration + ":" + numberOfpreemptions);

	}

	@Override
	public void close() {
		this.out.close();
	}

}