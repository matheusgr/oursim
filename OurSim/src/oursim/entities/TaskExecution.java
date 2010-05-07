package oursim.entities;

public class TaskExecution {

	/**
	 * The size in Millions of Instructions (MI) of this task to be executed in
	 */
	private long size;

	private long previousTime;

	private long remainingSize;

	private Task task;

	private Processor processor;

	public TaskExecution(Task task, Processor processor, long time) {
		this.task = task;
		this.processor = processor;
		this.size = Processor.EC2_COMPUTE_UNIT.calculateAmountOfInstructionsProcessed(this.task.getDuration());
		this.remainingSize = size;
		this.previousTime = time;
	}

	/**
	 * @param processor
	 * @param currentTime
	 * @return The time lacking to this Task be finished in that processor.
	 */
	public Long updateProcessing(long currentTime) {
		assert currentTime > previousTime;
		assert false;

		// time since last update
		long timeElapsed = currentTime - previousTime;

		// TODO: verificar as consequências do remaining time negativo.
		this.remainingSize -= processor.calculateAmountOfInstructionsProcessed(timeElapsed);

		this.previousTime = currentTime;

		return (remainingSize <= 0) ? 0 : processor.calculateTimeToExecute(remainingSize);

	}

	public Long getRemainingTimeToFinish() {
		return processor.calculateTimeToExecute(remainingSize);
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

}