/*
 * Copyright (C) 2024 FeatJAR-Development-Team
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
package de.featjar.formula.analysis.javasmt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.featjar.Common;
import de.featjar.base.computation.Computations;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.BiImplies;
import de.featjar.formula.structure.formula.connective.Implies;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class CountSolutionsAnalysisTest extends Common {

    @Test
    public void formulaHas3Solutions() {
        final Literal a = Expressions.literal("a");
        final Literal b = Expressions.literal("b");
        final Literal c = Expressions.literal("c");

        final Implies implies1 = new Implies(a, b);
        final Or or = new Or(implies1, c);
        final BiImplies equals = new BiImplies(a, b);
        final And and = new And(equals, c);
        final Implies formula = new Implies(or, and);

        checkCount(formula, 3);
    }

    @Test
    public void gplHas960Solutions() {
        IFormula formula = loadFormula("testFeatureModels/gpl_medium_model.xml");
        checkCount(formula, 960);
    }

    private void checkCount(final IFormula formula, int count) {
        IFormula cnf = formula.toCNF().orElseThrow();
        final Result<BigInteger> result =
                Computations.of(cnf).map(CountSolutionsAnalysis::new).computeResult();
        assertTrue(result.isPresent(), () -> Problem.printProblems(result.getProblems()));
        assertEquals(BigInteger.valueOf(count), result.get());
    }
}
