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

	private int id;

	// private int MIPSRating_; // in SPEC MIPS or LINPACK MFLOPS
	private long speed; // in SPEC MIPS or LINPACK MFLOPS

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

	public boolean isBusy() {
		return busy;
	}

	public void busy() {
		this.busy = true;
	}

	public void free() {
		this.busy = false;
	}

	public long calculateAmountOfInstructionsProcessed(long duration) {
		assert duration > 0;
		return speed * duration;
	}

	public long calculateTimeToExecute(long amountOfInstruction) {
		assert amountOfInstruction > 0;
		// it must be casted to a double value because "/" truncates the result
		double estimatedFinishTimeD = (double) amountOfInstruction / speed;
		long estimatedFinishTimeL = (long) estimatedFinishTimeD;
		long adjustment = (estimatedFinishTimeL < estimatedFinishTimeD) ? 1 : 0;
		return estimatedFinishTimeL + adjustment;
	}

}
