package util;

//
// How do we set a IntegerVariable to a value?
//
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.solver.ContradictionException;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;

public class SetVal {

	public static void main(String[] args) throws ContradictionException {
		Model m = new CPModel();

		IntegerVariable x = makeIntVar("x", 0, 9);
		IntegerVariable y = makeIntVar("y", 0, 9);

		x.setLowB(5);
		x.setUppB(5);

		m.addConstraint(eq(y, 5));

		System.out.println(x.pretty());
		System.out.println(y.pretty() + " YIKES!!!");

		Solver s = new CPSolver();
		s.read(m);
		s.propagate();
		System.out.println(s.getVar(y) + " Ahaaa !!");

	}
}
