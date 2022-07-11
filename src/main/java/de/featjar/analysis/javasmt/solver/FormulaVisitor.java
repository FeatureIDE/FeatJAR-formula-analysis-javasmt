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
package de.featjar.analysis.javasmt.solver;

import java.util.*;

import de.featjar.formula.structure.atomic.literal.ErrorLiteral;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import org.sosy_lab.java_smt.api.*;
import org.sosy_lab.java_smt.api.visitors.*;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.literal.*;

public abstract class FormulaVisitor implements BooleanFormulaVisitor<Formula> {
	protected final BooleanFormulaManager booleanFormulaManager;
	protected final VariableMap variableMap;

	public FormulaVisitor(BooleanFormulaManager booleanFormulaManager, VariableMap variableMap) {
		this.booleanFormulaManager = booleanFormulaManager;
		this.variableMap = variableMap;
	}

	@Override
	public Formula visitConstant(boolean value) {
		return new ErrorLiteral("unexpected constant");
	}

	@Override
	public Formula visitBoundVar(BooleanFormula var, int deBruijnIdx) {
		return new ErrorLiteral("unexpected bound var");
	}

	@Override
	public Formula visitNot(BooleanFormula operand) {
		return new ErrorLiteral("unexpected not");
	}

	@Override
	public Formula visitAnd(List<BooleanFormula> operands) {
		return new ErrorLiteral("unexpected and");
	}

	@Override
	public Formula visitOr(List<BooleanFormula> operands) {
		return new ErrorLiteral("unexpected or");
	}

	@Override
	public Formula visitXor(BooleanFormula operand1, BooleanFormula operand2) {
		return new ErrorLiteral("unexpected xor");
	}

	@Override
	public Formula visitEquivalence(BooleanFormula operand1, BooleanFormula operand2) {
		return new ErrorLiteral("unexpected equivalence");
	}

	@Override
	public Formula visitImplication(BooleanFormula operand1, BooleanFormula operand2) {
		return new ErrorLiteral("unexpected implication");
	}

	@Override
	public Formula visitIfThenElse(BooleanFormula condition, BooleanFormula thenFormula,
		BooleanFormula elseFormula) {
		return new ErrorLiteral("unexpected if-then-else");
	}

	@Override
	public Formula visitQuantifier(QuantifiedFormulaManager.Quantifier quantifier,
		BooleanFormula quantifiedAST, List<org.sosy_lab.java_smt.api.Formula> boundVars, BooleanFormula body) {
		return new ErrorLiteral("unexpected quantifier");
	}

	@Override
	public Formula visitAtom(BooleanFormula atom,
		FunctionDeclaration<BooleanFormula> funcDecl) {
		return new ErrorLiteral("unexpected atom");
	}
}
