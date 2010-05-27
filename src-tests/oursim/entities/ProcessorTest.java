package oursim.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProcessorTest {

	@Test
	public void testCalculateTimeToFinish() {
		Processor processor1 = new Processor(0, 500);
		assertEquals(2, processor1.calculateTimeToExecute(900));
		Processor processor2 = new Processor(0, 3000);
		assertEquals(30, processor2.calculateTimeToExecute(90000));
	}

	@Test
	public void testConvertDurationToMI() {
		assertEquals(90000, Processor.EC2_COMPUTE_UNIT.calculateNumberOfInstructionsProcessed(30));
	}

}