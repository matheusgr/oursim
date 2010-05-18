package oursim.entities;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 22/04/2010
 * 
 */
public class Machine implements Comparable<Machine> {

	/**
	 * The name of this machine. This name must be unique.
	 */
	private String name;

	/**
	 * The processor owned by this machine.
	 * 
	 * TODO: All processor under the same Machine must have the same speed? For
	 * Shared Memory Multiprocessors (SMPs), it is generally assumed that all
	 * processor have the same rating.
	 */
	private List<Processor> processors;

	/**
	 * An special constructor for a monoprocessed machine.
	 * 
	 * @param name
	 *            the machine's name.
	 * @param processorSpeed
	 *            the rating of the only processor of this machine.
	 */
	public Machine(String name, long processorSpeed) {
		this(name, processorSpeed, 1);
	}

	/**
	 * 
	 * An generic constructor for a machine.
	 * 
	 * @param name
	 *            the machine's name.
	 * @param processorSpeed
	 *            the rating of all the processor of this machine.
	 * @param numProcessor
	 *            The number of processor of this machine.
	 * @throws IllegalArgumentException
	 *             in case <code>numProcessor < 1 </code>
	 */
	public Machine(String name, long processorSpeed, int numProcessor) throws IllegalArgumentException {
		assert numProcessor > 0;
		if (numProcessor < 1) {
			throw new IllegalArgumentException("There must be at least one processor in a machine.");
		}

		this.name = name;
		this.processors = new ArrayList<Processor>();

		for (int i = 0; i < numProcessor; i++) {
			addProcessor(processorSpeed);
		}

	}

	private void addProcessor(long speed) {
		int processorId = this.processors.size();
		this.processors.add(new Processor(processorId, speed));
	}

	/**
	 * @return The name of this machine.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the number of processors of this machine.
	 */
	public int getNumberOfProcessors() {
		return this.processors.size();
	}

	/**
	 * @return the number of free processors of this machine.
	 */
	public int getNumberOfFreeProcessors() {
		int numFreeProcessors = 0;
		for (Processor processor : processors) {
			numFreeProcessors += processor.isBusy() ? 0 : 1;
		}
		return numFreeProcessors;
	}

	/**
	 * 
	 * Gets the main processor of a machine.
	 * 
	 * @return the default processor of this machine.
	 */
	public Processor getDefaultProcessor() {
		return processors.get(0);
	}

	@Override
	public int compareTo(Machine o) {
		// TODO: definir critério de comparação.
		return name.compareTo(o.getName());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name).append("#processors", processors.size()).append(
				"processors rating", getDefaultProcessor().getSpeed()).toString();
	}

}