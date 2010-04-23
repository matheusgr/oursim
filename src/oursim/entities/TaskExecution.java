package oursim.entities;

class TaskExecution {

	/**
	 * The size in Millions of Instructions (MI) of this task to be executed in
	 */
	private long size;

	private long previousTime;

	private long remainingSize;

	private Task task;

	public TaskExecution(Task task) {
		this.task = task;
		this.size = Processor.EC2_COMPUTE_UNIT.calculateAmountOfInstructionsProcessed(this.task.getDuration());
		this.remainingSize = size;
		this.previousTime = 0;
	}

	/**
	 * @param processor
	 * @param currentTime
	 * @return The time lacking to this Task be finished in that processor.
	 */
	public long updateProcessing(Processor processor, long currentTime) {
		assert currentTime > previousTime;

		// time since last update
		long timePassed = currentTime - previousTime;

		this.remainingSize -= processor.calculateAmountOfInstructionsProcessed(timePassed);

		long estimatedTimeToFinish = (remainingSize <= 0) ? 0 : processor.calculateTimeToExecute(remainingSize);

		this.previousTime = currentTime;

		return estimatedTimeToFinish;

	}

}
