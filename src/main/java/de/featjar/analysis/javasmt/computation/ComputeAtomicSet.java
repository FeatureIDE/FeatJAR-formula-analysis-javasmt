/*
 * Copyright (C) 2025 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula-analysis-javasmt.
 *
 * formula-analysis-javasmt is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula-analysis-javasmt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula-analysis-javasmt. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula-analysis-javasmt> for further information.
 */

package de.featjar.analysis.javasmt.computation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;

import de.featjar.analysis.javasmt.solver.FormulaToJavaSMT;
import de.featjar.analysis.javasmt.solver.JavaSMTFormula;
import de.featjar.analysis.javasmt.solver.JavaSMTSolver;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.base.data.SingleLexicographicIterator;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.connective.Not;
import de.featjar.formula.structure.predicate.Equals;
import de.featjar.formula.structure.term.value.Variable;
import de.featjar.formula.assignment.ValueAssignment;

/**
 * Finds atomic sets.
 *
 * @author Sebastian Krieter
 * @author Klara Surmeier
 */
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
        IExpression originalFormula = solver.getSolverFormula().getOriginalFormula();
        List<List<Variable>> atomicSets = new ArrayList<>();
   
        List<String> names = solver.getSolverFormula().getVariableMap().getVariableNames();
		//int[] index = IntStream.range(0, names.size()).toArray();
        int[] index = solver.getSolverFormula().getVariableMap()
        		.getIndices()
        		.stream()          
                .mapToInt(i -> i)  
                .toArray(); 
        
        ArrayList<ValueAssignment> solutions = new ArrayList<>();
        
        SingleLexicographicIterator.stream(index, 2)
        	.forEach(combination -> {
        		int[] indices = combination.indexElements();
        		
				for (ValueAssignment valueAssignment : solutions) {
	        		int keyA = index[indices[0]]; 
	        		int keyB = index[indices[1]];
	        		
	        		Map<Integer, Object> all = valueAssignment.getAll();
					Object valueA = all.get(keyA);
	        	    Object valueB = all.get(keyB);
	
	        	    if (!valueA.equals(valueB)) {
	        	        return; 
	        	    }
				}
        		
        		final Variable a = new Variable(names.get(indices[0]), Double.class);
    	        final Variable b = new Variable(names.get(indices[1]), Double.class);
    	        final Not not = new Not(new Equals(a, b));
    	        
    	        BooleanFormula notEqualsToJavaSMT = translator.nodeToFormula(not);
    	        BooleanFormula javaSMTFormula = translator.nodeToFormula(originalFormula);
    	        List<BooleanFormula> formulaParts = Arrays.asList(javaSMTFormula, notEqualsToJavaSMT);
    	        
    	        BooleanFormula formulaAndNotEquals = translator.createAnd(formulaParts); 
    	        
    	        // And formula = new And(originalFormula, not);
    	        solver.setFormula(formulaAndNotEquals);

        		Result<ValueAssignment> findSolution = solver.findSolution();
    	        if (findSolution.isEmpty()) {
    	        	List<Variable> atomicSet = Arrays.asList(a, b);
    	        	atomicSets.add(atomicSet);
    	        } 
    	        else {
    	        	solutions.add(findSolution.get());
    	        }
    	        
    	        solver.setFormula(javaSMTFormula);
    	        
    	     });
        
         return Result.of(atomicSets);
    }
}
