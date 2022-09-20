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

import de.featjar.formula.analysis.javasmt.solver.JavaSMTSolver;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.base.task.Monitor;
import org.sosy_lab.java_smt.api.NumeralFormula;

/**
 * Finds the minimum and maximum value of a Term. As example we have the
 * following expression:<br>
 * <br>
 *
 * <code> (Price + 233) &gt; -17</code><br>
 * <br>
 *
 * If you want to evaluate the maximum and minimum value for the variable
 * <code>Price</code> you need to pass the {@link Literal} object to the
 * analysis. The variable of interest can be set via
 * {@link FeatureAttributeRangeAnalysis#setVariable(NumeralFormula)}.
 *
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class FeatureAttributeRangeAnalysis extends JavaSmtSolverAnalysis<Object[]> {
    /** The variable of interest */
    private NumeralFormula variable;

    @Override
    protected Object[] analyze(JavaSMTSolver solver, Monitor monitor) throws Exception {
        if (variable == null) {
            return null;
        }
        final Object[] result = new Object[2];
        solver.findSolution();
        result[0] = solver.minimize(variable);
        result[1] = solver.maximize(variable);
        return result;
    }

    /**
     * Sets the variable of interest. As example we have the following
     * expression:<br>
     * <br>
     *
     * <code> (Price + 233) &gt; -17</code><br>
     * <br>
     *
     * If you want to evaluate the maximum and minimum value for the variable
     * <code>Price</code> you need to pass the Literal object for
     * <code>Price</code>.
     *
     * @param variable The variable to compute the maximum and minimum of.
     */
    public void setVariable(NumeralFormula variable) {
        this.variable = variable;
    }
}
