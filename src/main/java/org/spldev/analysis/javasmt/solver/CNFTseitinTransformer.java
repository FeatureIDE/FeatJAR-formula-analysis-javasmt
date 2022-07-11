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
package org.spldev.analysis.javasmt.solver;

import java.util.*;
import java.util.stream.*;

import org.sosy_lab.common.*;
import org.sosy_lab.common.configuration.*;
import org.sosy_lab.common.log.*;
import org.sosy_lab.java_smt.*;
import org.sosy_lab.java_smt.api.*;
import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.formula.structure.term.*;
import org.spldev.formula.structure.term.bool.*;
import org.spldev.formula.structure.transform.*;
import org.spldev.util.job.*;

/**
 * Transforms a formula into CNF using the Tseitin transformation implemented in
 * Z3. Requires Z3 to be installed and libz3/libz3java to be in Java's dynamic
 * linking path.
 */
public class CNFTseitinTransformer implements Transformer {
	private static Configuration config;
	private static LogManager logManager;
	private static SolverContext context;
	private static ShutdownManager shutdownManager;
	private static FormulaManager formulaManager;
	private static BooleanFormulaManager booleanFormulaManager;

	static {
		try {
			config = Configuration.defaultConfiguration();
			logManager = BasicLogManager.create(config);
			shutdownManager = ShutdownManager.create();
			context = SolverContextFactory.createSolverContext(config, logManager, shutdownManager
				.getNotifier(), SolverContextFactory.Solvers.Z3);
			formulaManager = context.getFormulaManager();
			booleanFormulaManager = formulaManager.getBooleanFormulaManager();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Formula execute(Formula formula, InternalMonitor monitor) throws Exception {
		VariableMap variableMap = VariableMap.fromExpression(formula);
		BooleanFormula booleanFormula = formulaManager.applyTactic(new FormulaToJavaSmt(context,
			variableMap).nodeToFormula(formula), Tactic.TSEITIN_CNF);
		return booleanFormulaManager.visit(booleanFormula, new CNFVisitor(booleanFormulaManager, variableMap));
	}

	public static class CNFVisitor extends FormulaVisitor {
		public CNFVisitor(BooleanFormulaManager booleanFormulaManager, VariableMap variableMap) {
			super(booleanFormulaManager, variableMap);
		}

		@Override
		public Formula visitAnd(List<BooleanFormula> operands) {
			return new And(operands.stream().map(operand -> booleanFormulaManager.visit(operand, new ClauseVisitor(
				booleanFormulaManager, variableMap))).collect(Collectors.toList()));
		}

		@Override
		public Formula visitOr(List<BooleanFormula> operands) {
			return new And(new ClauseVisitor(booleanFormulaManager, variableMap).visitOr(operands));
		}

		@Override
		public Formula visitNot(BooleanFormula operand) {
			return new And(new Or(new LiteralVisitor(booleanFormulaManager, variableMap).visitNot(operand)));
		}

		@Override
		public Formula visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
			return new And(new Or(new LiteralVisitor(booleanFormulaManager, variableMap).visitAtom(atom, funcDecl)));
		}
	}

	public static class ClauseVisitor extends FormulaVisitor {
		public ClauseVisitor(BooleanFormulaManager booleanFormulaManager, VariableMap variableMap) {
			super(booleanFormulaManager, variableMap);
		}

		@Override
		public Formula visitOr(List<BooleanFormula> operands) {
			return new Or(operands.stream().map(operand -> booleanFormulaManager.visit(operand, new LiteralVisitor(
				booleanFormulaManager, variableMap))).collect(Collectors.toList()));
		}

		@Override
		public Formula visitNot(BooleanFormula operand) {
			return new Or(new LiteralVisitor(booleanFormulaManager, variableMap).visitNot(operand));
		}

		@Override
		public Formula visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
			return new Or(new LiteralVisitor(booleanFormulaManager, variableMap).visitAtom(atom, funcDecl));
		}
	}

	public static class LiteralVisitor extends FormulaVisitor {
		public LiteralVisitor(BooleanFormulaManager booleanFormulaManager, VariableMap variableMap) {
			super(booleanFormulaManager, variableMap);
		}

		@Override
		public Formula visitNot(BooleanFormula operand) {
			LiteralPredicate literalPredicate = (LiteralPredicate) booleanFormulaManager.visit(operand, this);
			return literalPredicate.flip();
		}

		@Override
		public Formula visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
			Variable<?> variable = variableMap.getVariable(atom.toString()).orElseGet(() -> variableMap
				.addBooleanVariable(atom.toString()).get());
			return new LiteralPredicate((BoolVariable) variable, true);
		}
	}
}
