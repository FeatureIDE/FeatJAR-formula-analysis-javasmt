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
 * See <https://github.com/FeatJAR/formula-analysis-javasmt> for further information.
 */
package de.featjar.analysis.javasmt;

import de.featjar.analysis.AbstractAnalysis;
import de.featjar.analysis.javasmt.solver.JavaSmtSolver;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.FormulaProvider;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

/**
 * Base class for analyses using a {@link JavaSmtSolver}.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public abstract class JavaSmtSolverAnalysis<T> extends AbstractAnalysis<T, JavaSmtSolver, Formula> {

    public JavaSmtSolverAnalysis() {
        super();
        solverInputProvider = FormulaProvider.empty();
    }

    @Override
    protected JavaSmtSolver createSolver(Formula input) {
        return new JavaSmtSolver(input, Solvers.SMTINTERPOL);
    }
}
