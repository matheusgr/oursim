package oursim.entities;

/**
 * 
 * A Central Processing Unit (CPU) defined in terms of Millions Instructions Per
 * Second (MIPS) rating.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 22/04/2010
 * 
 */
public class Processor {

	/**
	 * EC2 Compute Unit (ECU) – One EC2 Compute Unit (ECU) provides the
	 * equivalent CPU capacity of a 1.0-1.2 GHz 2007 Opteron or 2007 Xeon
	 * processor.
	 */
	static Processor EC2_COMPUTE_UNIT = new Processor(0, 3000);

	/**
	 * the identifier of this processor.
	 */
	private final int id;

	/**
	 * The rating in SPEC MIPS or LINPACK MFLOPS of this processor (MIPSRating).
	 */
	private final long speed;

	/**
	 * Flag indicating of this processor is busy.
	 */
	private boolean busy = false;

	Processor(int id, long speed) {
		this.id = id;
		this.speed = speed;
	}

	public int getId() {
		return id;
	}

	public long getSpeed() {
		return speed;
	}

	/**
	 * Verifies if this processor is busy.
	 * 
	 * @return <code>true</true> if this processor is busy, <code>false</code> otherwise.
	 */
	public boolean isBusy() {
		return busy;
	}

	/**
	 * Indicates that this processor is busy.
	 */
	public void busy() {
		this.busy = true;
	}

	/**
	 * Indicates that this processor is free.
	 */
	public void free() {
		this.busy = false;
	}

	/**
	 * Calculate the number of instructions that could be processed by this
	 * processor in a given amount of time.This method could be seem as the
	 * opposite of {@link #calculateTimeToExecute(long).
	 * 
	 * @param duration
	 *            the amount of time in which is going to be calculated the
	 *            number of instructions.
	 * @return the number of instructions that could be processed by this
	 *         processor in a given amount of time.
	 * @throws IllegalArgumentException
	 *             if <code>duration < 1</code>.
	 * @see {@link #calculateTimeToExecute(long)}
	 */
	public long calculateNumberOfInstructionsProcessed(long duration) throws IllegalArgumentException {
		assert duration > 0;
		if (duration < 1) {
			throw new IllegalArgumentException("duration must be at least 1.");
		}
		return speed * duration;
	}

	/**
	 * Calculate the amount of time needed to processed a given number of
	 * instructions. This method could be seem as the opposite of
	 * {@link #calculateNumberOfInstructionsProcessed(long)}.
	 * 
	 * @param numberOfInstruction
	 *            the number of instructions to be processed.
	 * @return the amount of time needed to processed a given number of
	 *         instructions.
	 * @throws IllegalArgumentException
	 *             if <code>numberOfInstruction < 1</code>.
	 * @see {@link #calculateNumberOfInstructionsProcessed(long)}
	 */
	public long calculateTimeToExecute(long numberOfInstruction) throws IllegalArgumentException {
		assert numberOfInstruction > 0;
		if (numberOfInstruction < 1) {
			throw new IllegalArgumentException("numberOfInstruction must be at least 1.");
		}
		// it must be casted to a double value because "/" truncates the result
		double estimatedFinishTimeD = (double) numberOfInstruction / speed;
		long estimatedFinishTimeL = (long) estimatedFinishTimeD;
		long adjustment = (estimatedFinishTimeL < estimatedFinishTimeD) ? 1 : 0;
		return estimatedFinishTimeL + adjustment;
	}

}
