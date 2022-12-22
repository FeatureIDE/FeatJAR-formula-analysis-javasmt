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
package de.featjar.formula.analysis.javasmt.solver;

import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.map.TermMap;
import de.featjar.formula.structure.formula.connective.And;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.SolverContext;

/**
 * Formula for {@link JavaSMTSolver}.
 *
 * @author Sebastian Krieter
 */
public class JavaSmtFormula extends SolverFormula<BooleanFormula> {

    private final ArrayList<Formula> variables;
    private final FormulaToJavaSmt translator;

    public JavaSmtFormula(SolverContext solverContext, IExpression originalExpression) {
        this(solverContext, originalExpression.getTermMap().orElseGet(TermMap::new));
        if (originalExpression instanceof And) {
            originalExpression.getChildren().forEach(this::push);
        }
    }

    public JavaSmtFormula(SolverContext solverContext, TermMap termMap) {
        super(termMap);
        translator = new FormulaToJavaSmt(solverContext, termMap);
        variables = translator.getVariables();
    }

    public FormulaToJavaSmt getTranslator() {
        return translator;
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

    @Override
    public List<BooleanFormula> push(IExpression clause) throws SolverContradictionException {
        final BooleanFormula constraint = translator.nodeToFormula(clause);
        solverFormulas.add(constraint);
        return Arrays.asList(constraint);
    }

    @Override
    public void clear() {
        solverFormulas.clear();
    }
}
