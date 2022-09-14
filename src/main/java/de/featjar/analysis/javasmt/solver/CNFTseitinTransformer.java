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
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.tmp.TermMap;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.transform.Transformer;
import de.featjar.base.task.Monitor;
import java.util.List;
import java.util.stream.Collectors;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.FunctionDeclaration;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.Tactic;

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
            context = SolverContextFactory.createSolverContext(
                    config, logManager, shutdownManager.getNotifier(), SolverContextFactory.Solvers.Z3);
            formulaManager = context.getFormulaManager();
            booleanFormulaManager = formulaManager.getBooleanFormulaManager();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Expression execute(Expression expression, Monitor monitor) throws Exception {
        TermMap termMap = expression.getTermMap().orElseGet(TermMap::new);
        BooleanFormula booleanFormula = formulaManager.applyTactic(
                new FormulaToJavaSmt(context, termMap).nodeToFormula(expression), Tactic.TSEITIN_CNF);
        return booleanFormulaManager.visit(booleanFormula, new CNFVisitor(booleanFormulaManager, termMap));
    }

    public static class CNFVisitor extends FormulaVisitor {
        public CNFVisitor(BooleanFormulaManager booleanFormulaManager, TermMap termMap) {
            super(booleanFormulaManager, termMap);
        }

        @Override
        public Expression visitAnd(List<BooleanFormula> operands) {
            return new And(operands.stream()
                    .map(operand ->
                            booleanFormulaManager.visit(operand, new ClauseVisitor(booleanFormulaManager, termMap)))
                    .collect(Collectors.toList()));
        }

        @Override
        public Expression visitOr(List<BooleanFormula> operands) {
            return new And(new ClauseVisitor(booleanFormulaManager, termMap).visitOr(operands));
        }

        @Override
        public Expression visitNot(BooleanFormula operand) {
            return new And(new Or(new LiteralVisitor(booleanFormulaManager, termMap).visitNot(operand)));
        }

        @Override
        public Expression visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
            return new And(new Or(new LiteralVisitor(booleanFormulaManager, termMap).visitAtom(atom, funcDecl)));
        }
    }

    public static class ClauseVisitor extends FormulaVisitor {
        public ClauseVisitor(BooleanFormulaManager booleanFormulaManager, TermMap termMap) {
            super(booleanFormulaManager, termMap);
        }

        @Override
        public Expression visitOr(List<BooleanFormula> operands) {
            return new Or(operands.stream()
                    .map(operand -> booleanFormulaManager.visit(
                            operand, new LiteralVisitor(booleanFormulaManager, termMap)))
                    .collect(Collectors.toList()));
        }

        @Override
        public Expression visitNot(BooleanFormula operand) {
            return new Or(new LiteralVisitor(booleanFormulaManager, termMap).visitNot(operand));
        }

        @Override
        public Expression visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
            return new Or(new LiteralVisitor(booleanFormulaManager, termMap).visitAtom(atom, funcDecl));
        }
    }

    public static class LiteralVisitor extends FormulaVisitor {
        public LiteralVisitor(BooleanFormulaManager booleanFormulaManager, TermMap termMap) {
            super(booleanFormulaManager, termMap);
        }

        @Override
        public Expression visitNot(BooleanFormula operand) {
            Literal literalPredicate = (Literal) booleanFormulaManager.visit(operand, this);
            return literalPredicate.invert();
        }

        @Override
        public Expression visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
            return termMap.createLiteral(atom.toString());
        }
    }
}
