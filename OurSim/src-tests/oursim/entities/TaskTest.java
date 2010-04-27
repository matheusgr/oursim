package oursim.entities;

import junit.framework.*;

public class TaskTest extends TestCase {

	public void testUpdateProcessing() {
		
		Task task = new Task(0,"executavel.exe",30,0,null);
		
		
		TaskExecution taskExecution = new TaskExecution(task);
		
		Processor processor = new Processor(0, 500);
		
		assertEquals(130, taskExecution.updateProcessing(processor, 50));
		
		Processor processor2 = new Processor(0, 200);
		
		assertEquals(295, taskExecution.updateProcessing(processor2, 80));
		
		Processor processor3 = new Processor(0, 1000);
		
		assertEquals(9, taskExecution.updateProcessing(processor3, 130));
		
		assertEquals(9, taskExecution.updateProcessing(processor, 139));
		
		assertEquals(0, taskExecution.updateProcessing(processor, 148));
		
	}

}