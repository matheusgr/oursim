package br.edu.ufcg.lsd.oursim.io.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 *
 *
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 23/10/2010
 *
 */
public final class RemoteWorkloadExtractorOutput extends OutputAdapter {

	private static final String SEP = " ";

	private static final String TASKS_SEP = ";";

	private static final String HEADER = "submissionTime".concat(SEP)
								.concat("jobId").concat(SEP)
								.concat("numberOfTasks").concat(SEP)
								.concat("avgRuntime").concat(SEP)
								.concat("tasks").concat(SEP)
								.concat("userId").concat(SEP)
								.concat("peerId");

	/**
	 * the stream where the results will be printed out.
	 */
	private PrintStream out;

	public RemoteWorkloadExtractorOutput(File file) throws IOException {
		this.out = new PrintStream(file);
		this.out.println(HEADER);
	}

	@Override
	public final void jobFinished(Event<Job> jobEvent) {

		Job job = jobEvent.getSource();

		long remoteMakeSpan = 0;
		StringBuilder remoteTasks = new StringBuilder();
		String tasksSep = "";
		int remTasksSize = 0;
		StringBuilder sb = new StringBuilder();

		Long remoteTasksRuntimeSum = 0l;
		for (Task task : job.getTasks()) {
			if (!task.hasLocallyRunned()) {
				remoteTasks.append(tasksSep).append(task.getDuration());
				remoteTasksRuntimeSum += task.getDuration();
				remTasksSize++;
				remoteMakeSpan = Math.max(remoteMakeSpan, task.getMakeSpan());
				tasksSep = TASKS_SEP;
			}
		}
	
		if (remTasksSize >0) {
			//submissionTime, jobId, numberOfTasks, avgRuntime, tasks, userId, peerId
			sb.append(job.getSubmissionTime()).append(SEP)
			.append(job.getId()).append(SEP)
			.append(remTasksSize).append(SEP)
			.append(Math.round(remoteTasksRuntimeSum/(1.0*remTasksSize))).append(SEP)
			.append(remoteTasks).append(SEP)
			.append(job.getUserId()).append(SEP)
			.append(job.getSourcePeer().getName()).append(SEP);
			this.out.println(sb);
		}

	}

	@Override
	public final void close() {
		this.out.close();
	}

}