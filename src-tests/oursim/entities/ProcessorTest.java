package oursim.entities;

import junit.framework.*;

public class ProcessorTest extends TestCase {

	public void testCalculateTimeToFinish() {
		Processor processor1 = new Processor(0, 500);
		assertEquals(180, processor1.calculateTimeToExecute(90000));
		Processor processor2 = new Processor(0, 3000);
		assertEquals(30, processor2.calculateTimeToExecute(90000));
	}

	public void testConvertDurationToMI() {
		assertEquals(90000, Processor.EC2_COMPUTE_UNIT.calculateAmountOfInstructionsProcessed(30));
	}

}