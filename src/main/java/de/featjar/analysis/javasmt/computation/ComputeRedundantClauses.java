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

import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

import de.featjar.analysis.javasmt.solver.JavaSMTFormula;
import de.featjar.analysis.javasmt.solver.JavaSMTSolver;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.FormulaNormalForm;
import de.featjar.formula.structure.term.value.Variable;

/**
 * Computes redundant clauses.
 *
 * @author Sebastian Krieter
 */
public class ComputeRedundantClauses extends AJavaSMTAnalysis<List<List<Variable>>> {

    public ComputeRedundantClauses(IComputation<? extends JavaSMTFormula> formula) {
        super(formula);
    }

    protected ComputeRedundantClauses(ComputeRedundantClauses other) {
        super(other);
    }

    @Override
    public Result<List<List<Variable>>> compute(List<Object> dependencyList, Progress progress) {
    	 JavaSMTSolver solver = initializeSolver(dependencyList);
    	
    	 List<Solvers> compatibleSolvers = Arrays.asList(Solvers.Z3, Solvers.SMTINTERPOL, Solvers.PRINCESS, Solvers.MATHSAT5);
         
         Solvers solverName = solver.getSolverFormula().getSolverName();
         if (!(compatibleSolvers.contains(solverName))) {
         	return Result.empty(new UnsupportedOperationException(solverName + " does not support ComputeRedundantClauses."));
         }
         
         // check for Reference 
         // check for CNF 
         IFormula formula = (IFormula) solver.getSolverFormula().getOriginalFormula();
         
         boolean isNF = formula.isStrictNormalForm(FormulaNormalForm.CNF);
         
         // negate clause
         // check for satisfiability 
         // add/remove clause from formula
         
         
         // test return value
         final Variable a = new Variable("a", Double.class);
	     final Variable b = new Variable("b", Double.class);
         List<List<Variable>> redundantClauses = new ArrayList<>();
         
         List<Variable> redundantClause = Arrays.asList(a, b);
         redundantClauses.add(redundantClause);
        
    	 return Result.of(redundantClauses);
    }
}
