package br.edu.ufcg.lsd.oursim.io.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public final class PrintOutput implements Output {

	private static final String COMMENT_CHARACTER = "#";

	private static final String SEP = ":";

	private static final String SUBMIT_LABEL = "U";

	private static final String START_LABEL = "S";

	private static final String PREEMPT_LABEL = "P";

	private static final String FINISH_LABEL = "F";

	private static final String SUBMIT_HEADER = SUBMIT_LABEL.concat(SEP).concat("submissionTime").concat(SEP).concat("jobId");

	private static final String START_HEADER = START_LABEL.concat(SEP).concat("startTime").concat(SEP).concat("jobId");

	private static final String PREEMPT_HEADER = PREEMPT_LABEL.concat(SEP).concat("preemptionTime").concat(SEP).concat("jobId");

	private static final String FINISH_HEADER =	FINISH_LABEL.concat(SEP)
	.concat("finishTime").concat(SEP)
	.concat("jobId").concat(SEP)
	.concat("submissionTime").concat(SEP)
	.concat("startTime").concat(SEP)
	.concat("runtimeDuration").concat(SEP)
	.concat("makeSpan").concat(SEP)
	.concat("cost").concat(SEP)
	.concat("queuingTime").concat(SEP)
	.concat("numberOfPreemption");

	private static final String SUBMIT_HEADER_2 = SUBMIT_LABEL.concat(SEP)
	.concat("finishTime").concat(SEP)
	.concat("jobId").concat(SEP)
	.concat("submissionTime").concat(SEP)
	.concat("startTime").concat(SEP)
	.concat("runtimeDuration").concat(SEP)
	.concat("makeSpan").concat(SEP)
	.concat("cost").concat(SEP)
	.concat("queuingTime").concat(SEP)
	.concat("numberOfPreemption");

	/**
	 * the stream where the results will be printed out.
	 */
	private PrintStream out;

	private boolean showProgress;

	/**
	 * An default constructor. Using this constructor the results will be
	 * printed out in the default output.
	 */
	public PrintOutput() {
		this.out = System.out;
	}

	public PrintOutput(String fileName) throws IOException {
		this(fileName, false);
	}

	/**
	 * Using this constructor the results will be printed out in the file called
	 * <code>fileName</code>.
	 * 
	 * @param fileName
	 *            The name of the file where the results will be printed out.
	 * @throws FileNotFoundException
	 */
	public PrintOutput(String fileName, boolean showProgress) throws IOException {
		this.showProgress = showProgress;
		this.out = new PrintStream(new File(fileName));
		if (this.showProgress) {
//			this.out.println(COMMENT_CHARACTER + SUBMIT_HEADER);
//			this.out.println(COMMENT_CHARACTER + START_HEADER);
//			this.out.println(COMMENT_CHARACTER + PREEMPT_HEADER);
//			this.out.print(COMMENT_CHARACTER);
		}
		this.out.println(FINISH_HEADER);
	}

	@Override
	public final void jobSubmitted(Event<Job> jobEvent) {
			
			Job job = jobEvent.getSource();

			long jobId = job.getId();
			long submissionTime = job.getSubmissionTime();
			long duration = job.getDuration();

			StringBuilder sb = new StringBuilder(SUBMIT_LABEL);
			sb.append(SEP)
			.append(submissionTime).append(SEP)
			.append(jobId).append(SEP)
			.append(submissionTime).append(SEP)
			.append("NA").append(SEP)
			.append(duration).append(SEP)
			.append("NA").append(SEP)
			.append("NA").append(SEP)
			.append("NA").append(SEP)
			.append("NA");
			this.out.println(sb);
	}

	@Override
	public final void jobStarted(Event<Job> jobEvent) {
		if (showProgress) {
			Job job = jobEvent.getSource();
			
			long jobId = job.getId();
			long submissionTime = job.getSubmissionTime();
			long finishTime = job.getFinishTime();
			long startTime = job.getStartTime();
			long runTimeDuration = job.getRunningTime();
			long makeSpan = job.getMakeSpan();
			double cost = job.getCost();
			long queuingTime = job.getQueueingTime();
			long numberOfPreemptions = job.getNumberOfPreemptions();

			StringBuilder sb = new StringBuilder(START_LABEL);
			sb.append(SEP)
			.append(finishTime).append(SEP)
			.append(jobId).append(SEP)
			.append(submissionTime).append(SEP)
			.append(startTime).append(SEP)
			.append(runTimeDuration).append(SEP)
			.append(makeSpan).append(SEP)
			.append(cost).append(SEP)
			.append(queuingTime).append(SEP)
			.append(numberOfPreemptions);
			this.out.println(sb);
		}
	}

	@Override
	public final void jobPreempted(Event<Job> jobEvent) {
		if (showProgress) {
			Job job = jobEvent.getSource();
			
			long jobId = job.getId();
			long submissionTime = job.getSubmissionTime();
			double cost = job.getCost();
			long numberOfPreemptions = job.getNumberOfPreemptions();

			StringBuilder sb = new StringBuilder(PREEMPT_LABEL);
			sb.append(SEP)
			.append("NA").append(SEP)
			.append(jobId).append(SEP)
			.append(submissionTime).append(SEP)
			.append("NA").append(SEP)
			.append("NA").append(SEP)
			.append("NA").append(SEP)
			.append(cost).append(SEP)
			.append("NA").append(SEP)
			.append(numberOfPreemptions);
			this.out.println(sb);
		}
	}

	@Override
	public final void jobFinished(Event<Job> jobEvent) {

		Job job = jobEvent.getSource();
		
		long jobId = job.getId();
		long submissionTime = job.getSubmissionTime();
		long finishTime = job.getFinishTime();
		long startTime = job.getStartTime();
		long runTimeDuration = job.getRunningTime();
		long makeSpan = job.getMakeSpan();
		double cost = job.getCost();
		long queuingTime = job.getQueueingTime();
		long numberOfPreemptions = job.getNumberOfPreemptions();

		StringBuilder sb = new StringBuilder(FINISH_LABEL);
		sb.append(SEP)
		.append(finishTime).append(SEP)
		.append(jobId).append(SEP)
		.append(submissionTime).append(SEP)
		.append(startTime).append(SEP)
		.append(runTimeDuration).append(SEP)
		.append(makeSpan).append(SEP)
		.append(cost).append(SEP)
		.append(queuingTime).append(SEP)
		.append(numberOfPreemptions);
		this.out.println(sb);
	}

	@Override
	public final void close() {
		this.out.close();
	}

	@Override
	public final void taskFinished(Event<Task> taskEvent) {
	}

	@Override
	public final void taskPreempted(Event<Task> taskEvent) {
	}

	@Override
	public final void taskStarted(Event<Task> taskEvent) {
	}

	@Override
	public final void taskSubmitted(Event<Task> taskEvent) {
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
	}

}