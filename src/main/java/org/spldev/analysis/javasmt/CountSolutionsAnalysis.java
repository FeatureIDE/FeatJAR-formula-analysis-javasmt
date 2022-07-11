/* -----------------------------------------------------------------------------
 * formula-analysis-javasmt - Analysis of first-order formulas using JavaSMT
 * Copyright (C) 2021 Sebastian Krieter
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
 * -----------------------------------------------------------------------------
 */
package org.spldev.analysis.javasmt;

import java.math.*;

import org.spldev.analysis.javasmt.solver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Counts the number of valid solutions to a formula.
 * 
 * @author Sebastian Krieter
 */
public class CountSolutionsAnalysis extends JavaSmtSolverAnalysis<BigInteger> {

	public static final Identifier<BigInteger> identifier = new Identifier<>();

	@Override
	public Identifier<BigInteger> getIdentifier() {
		return identifier;
	}

	@Override
	protected BigInteger analyze(JavaSmtSolver solver, InternalMonitor monitor) throws Exception {
		return solver.countSolutions();
	}

}
