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

import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Problem;
import de.featjar.formula.tmp.TermMap;
import java.util.List;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FunctionDeclaration;
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager;
import org.sosy_lab.java_smt.api.visitors.BooleanFormulaVisitor;

public abstract class FormulaVisitor implements BooleanFormulaVisitor<Expression> {
    protected final BooleanFormulaManager booleanFormulaManager;
    protected final TermMap termMap;

    public FormulaVisitor(BooleanFormulaManager booleanFormulaManager, TermMap termMap) {
        this.booleanFormulaManager = booleanFormulaManager;
        this.termMap = termMap;
    }

    @Override
    public Expression visitConstant(boolean value) {
        return new Problem("unexpected constant");
    }

    @Override
    public Expression visitBoundVar(BooleanFormula var, int deBruijnIdx) {
        return new Problem("unexpected bound var");
    }

    @Override
    public Expression visitNot(BooleanFormula operand) {
        return new Problem("unexpected not");
    }

    @Override
    public Expression visitAnd(List<BooleanFormula> operands) {
        return new Problem("unexpected and");
    }

    @Override
    public Expression visitOr(List<BooleanFormula> operands) {
        return new Problem("unexpected or");
    }

    @Override
    public Expression visitXor(BooleanFormula operand1, BooleanFormula operand2) {
        return new Problem("unexpected xor");
    }

    @Override
    public Expression visitEquivalence(BooleanFormula operand1, BooleanFormula operand2) {
        return new Problem("unexpected equivalence");
    }

    @Override
    public Expression visitImplication(BooleanFormula operand1, BooleanFormula operand2) {
        return new Problem("unexpected implication");
    }

    @Override
    public Expression visitIfThenElse(BooleanFormula condition, BooleanFormula thenFormula, BooleanFormula elseFormula) {
        return new Problem("unexpected if-then-else");
    }

    @Override
    public Expression visitQuantifier(
            QuantifiedFormulaManager.Quantifier quantifier,
            BooleanFormula quantifiedAST,
            List<org.sosy_lab.java_smt.api.Formula> boundVars,
            BooleanFormula body) {
        return new Problem("unexpected quantifier");
    }

    @Override
    public Expression visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
        return new Problem("unexpected atom");
    }
}
