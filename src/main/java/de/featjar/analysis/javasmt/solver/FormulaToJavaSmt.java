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

import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.VariableMap;
import de.featjar.formula.structure.VariableMap.Constant;
import de.featjar.formula.structure.VariableMap.Variable;
import de.featjar.formula.structure.atomic.predicate.Equals;
import de.featjar.formula.structure.atomic.predicate.GreaterEqual;
import de.featjar.formula.structure.atomic.predicate.GreaterThan;
import de.featjar.formula.structure.atomic.predicate.LessEqual;
import de.featjar.formula.structure.atomic.predicate.LessThan;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.BiImplies;
import de.featjar.formula.structure.connective.Implies;
import de.featjar.formula.structure.connective.Not;
import de.featjar.formula.structure.connective.Or;
import de.featjar.formula.structure.term.Add;
import de.featjar.formula.structure.term.Function;
import de.featjar.formula.structure.term.Multiply;
import de.featjar.formula.structure.term.Term;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.RationalFormula;
import org.sosy_lab.java_smt.api.RationalFormulaManager;
import org.sosy_lab.java_smt.api.SolverContext;

/**
 * Class containing functions that are used to translate formulas to java smt.
 *
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public class FormulaToJavaSmt {

    private FormulaManager currentFormulaManager;
    private BooleanFormulaManager currentBooleanFormulaManager;
    private IntegerFormulaManager currentIntegerFormulaManager;
    private RationalFormulaManager currentRationalFormulaManager;
    private boolean isPrincess = false;
    private boolean createVariables = true;

    private VariableMap variableMapping;
    private ArrayList<org.sosy_lab.java_smt.api.Formula> variables = new ArrayList<>();

    public FormulaToJavaSmt(SolverContext context, VariableMap variableMapping) {
        setContext(context);
        this.variableMapping = variableMapping;
        variables = new ArrayList<>(variableMapping.getVariableCount() + 1);
        for (int i = 0; i < (variableMapping.getVariableCount() + 1); i++) {
            variables.add(null);
        }
    }

    public void setContext(SolverContext context) {
        currentFormulaManager = context.getFormulaManager();
        currentBooleanFormulaManager = currentFormulaManager.getBooleanFormulaManager();
        currentIntegerFormulaManager = currentFormulaManager.getIntegerFormulaManager();
        if (context.getSolverName() != Solvers.PRINCESS) { // Princess does not support Rationals
            isPrincess = false;
            currentRationalFormulaManager = currentFormulaManager.getRationalFormulaManager();
        } else {
            isPrincess = true;
        }
    }

    public BooleanFormula nodeToFormula(de.featjar.formula.structure.Formula node) {
        if (node instanceof Not) {
            return createNot(nodeToFormula(node.getChildren().get(0)));
        } else if (node instanceof Or) {
            return createOr(getChildren(node));
        } else if (node instanceof And) {
            return createAnd(getChildren(node));
        } else if (node instanceof BiImplies) {
            return createBiimplies(
                    nodeToFormula(node.getChildren().get(0)),
                    nodeToFormula(node.getChildren().get(1)));
        } else if (node instanceof Implies) {
            return createImplies(
                    nodeToFormula(node.getChildren().get(0)),
                    nodeToFormula(node.getChildren().get(1)));
        } else if (node instanceof Literal) {
            return handleLiteralNode((Literal) node);
        } else if (node instanceof LessThan) {
            return handleLessThanNode((LessThan) node);
        } else if (node instanceof GreaterThan) {
            return handleGreaterThanNode((GreaterThan) node);
        } else if (node instanceof LessEqual) {
            return handleLessEqualNode((LessEqual) node);
        } else if (node instanceof GreaterEqual) {
            return handleGreaterEqualNode((GreaterEqual) node);
        } else if (node instanceof Equals) {
            return handleEqualNode((Equals) node);
        } else {
            throw new RuntimeException("The nodes of type: " + node.getClass() + " are not supported by JavaSmt.");
        }
    }

    private List<BooleanFormula> getChildren(de.featjar.formula.structure.Formula node) {
        return node.getChildren().stream() //
                .map(this::nodeToFormula) //
                .collect(Collectors.toList());
    }

    public BooleanFormula createAnd(List<BooleanFormula> collect) {
        return currentBooleanFormulaManager.and(collect);
    }

    public BooleanFormula createImplies(final BooleanFormula leftChild, final BooleanFormula rightChild) {
        return currentBooleanFormulaManager.implication(leftChild, rightChild);
    }

    public BooleanFormula createBiimplies(final BooleanFormula leftChild, final BooleanFormula rightChild) {
        return currentBooleanFormulaManager.equivalence(leftChild, rightChild);
    }

    public BooleanFormula createOr(List<BooleanFormula> collect) {
        return currentBooleanFormulaManager.or(collect);
    }

    public BooleanFormula createNot(final BooleanFormula childFormula) {
        return currentBooleanFormulaManager.not(childFormula);
    }

    private BooleanFormula handleEqualNode(Equals node) {
        final NumeralFormula leftTerm = termToFormula(node.getChildren().get(0));
        final NumeralFormula rightTerm = termToFormula(node.getChildren().get(1));
        return createEqual(leftTerm, rightTerm);
    }

    public BooleanFormula createEqual(final NumeralFormula leftTerm, final NumeralFormula rightTerm) {
        if (((leftTerm instanceof RationalFormula) || (rightTerm instanceof RationalFormula)) && !isPrincess) {
            return currentRationalFormulaManager.equal(leftTerm, rightTerm);
        } else {
            return currentIntegerFormulaManager.equal((IntegerFormula) leftTerm, (IntegerFormula) rightTerm);
        }
    }

    private BooleanFormula handleGreaterEqualNode(GreaterEqual node) {
        final NumeralFormula leftTerm = termToFormula(node.getChildren().get(0));
        final NumeralFormula rightTerm = termToFormula(node.getChildren().get(1));
        return createGreaterEqual(leftTerm, rightTerm);
    }

    public BooleanFormula createGreaterEqual(final NumeralFormula leftTerm, final NumeralFormula rightTerm) {
        if (((leftTerm instanceof RationalFormula) || (rightTerm instanceof RationalFormula)) && !isPrincess) {
            return currentRationalFormulaManager.greaterOrEquals(leftTerm, rightTerm);
        } else {
            return currentIntegerFormulaManager.greaterOrEquals((IntegerFormula) leftTerm, (IntegerFormula) rightTerm);
        }
    }

    private BooleanFormula handleLessEqualNode(LessEqual node) {
        final NumeralFormula leftTerm = termToFormula(node.getChildren().get(0));
        final NumeralFormula rightTerm = termToFormula(node.getChildren().get(1));
        return createLessEqual(leftTerm, rightTerm);
    }

    public BooleanFormula createLessEqual(final NumeralFormula leftTerm, final NumeralFormula rightTerm) {
        if (((leftTerm instanceof RationalFormula) || (rightTerm instanceof RationalFormula)) && !isPrincess) {
            return currentRationalFormulaManager.lessOrEquals(leftTerm, rightTerm);
        } else {
            return currentIntegerFormulaManager.lessOrEquals((IntegerFormula) leftTerm, (IntegerFormula) rightTerm);
        }
    }

    private BooleanFormula handleGreaterThanNode(GreaterThan node) {
        final NumeralFormula leftTerm = termToFormula(node.getChildren().get(0));
        final NumeralFormula rightTerm = termToFormula(node.getChildren().get(1));
        return createGreaterThan(leftTerm, rightTerm);
    }

    public BooleanFormula createGreaterThan(final NumeralFormula leftTerm, final NumeralFormula rightTerm) {
        if (((leftTerm instanceof RationalFormula) || (rightTerm instanceof RationalFormula)) && !isPrincess) {
            return currentRationalFormulaManager.greaterThan(leftTerm, rightTerm);
        } else {
            return currentIntegerFormulaManager.greaterThan((IntegerFormula) leftTerm, (IntegerFormula) rightTerm);
        }
    }

    private BooleanFormula handleLessThanNode(LessThan node) {
        final NumeralFormula leftTerm = termToFormula(node.getChildren().get(0));
        final NumeralFormula rightTerm = termToFormula(node.getChildren().get(1));
        return createLessThan(leftTerm, rightTerm);
    }

    public BooleanFormula createLessThan(final NumeralFormula leftTerm, final NumeralFormula rightTerm) {
        if (((leftTerm instanceof RationalFormula) || (rightTerm instanceof RationalFormula)) && !isPrincess) {
            return currentRationalFormulaManager.lessThan(leftTerm, rightTerm);
        } else {
            return currentIntegerFormulaManager.lessThan((IntegerFormula) leftTerm, (IntegerFormula) rightTerm);
        }
    }

    private NumeralFormula termToFormula(Term term) {
        if (term instanceof Constant) {
            return createConstant(((Constant) term).getValue());
        } else if (term instanceof Variable) {
            final Variable variable = (Variable) term;
            return handleVariable(variable);
        } else if (term instanceof Function) {
            return handleFunction((Function) term);
        } else {
            throw new RuntimeException("The given term is not supported by JavaSMT: " + term.getClass());
        }
    }

    private NumeralFormula handleFunction(Function function) {
        final NumeralFormula[] children =
                new NumeralFormula[function.getChildren().size()];
        int index = 0;
        for (final Term term : function.getChildren()) {
            children[index++] = termToFormula(term);
        }
        if (function.getType() == Double.class) {
            if (isPrincess) {
                throw new UnsupportedOperationException("Princess does not support variables from type: Double");
            }
            if (function instanceof Add) {
                return currentRationalFormulaManager.add(children[0], children[1]);
            } else if (function instanceof Multiply) {
                return currentRationalFormulaManager.multiply(children[0], children[1]);
            } else {
                throw new RuntimeException(
                        "The given function is not supported by JavaSMT Rational Numbers: " + function.getClass());
            }
        } else if (function.getType() == Long.class) {
            if (function instanceof Add) {
                return currentIntegerFormulaManager.add((IntegerFormula) children[0], (IntegerFormula) children[1]);
            } else if (function instanceof Multiply) {
                return currentIntegerFormulaManager.multiply(
                        (IntegerFormula) children[0], (IntegerFormula) children[1]);
            } else {
                throw new RuntimeException(
                        "The given function is not supported by JavaSMT Rational Numbers: " + function.getClass());
            }
        } else {
            throw new UnsupportedOperationException("Unknown function type: " + function.getType());
        }
    }

    public NumeralFormula createConstant(Object value) {
        if (value instanceof Long) {
            return currentIntegerFormulaManager.makeNumber((long) value);
        } else if (value instanceof Double) {
            if (isPrincess) {
                throw new UnsupportedOperationException("Princess does not support constants from type: Double");
            }
            return currentRationalFormulaManager.makeNumber((double) value);
        } else {
            throw new UnsupportedOperationException("Unknown constant type: " + value.getClass());
        }
    }

    private NumeralFormula handleVariable(Variable variable) {
        final String name = variable.getName();
        final Optional<Formula> map = variableMapping.getVariableIndex(name).map(variables::get);
        if (variable.getType() == Double.class) {
            if (isPrincess) {
                throw new UnsupportedOperationException("Princess does not support variables from type: Double");
            }
            return (NumeralFormula) map.orElseGet(() -> newVariable(name, currentRationalFormulaManager::makeVariable));
        } else if (variable.getType() == Long.class) {
            return (NumeralFormula) map.orElseGet(() -> newVariable(name, currentIntegerFormulaManager::makeVariable));
        } else {
            throw new UnsupportedOperationException("Unknown variable type: " + variable.getType());
        }
    }

    private BooleanFormula handleLiteralNode(Literal literal) {
        if (literal == de.featjar.formula.structure.Formula.TRUE) {
            return currentBooleanFormulaManager.makeTrue();
        } else if (literal == de.featjar.formula.structure.Formula.FALSE) {
            return currentBooleanFormulaManager.makeFalse();
        } else {
            final String name = literal.getName();
            final BooleanFormula variable = (BooleanFormula) variableMapping
                    .getVariableIndex(name)
                    .map(variables::get)
                    .orElseGet(() -> newVariable(name, currentBooleanFormulaManager::makeVariable));
            return literal.isPositive() ? variable : createNot(variable);
        }
    }

    private <T extends org.sosy_lab.java_smt.api.Formula> T newVariable(
            final String name, java.util.function.Function<String, T> variableCreator) {
        if (createVariables) {
            final Integer index = variableMapping.getVariableIndex(name).orElseThrow(RuntimeException::new);
            final T newVariable = variableCreator.apply(name);
            while (variables.size() <= index) {
                variables.add(null);
            }
            variables.set(index, newVariable);
            return newVariable;
        } else {
            throw new RuntimeException(name);
        }
    }

    public VariableMap getVariableMapping() {
        return variableMapping;
    }

    public void setVariableMapping(VariableMap variableMapping) {
        this.variableMapping = variableMapping;
    }

    public ArrayList<org.sosy_lab.java_smt.api.Formula> getVariables() {
        return variables;
    }
}
