package de.featjar.analysis.javasmt.computation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;

import de.featjar.analysis.javasmt.solver.FormulaToJavaSMT;
import de.featjar.analysis.javasmt.solver.JavaSMTFormula;
import de.featjar.analysis.javasmt.solver.JavaSMTSolver;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.formula.assignment.ValueAssignment;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Not;
import de.featjar.formula.structure.connective.Reference;

public class ComputeRedundantClauses extends AJavaSMTAnalysis<List<IExpression>> {
	public ComputeRedundantClauses(IComputation<? extends JavaSMTFormula> formula) {
        super(formula);
    }

    protected ComputeRedundantClauses(ComputeRedundantClausesIncrementally other) {
        super(other);
    }

    @Override
    public Result<List<IExpression>> compute(List<Object> dependencyList, Progress progress) {
    	 JavaSMTSolver solver = initializeSolver(dependencyList);
    	
    	 List<Solvers> compatibleSolvers = Arrays.asList(Solvers.Z3, Solvers.SMTINTERPOL, Solvers.PRINCESS, Solvers.MATHSAT5);
         
         Solvers solverName = solver.getSolverFormula().getSolverName();
         if (!(compatibleSolvers.contains(solverName))) {
         	return Result.empty(new UnsupportedOperationException(solverName + " does not support ComputeRedundantClauses."));
         }
         
         List<IExpression> redundantClauses = new ArrayList<>();
         
         // access originalFormula
         IExpression originalFormula = FORMULA.get(dependencyList).getOriginalFormula();
         FormulaToJavaSMT translator = FORMULA.get(dependencyList).getTranslator();
         // check for structure: Reference and And with children
         if (originalFormula instanceof Reference) {
        	 IExpression expression = originalFormula.getChildren().get(0);
			if (expression instanceof And) {
				// get children clauses of And
				List<? extends IExpression> clausesToTest = expression.getChildren();
				
				// transform them to SMT clauses
				List<BooleanFormula> SMTClausesToTest = new ArrayList<>();
				for (IExpression clause : clausesToTest) {
				    BooleanFormula SMTClause = translator.nodeToFormula(clause);
				    SMTClausesToTest.add(SMTClause);
				}
				
				// iterate through size of children clauses list 
				for (int i = 0; i < SMTClausesToTest.size(); i++) {
					// negate the clause at the current index
					BooleanFormula currentClause = SMTClausesToTest.get(i);
					BooleanFormula currentNegatedClause = translator.createNot(currentClause);
					SMTClausesToTest.set(i, currentNegatedClause);
					
					// set the list of clauses to the new solver formula and solve
					 BooleanFormula currentFormula = translator.createAnd(SMTClausesToTest);
        			 solver.setFormula(currentFormula);
        			 Result<ValueAssignment> findSolution = solver.findSolution();
        			 if (findSolution.isPresent()) {
        				 // restore SMTClausesToTest
        				 SMTClausesToTest.set(i, currentClause);
            		} else {
            			// restore SMTClausesToTest and add clause to redundantClauses
            			SMTClausesToTest.set(i, currentClause);
            			redundantClauses.add(clausesToTest.get(i));
            		}
					
				}
        	 } else {
        		 return Result.empty(new UnsupportedOperationException("Formula's structure is not supported."));
        	 }
         } else {
        	 return Result.empty(new UnsupportedOperationException("Formula's structure is not supported. Reference is missing."));
         }
         
         return Result.ofNullable(redundantClauses);
    }
}