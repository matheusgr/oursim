package oursim.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 22/04/2010
 * 
 */
public class Machine {

	private String name;

	// TODO: All processor under the same Machine must have the same speed? For
	// Shared Memory Multiprocessors (SMPs), it is is
	// generally assumed that all processor have the same rating.
	private List<Processor> processors;

	public Machine(String name, long processorSpeed) {
		this(name, processorSpeed, 1);
	}

	public Machine(String name, long processorSpeed, int numProcessor) {

		this.name = name;
		this.processors = new ArrayList<Processor>();

		for (int i = 0; i < numProcessor; i++) {
			addProcessor(processorSpeed);
		}

	}

	public void addProcessor(long speed) {
		int processorId = this.processors.size();
		this.processors.add(new Processor(processorId, speed));
	}

	public String getName() {
		return name;
	}

	public int getNumProcessors() {
		return this.processors.size();
	}

	public int getNumFreeProcessors() {
		int numFreeProcessors = 0;
		for (Processor processor : processors) {
			numFreeProcessors += processor.isBusy() ? 0 : 1;
		}
		return numFreeProcessors;
	}

	public Processor getDefaultProcessor() {
		return processors.get(0);
	}

	@Override
	public String toString() {
		return name;
	}

}
