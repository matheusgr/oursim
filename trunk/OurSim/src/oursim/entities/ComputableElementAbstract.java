package oursim.entities;


public abstract class ComputableElementAbstract implements ComputableElement {

	@Override
	public Long getMakeSpan() {
		return this.isFinished() ? getFinishTime() - getSubmissionTime() : null;
	}

	@Override
	public Long getRunningTime() {
		return this.isFinished() ? getFinishTime() - getStartTime() : null;
	}

	@Override
	public Long getQueueingTime() {
		return this.isFinished() ? getMakeSpan() - getRunningTime() : null;
	}

}
