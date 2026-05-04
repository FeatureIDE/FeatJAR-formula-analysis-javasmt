/*
 * Copyright (C) 2026 FeatJAR-Development-Team
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
package de.featjar.analysis.javasmt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.featjar.analysis.javasmt.computation.ComputeAtomicSet;
import de.featjar.analysis.javasmt.computation.ComputeJavaSMTFormula;
import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Computations;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.term.value.Variable;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

public class ComputeAtomicSetTest {

    @BeforeAll
    public static void begin() {
        FeatJAR.testConfiguration().initialize();
    }

    @AfterAll
    public static void end() {
        FeatJAR.deinitialize();
    }

    @Test
    public void formulaHasOneAtomicSet() {
        JavaSMTFormulaGenerator formulaGenerator = new JavaSMTFormulaGenerator(42);
        IFormula formula = formulaGenerator.generate(1000, 4000, 3);
        String formulaPrint = formula.printParseable();
        System.out.println(formulaPrint);

        long start = System.currentTimeMillis();

        // IFormula cnf = formula.toCNF().orElseThrow();
        final Result<List<List<Variable>>> result = Computations.of(formula)
                .map(ComputeJavaSMTFormula::new)
                .set(ComputeJavaSMTFormula.SOLVER, Solvers.Z3)
                .map(ComputeAtomicSet::new)
                .computeResult();

        long end = System.currentTimeMillis();

        long diff = end - start;

        System.out.println(diff / 1000.0);

        assertTrue(result.isPresent(), () -> Problem.printProblems(result.getProblems()));
        List<List<Variable>> resultAtomicSets = result.get();

        // assertEquals(solutionAtomicSets, resultAtomicSets);

    }
}
