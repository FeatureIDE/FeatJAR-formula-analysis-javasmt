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
import de.featjar.formula.structure.formula.predicate.ProblemFormula;
import de.featjar.formula.structure.map.TermMap;
import java.util.List;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FunctionDeclaration;
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager;
import org.sosy_lab.java_smt.api.visitors.BooleanFormulaVisitor;

public abstract class FormulaVisitor implements BooleanFormulaVisitor<IExpression> {
    protected final BooleanFormulaManager booleanFormulaManager;
    protected final TermMap termMap;

    public FormulaVisitor(BooleanFormulaManager booleanFormulaManager, TermMap termMap) {
        this.booleanFormulaManager = booleanFormulaManager;
        this.termMap = termMap;
    }

    @Override
    public IExpression visitConstant(boolean value) {
        return new ProblemFormula("unexpected constant");
    }

    @Override
    public IExpression visitBoundVar(BooleanFormula var, int deBruijnIdx) {
        return new ProblemFormula("unexpected bound var");
    }

    @Override
    public IExpression visitNot(BooleanFormula operand) {
        return new ProblemFormula("unexpected not");
    }

    @Override
    public IExpression visitAnd(List<BooleanFormula> operands) {
        return new ProblemFormula("unexpected and");
    }

    @Override
    public IExpression visitOr(List<BooleanFormula> operands) {
        return new ProblemFormula("unexpected or");
    }

    @Override
    public IExpression visitXor(BooleanFormula operand1, BooleanFormula operand2) {
        return new ProblemFormula("unexpected xor");
    }

    @Override
    public IExpression visitEquivalence(BooleanFormula operand1, BooleanFormula operand2) {
        return new ProblemFormula("unexpected equivalence");
    }

    @Override
    public IExpression visitImplication(BooleanFormula operand1, BooleanFormula operand2) {
        return new ProblemFormula("unexpected implication");
    }

    @Override
    public IExpression visitIfThenElse(BooleanFormula condition, BooleanFormula thenFormula, BooleanFormula elseFormula) {
        return new ProblemFormula("unexpected if-then-else");
    }

    @Override
    public IExpression visitQuantifier(
            QuantifiedFormulaManager.Quantifier quantifier,
            BooleanFormula quantifiedAST,
            List<org.sosy_lab.java_smt.api.Formula> boundVars,
            BooleanFormula body) {
        return new ProblemFormula("unexpected quantifier");
    }

    @Override
    public IExpression visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
        return new ProblemFormula("unexpected atom");
    }
}
