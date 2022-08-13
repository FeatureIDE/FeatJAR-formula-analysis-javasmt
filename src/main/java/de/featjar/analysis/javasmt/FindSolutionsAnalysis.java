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

import de.featjar.analysis.javasmt.solver.JavaSmtSolver;
import de.featjar.util.data.Identifier;
import de.featjar.util.job.InternalMonitor;

/**
 * Counts the number of valid solutions to a formula.
 *
 * @author Sebastian Krieter
 */
public class FindSolutionsAnalysis extends JavaSmtSolverAnalysis<Object[]> {

    public static final Identifier<Object[]> identifier = new Identifier<>();

    @Override
    public Identifier<Object[]> getIdentifier() {
        return identifier;
    }

    @Override
    protected Object[] analyze(JavaSmtSolver solver, InternalMonitor monitor) throws Exception {
        return solver.findSolution();
    }
}
