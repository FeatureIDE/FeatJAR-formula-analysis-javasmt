/*
 * Copyright (C) 2023 Sebastian Krieter
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
package de.featjar.formula.analysis.javasmt.solver;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.SolverContext;

import de.featjar.formula.structure.IExpression;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Formula for {@link JavaSMTSolver}.
 *
 * @author Sebastian Krieter
 */
public class JavaSmtFormula {

	private final BooleanFormula formula;
    private final List<Formula> variables;
    private final FormulaToJavaSMT translator;

    public JavaSmtFormula(SolverContext solverContext, IExpression expression) {
        translator = new FormulaToJavaSMT(solverContext);
        formula = translator.nodeToFormula(expression);
        variables = translator.getVariableFormulas();
    }

    public FormulaToJavaSMT getTranslator() {
        return translator;
    }

    public BooleanFormula getFormula() {
        return formula;
    }

    public List<Formula> getVariables() {
        return variables;
    }

    public List<BooleanFormula> getBooleanVariables() {
        return variables.stream()
                .filter(f -> f instanceof BooleanFormula)
                .map(f -> (BooleanFormula) f)
                .collect(Collectors.toList());
    }
}
