import java.util.*;
import java.io.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

public class CPBinPack {

    private int[] data;                  // the data to be packed
    private Model model;                 // a model object, to attach variables and constraints
    private IntegerVariable inBin[][];   // inBin[i][j] = 1 iff jth number is in ith bin
    private IntegerVariable inBinT[][];  // transpose of inBin[i][j]
    private IntegerVariable load[];      // load[i] is sum of the numbers in ith bin
    private IntegerVariable binUsed[];   // binUsed[i] = 1 iff load[i] > 0
    private IntegerVariable totBinsUsed; // total number of bins used
    private int c;                       // the capacity of each bin
    private int n;                       // number of items to pack
    private int m;                       // number of bins you have
    private String id;                   // an identification for the problem
	
    public CPBinPack(String fname,int numberOfItems,int numberOfBins,int capacity) throws Exception {
	n              = numberOfItems;
	m              = numberOfBins;
	c              = capacity;
	id             = fname;
	data           = new int[n];
	model          = new CPModel();
	inBin          = new IntegerVariable[m][n];
	inBinT         = new IntegerVariable[n][m];
	
	load           = new IntegerVariable[m];
	binUsed        = new IntegerVariable[m];
	totBinsUsed    = makeIntVar("totBinsUsed",0,m);
	for (int i=0;i<m;i++){
	    load[i] = makeIntVar("l_" + i,0,c);
	    binUsed[i] = makeIntVar("bu_" + i,0,1);
	    for (int j=0;j<n;j++){
		inBin[i][j]  = makeIntVar("B_" + i + "_" + j,0,1);
		inBinT[j][i] = inBin[i][j];
	    }
	}
	MyIo fin = new MyIo(fname);
	for (int j=0;j<n;j++) data[j]=fin.getNextInt();
	fin.close();

	// EDIT
	// sort();
	// first fit decreasing

	for (int i=0;i<m;i++)
	    model.addConstraint(eq(load[i],scalar(data,inBin[i])));
	// 
	// constrain load[i] to be the scalar product of inBin[i] and data[i]
	//

	for (int i=0;i<m;i++)
	    model.addConstraint(ifOnlyIf(eq(binUsed[i],1),gt(load[i],0)));
	// 
	// constrain binUsed[i] to be 1 if and only if there are some
	// items stacked in the ith bin, i.e. load[i] > 0
	//

	for (int i=0;i<n;i++)
	    model.addConstraint(eq(1,sum(inBinT[i])));
	//
	// constrain a piece of data such that it can only be in one bin at a time!
	// i.e. the sum of a column of the array inBin must be equal to 1
	//      such that an item/number is in exactly one bin
	// NOTE: inBinT is transpose of inBin
	//

	model.addConstraint(eq(totBinsUsed,sum(binUsed)));
	//
	// constrain totBinsUsed to be the sum of the binUsed vector
	// we now need to minimise this value
	//

	model.addConstraint(leq(totBinsUsed,m));
	//
	// Now a decision problem
	//

	// EDIT
	// for (int i=0;i<m-1;i++)
	//     model.addConstraint(geq(load[i],load[i+1]));
	//
	// symmetry breaking consistent with first fit decreasing
	//
    }


    public Model getModel(){return model;}

    public IntegerVariable getTotBinsUsed(){return totBinsUsed;}

    public IntegerVariable[] getDecisionVars(){
	IntegerVariable D[] = new IntegerVariable[m * n];
	int k = 0;
	for (int i=0;i<m;i++)
	    for (int j=0;j<n;j++){
		D[k] = inBin[i][j];
		k++;
	    }
	return D;
    }

    public String toString(Solver sol){
	String s = id + " " + " #items: " + n + 
	          " #bins: " + m + " capacity: " + c + "\n";
	s = s + "#bins used: " +  sol.getVar(totBinsUsed).getVal() + "\n";
	int sum = 0;
	for (int j=0;j<n;j++){
	    s = s + data[j] + " ";
	    sum = sum + data[j];
	}
	s = s + "sum: " + sum + "\n";
	for (int i=0;i<m;i++){
	    s = s + "bin[" + i + "] ";
	    for (int j=0;j<n;j++)
		s = s + sol.getVar(inBin[i][j]).getVal() + " ";
	    s = s + sol.getVar(load[i]).getVal() + "\n";
	}
	return s;
    }

    private void swap(int i,int j){
	int temp = data[i];
	data[i] = data[j];
	data[j] = temp;
    }

    private void sort(){
	for (int i=n-1;i>0;i--)
	    for (int j=0;j<i;j++)
		if (data[j] < data[j+1])
		    swap(j,j+1);
    }
	


    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
	String fname       = args[0];
	int capacity       = Integer.parseInt(args[1]);
	int numberOfItems  = Integer.parseInt(args[2]);    
	int numberOfBins   = Integer.parseInt(args[3]);

	CPBinPack bp = new CPBinPack(fname,numberOfItems,numberOfBins,capacity);

	Model model = bp.getModel();

	Solver s = new CPSolver();
	s. read(model);

	// EDIT
	// s.setVarIntSelector(new StaticVarOrder(s.getVar(bp.getDecisionVars())));
	//
	// only consider the decision variables
	//

	// EDIT
	// s.setValIntIterator(new DecreasingDomain()); 
	//
	// select values 1 before 0 (why?)
	//
	System.out.println(s.solve(false));	
	System.out.println(bp.toString(s));
	s.printRuntimeSatistics();
    }
}
