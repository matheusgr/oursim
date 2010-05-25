package oursim.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import oursim.dispatchableevents.Event;
import oursim.entities.Job;
import oursim.entities.Task;

/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class TaskPrintOutput implements Output {

	/**
	 * the stream where the results will be printed out.
	 */
	private PrintStream out;

	/**
	 * An default constructor. Using this constructor the results will be
	 * printed out in the default output.
	 */
	public TaskPrintOutput() {
		this.out = System.out;
	}

	/**
	 * Using this constructor the results will be printed out in the file called
	 * <code>fileName</code>.
	 * 
	 * @param fileName
	 *            The name of the file where the results will be printed out.
	 */
	public TaskPrintOutput(String fileName) {
		try {
			this.out = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
	}

	@Override
	public void close() {
		this.out.close();
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		this.out.println("(U:" + task.getSubmissionTime() + ":" + task.getId() + ")");
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		String machineName = task.getTaskExecution().getMachine().getName();
		this.out.println("(S:" + task.getStartTime() + ":" + task.getId() + ":" + machineName + ")");
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		String machineName = task.getTaskExecution().getMachine().getName();
		this.out.println("(P:" + taskEvent.getTime() + ":" + task.getId() + ":" + machineName + ")");
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		String machineName = task.getTaskExecution().getMachine().getName();
		this.out.println("(F:" + task.getFinishTime() + ":" + task.getId() + ":" + machineName + ")");
	}

}