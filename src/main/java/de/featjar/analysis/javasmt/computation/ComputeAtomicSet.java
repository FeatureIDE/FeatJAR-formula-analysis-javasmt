package de.featjar.analysis.javasmt.computation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;

import de.featjar.analysis.javasmt.solver.FormulaToJavaSMT;
import de.featjar.analysis.javasmt.solver.JavaSMTFormula;
import de.featjar.analysis.javasmt.solver.JavaSMTSolver;
import de.featjar.analysis.javasmt.solver.FormulaToJavaSMT.VariableReference;
import de.featjar.base.computation.ComputeConstant;
import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.base.data.SingleLexicographicIterator;
import de.featjar.formula.structure.connective.Not;
import de.featjar.formula.structure.predicate.Equals;
import de.featjar.formula.structure.term.value.Variable;
import de.featjar.formula.assignment.ValueAssignment;

public class ComputeAtomicSet extends AJavaSMTAnalysis<List<List<Variable>>> {
	
    public ComputeAtomicSet(IComputation<? extends JavaSMTFormula> formula) {
        super(formula);
    }

    protected ComputeAtomicSet(AJavaSMTAnalysis<List<List<Variable>>> other) {
        super(other);
    }

    @Override
    public Result<List<List<Variable>>> compute(List<Object> dependencyList, Progress progress) {
        JavaSMTSolver solver = initializeSolver(dependencyList);
        
        List<Solvers> compatibleSolvers = Arrays.asList(Solvers.Z3, Solvers.SMTINTERPOL, Solvers.PRINCESS, Solvers.MATHSAT5);
        
        Solvers solverName = solver.getSolverFormula().getSolverName();
        if (!(compatibleSolvers.contains(solverName))) {
        	return Result.empty(new UnsupportedOperationException(solverName + " does not support ComputeAtomicSet."));
        }
        
        // formula has to be satisfiable
        
        FormulaToJavaSMT translator = solver.getSolverFormula().getTranslator();
        List<List<Variable>> atomicSets = new ArrayList<>();
   
        List<String> names = solver.getSolverFormula().getVariableMap().getVariableNames();
		int[] index = IntStream.range(0, names.size()).toArray();
        
        SingleLexicographicIterator.stream(index, 2)
        	.forEach(combination -> {
        		int[] indices = combination.indexElements();
        		
        		ValueAssignment solution = solver.findSolution().get();
        		Map<Integer, Object> valueAssignment = solution.getAll();
        		
        		Object valueA = valueAssignment.get(indices[0]);
        	    Object valueB = valueAssignment.get(indices[1]);

        	    if (valueA != null && !valueA.equals(valueB)) {
        	        return; 
        	    }
        		
        		final Variable a = new Variable(names.get(indices[0]), Double.class);
    	        final Variable b = new Variable(names.get(indices[1]), Double.class);
    	        final Not not = new Not(new Equals(a, b));
    	        
    	        BooleanFormula notEqualsToJavaSMT = translator.nodeToFormula(not);
    	        BooleanFormula originalFormula = solver.getSolverFormula().getFormula();
    	        List<BooleanFormula> formulaParts = Arrays.asList(originalFormula, notEqualsToJavaSMT);
    	        
    	        BooleanFormula formulaAndNotEquals = translator.createAnd(formulaParts); 
    	        solver.getSolverFormula().setFormula(formulaAndNotEquals);
    	        
    	        if (!solver.hasSolution().get()) {
    	        	List<Variable> atomicSet = Arrays.asList(a, b);
    	        	atomicSets.add(atomicSet);
    	        }
    	        
    	        solver.getSolverFormula().setFormula(originalFormula);
    	        
    	     });
        
         return Result.of(atomicSets);
    }
}
