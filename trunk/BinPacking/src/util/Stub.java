package util;

//
// Stub/template, getting started
// Simple example, x < y, x,y in [0..9]
// code for 1st solution, count solutions, get all solutions
//

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.solver.ContradictionException;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

public class Stub {

	public static void main(String[] args) {
		Model m = new CPModel(); // create a model

		IntegerVariable x = makeIntVar("x", 0, 9); // create a variable x,
													// domain 0..9
		IntegerVariable y = makeIntVar("y", 0, 9); // create a variable y,
													// domain 0..9

		m.addConstraint(lt(x, y)); // create a constraint, x < y

		System.out.println("THE MODEL");
		System.out.println(m.pretty()); // display the model

		Solver s = new CPSolver(); // create a solver
		s.read(m); // copy model into solver

		/*
		 * s.solve(true); // find the 1st solution, if it exists
		 * 
		 * System.out.println("SOLVED: THE MODEL VARIABLES");
		 * System.out.println(x.pretty()); // print out the model variables
		 * System.out.println(y.pretty()); System.out.println(m.pretty()); //
		 * print the model (note, not solved!)
		 * 
		 * System.out.println("SOLVED: THE SOLVER VARIABLES");
		 * System.out.println(s.getVar(x).pretty()); // print out the solved
		 * variables System.out.println(s.getVar(y).pretty());
		 * System.out.println(s.pretty()); // print the solver
		 * 
		 *  // // count all solutions and tell me how many //
		 * System.out.println("number of solutions: " + s.getNbSolutions());
		 * 
		 */

		// 
		// get all the solutions
		//
		int n = 0;
		if (s.solve()) {
			System.out.println(n + " " + s.getVar(x).pretty() + " " + s.getVar(y).pretty());
			while (s.nextSolution()) {
				System.out.println(n + " " + s.getVar(x).pretty() + " " + s.getVar(y).pretty());
				n++;
			}
		}

		System.out.println(n);

		//
		// (1) could we get all solutions in a different order?
		// (2) what happens if there is no solution?
		// (3) How many solutions should there be?
		// (4) As (3) but, with neq replacing lt?
		// (5) What if we change domain size?
		// (6) What if we increase number of variables
		// (7) propagate?
		// (8) What would happen if we changed the order that we declared
		// variables?
		//

	}
}

//
//
//
