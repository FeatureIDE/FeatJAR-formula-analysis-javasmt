/*
 * Copyright (C) 2022 Sebastian Krieter
 *
 * This file is part of formula-analysis-javasmt.
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
package de.featjar.formula.analysis.javasmt;

import de.featjar.formula.analysis.IFormulaAnalysis;
import de.featjar.formula.analysis.javasmt.solver.JavaSMTSolver;
import de.featjar.formula.structure.IExpression;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

/**
 * Base class for analyses using a {@link JavaSMTSolver}.
 *
 * @param <T> the type of the analysis result.
 *
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public abstract class JavaSmtSolverAnalysis<T> extends IFormulaAnalysis<T, JavaSMTSolver, IExpression> {

    public JavaSmtSolverAnalysis() {
        solverInputComputation = FormulaComputation.empty();
    }

    protected JavaSMTSolver newSolver(IExpression input) {
        return new JavaSMTSolver(input, Solvers.SMTINTERPOL);
    }
}
