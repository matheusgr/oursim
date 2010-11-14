package br.edu.ufcg.lsd.oursim.entities;

public class TaskExecution {

	/**
	 * The size in Millions of Instructions (MI) of this task to be executed in
	 */
	private long size;

	private long previousTime;

	private long remainingSize;

	private Task task;

	private Processor processor;

	public TaskExecution(Task task, Processor processor, long startTime) {
		this.task = task;
		this.processor = processor;
		// this.size =
		// Processor.EC2_COMPUTE_UNIT.calculateNumberOfInstructionsProcessed(this.task.getDuration());
		this.size = convertTaskDurationToNumberOfInstruction(task);
		this.remainingSize = size;
		this.previousTime = startTime;
	}

	private long convertTaskDurationToNumberOfInstruction(Task task) {
		Peer sourcePeer = task.getSourcePeer();
		Processor referenceProcessor = sourcePeer.getReferenceProcessor();
		return referenceProcessor.calculateNumberOfInstructionsProcessed(this.task.getDuration());
	}

	/**
	 * @param processor
	 * @param currentTime
	 * @return The time lacking to this Task be finished in that processor.
	 */
	public Long updateProcessing(long currentTime) {
		assert currentTime > previousTime;

		// time since last update
		long timeElapsed = currentTime - previousTime;

		// TODO: verificar as consequências do remaining time negativo.
		this.remainingSize -= processor.calculateNumberOfInstructionsProcessed(timeElapsed);

		this.previousTime = currentTime;

		return (remainingSize <= 0) ? 0 : processor.calculateTimeToExecute(remainingSize);

	}

	public Long getRemainingTimeToFinish() {
		if (remainingSize > 0) {
			return processor.calculateTimeToExecute(remainingSize);
		} else {
			return 0L;
		}
	}

	public Long getEstimatedFinishTime() {
		return previousTime + getRemainingTimeToFinish();
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Machine getMachine() {
		return this.processor.getMachine();
	}

}
