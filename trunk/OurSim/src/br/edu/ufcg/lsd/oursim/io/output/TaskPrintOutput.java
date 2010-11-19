package br.edu.ufcg.lsd.oursim.io.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class TaskPrintOutput extends OutputAdapter {

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
	public TaskPrintOutput(File file) {
		try {
			this.out = new PrintStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		this.out.println("(U:" + task.getSubmissionTime() + ":" + task.getReplicaId() + ":" + task.getId() + ")");
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		String machineName = task.getTaskExecution().getMachine().getName();
		this.out.println("(S:" + task.getStartTime() + ":" + task.getId() + ":" + task.getReplicaId() + ":" + machineName + ")");
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		String machineName = task.getTaskExecution().getMachine().getName();
		this.out.println("(P:" + taskEvent.getTime() + ":" + task.getId() + ":" + task.getReplicaId() + ":" + machineName + ")");
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		String machineName = task.getTaskExecution().getMachine().getName();
		this.out.println("(F:" + task.getFinishTime() + ":" + task.getId() + ":" + task.getReplicaId() + ":" + machineName + ")");
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		String machineName = task.getTaskExecution().getMachine().getName();
		this.out.println("(C:" + taskEvent.getTime() + ":" + task.getId() + ":" + task.getReplicaId() + ":" + machineName + ")");
	}

	@Override
	public void close() {
		this.out.close();
	}

}