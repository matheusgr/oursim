package oursim.entities;

import junit.framework.*;

public class TaskTest extends TestCase {

	public void testUpdateProcessing() {

		Task task = new Task(0, "executavel.exe", 30, 0, null);

		Processor processor = new Processor(0, 500);

		TaskExecution taskExecution = new TaskExecution(task, processor, 0);

		assertTrue(130 == taskExecution.updateProcessing(50));

		Processor processor2 = new Processor(0, 200);
		taskExecution.setProcessor(processor2);
		assertTrue(295 == taskExecution.updateProcessing(80));

		Processor processor3 = new Processor(0, 1000);
		taskExecution.setProcessor(processor3);
		assertTrue(9 == taskExecution.updateProcessing(130));
		taskExecution.setProcessor(processor);
		assertTrue(9 == taskExecution.updateProcessing(139));

		assertTrue(0 == taskExecution.updateProcessing(148));

	}

}