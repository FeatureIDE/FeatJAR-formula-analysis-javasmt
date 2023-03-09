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

import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.api.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Transforms a formula into CNF using the Tseitin transformation implemented in
 * Z3. Requires Z3 to be installed and libz3/libz3java to be in Java's dynamic
 * linking path.
 */
public class CNFTseitinTransformer { // implements IComputation<IFormula> {
    private static Configuration config;
    private static LogManager logManager;
    private static SolverContext context;
    private static ShutdownManager shutdownManager;
    private static FormulaManager formulaManager;
    private static BooleanFormulaManager booleanFormulaManager;

    // TODO: do this inside the JavaSMTSolver constructor (ie. use extension loader)
    //    static {
    //        try {
    //            config = Configuration.defaultConfiguration();
    //            logManager = BasicLogManager.create(config);
    //            shutdownManager = ShutdownManager.create();
    //            context = SolverContextFactory.createSolverContext(
    //                    config, logManager, shutdownManager.getNotifier(), SolverContextFactory.Solvers.Z3);
    //            formulaManager = context.getFormulaManager();
    //            booleanFormulaManager = formulaManager.getBooleanFormulaManager();
    //        } catch (InvalidConfigurationException e) {
    //            e.printStackTrace();
    //        }
    //    }

//    @Override
//    public IExpression execute(IExpression expression, Progress progress) throws Exception {
//        BooleanFormula booleanFormula = formulaManager.applyTactic(
//                new FormulaToJavaSmt(context).nodeToFormula(expression), Tactic.TSEITIN_CNF);
//        return booleanFormulaManager.visit(booleanFormula, new CNFVisitor(booleanFormulaManager));
//    }

    public static class CNFVisitor extends FormulaVisitor {
        public CNFVisitor(BooleanFormulaManager booleanFormulaManager) {
            super(booleanFormulaManager);
        }

        @Override
        public IFormula visitAnd(List<BooleanFormula> operands) {
            return new And(operands.stream()
                    .map(operand ->
                            (IFormula) booleanFormulaManager.visit(operand, new ClauseVisitor(booleanFormulaManager)))
                    .collect(Collectors.toList()));
        }

        @Override
        public IFormula visitOr(List<BooleanFormula> operands) {
            return new And(new ClauseVisitor(booleanFormulaManager).visitOr(operands));
        }

        @Override
        public IFormula visitNot(BooleanFormula operand) {
            return new And(new Or(new LiteralVisitor(booleanFormulaManager).visitNot(operand)));
        }

        @Override
        public IFormula visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
            return new And(new Or(new LiteralVisitor(booleanFormulaManager).visitAtom(atom, funcDecl)));
        }
    }

    public static class ClauseVisitor extends FormulaVisitor {
        public ClauseVisitor(BooleanFormulaManager booleanFormulaManager) {
            super(booleanFormulaManager);
        }

        @Override
        public IFormula visitOr(List<BooleanFormula> operands) {
            return new Or(operands.stream()
                    .map(operand ->
                            (IFormula) booleanFormulaManager.visit(operand, new LiteralVisitor(booleanFormulaManager)))
                    .collect(Collectors.toList()));
        }

        @Override
        public IFormula visitNot(BooleanFormula operand) {
            return new Or(new LiteralVisitor(booleanFormulaManager).visitNot(operand));
        }

        @Override
        public IFormula visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
            return new Or(new LiteralVisitor(booleanFormulaManager).visitAtom(atom, funcDecl));
        }
    }

    public static class LiteralVisitor extends FormulaVisitor {
        public LiteralVisitor(BooleanFormulaManager booleanFormulaManager) {
            super(booleanFormulaManager);
        }

        @Override
        public IFormula visitNot(BooleanFormula operand) {
            Literal literalPredicate = (Literal) booleanFormulaManager.visit(operand, this);
            return literalPredicate.invert();
        }

        @Override
        public IFormula visitAtom(BooleanFormula atom, FunctionDeclaration<BooleanFormula> funcDecl) {
            return new Literal(atom.toString());
        }
    }
}
