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
package de.featjar.analysis.javasmt.solver;

import de.featjar.analysis.solver.AbstractDynamicFormula;
import de.featjar.analysis.solver.RuntimeContradictionException;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.formula.structure.compound.And;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.SolverContext;

/**
 * Formula for {@link JavaSmtSolver}.
 *
 * @author Sebastian Krieter
 */
public class JavaSmtFormula extends AbstractDynamicFormula<BooleanFormula> {

    private final ArrayList<Formula> variables;
    private final FormulaToJavaSmt translator;

    public JavaSmtFormula(SolverContext solverContext, de.featjar.formula.structure.Formula originalFormula) {
        this(solverContext, originalFormula.getVariableMap().orElseGet(VariableMap::new));
        if (originalFormula instanceof And) {
            originalFormula.getChildren().forEach(this::push);
        }
    }

    public JavaSmtFormula(SolverContext solverContext, VariableMap variableMap) {
        super(variableMap);
        translator = new FormulaToJavaSmt(solverContext, variableMap);
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
    public List<BooleanFormula> push(de.featjar.formula.structure.Formula clause) throws RuntimeContradictionException {
        final BooleanFormula constraint = translator.nodeToFormula(clause);
        constraints.add(constraint);
        return Arrays.asList(constraint);
    }

    @Override
    public void clear() {
        constraints.clear();
    }
}
