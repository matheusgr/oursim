package jcat;

/**
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/08/2010
 * 
 */
public interface BinPacking_Events {

	/**
	 * The setup event is called once when the algorithm starts, to communicate
	 * to the views how many blocks will be processed and the maximum number of
	 * bins available.
	 * 
	 * @param numBins
	 * @param numBlocks
	 */
	void setup(int numBins, int numBlocks);

	/**
	 * The newBlock event is called each time the algorithm encounters a new
	 * block, whose weight is specified as the parameter.
	 * 
	 * @param wt
	 */
	void newBlock(double wt);

	/**
	 * The probe event is called each time the algorithm checks if the new block
	 * can be packed into the bin specified as a parameter.
	 * 
	 * @param bin
	 */
	void probe(int bin);

	/**
	 * The pack event is called to signal that the last bin probed is where the
	 * new block will be placed.
	 * 
	 */
	void pack();
}
