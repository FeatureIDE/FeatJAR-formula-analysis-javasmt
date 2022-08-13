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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.featjar.formula.ModelRepresentation;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Biimplies;
import de.featjar.formula.structure.compound.Implies;
import de.featjar.formula.structure.compound.Or;
import de.featjar.util.data.Result;
import de.featjar.util.extension.ExtensionLoader;
import de.featjar.util.logging.Logger;

public class CountSolutionAnalysisTest {

	private static final Path modelDirectory = Paths.get("src/test/resources/testFeatureModels");

	private static final List<String> modelNames = Arrays.asList( //
		"basic", //
		"simple", //
		"car", //
		"gpl_medium_model", //
		"500-100");

	private static ModelRepresentation load(final Path modelFile) {
		return ModelRepresentation.load(modelFile) //
			.orElseThrow(p -> new IllegalArgumentException(p.isEmpty() ? null : p.get(0).toException()));
	}

	static {
		ExtensionLoader.load();
	}

	@Test
	public void count() {
		final VariableMap variables = new VariableMap();
		final Literal a = variables.createLiteral("a");
		final Literal b = variables.createLiteral("b");
		final Literal c = variables.createLiteral("c");

		final Implies implies1 = new Implies(a, b);
		final Or or = new Or(implies1, c);
		final Biimplies equals = new Biimplies(a, b);
		final And and = new And(equals, c);
		final Implies formula = new Implies(or, and);

		final Formula cnfFormula = Formulas.toCNF(formula).get();
		final ModelRepresentation rep = new ModelRepresentation(cnfFormula);

		final CountSolutionsAnalysis analysis = new CountSolutionsAnalysis();
		final Result<?> result = rep.getResult(analysis);
		result.orElse(Logger::logProblems);
		assertTrue(result.isPresent());
		assertEquals(BigInteger.valueOf(3), result.get());
	}

	@Test
	public void count2() {
		final ModelRepresentation rep = load(modelDirectory.resolve(modelNames.get(3) + ".xml"));

		final CountSolutionsAnalysis analysis = new CountSolutionsAnalysis();
		final Result<?> result = rep.getResult(analysis);
		result.orElse(Logger::logProblems);
		assertTrue(result.isPresent());
		assertEquals(BigInteger.valueOf(960), result.get());
	}

}
