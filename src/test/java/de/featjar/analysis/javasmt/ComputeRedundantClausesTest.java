package de.featjar.analysis.javasmt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

import de.featjar.analysis.javasmt.computation.ComputeJavaSMTFormula;
import de.featjar.analysis.javasmt.computation.ComputeRedundantClauses;
import de.featjar.analysis.javasmt.computation.ComputeRedundantClausesIncrementally;
import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Computations;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Not;
import de.featjar.formula.structure.connective.Reference;
import de.featjar.formula.structure.predicate.Equals;
import de.featjar.formula.structure.predicate.GreaterEqual;
import de.featjar.formula.structure.predicate.GreaterThan;
import de.featjar.formula.structure.predicate.LessEqual;
import de.featjar.formula.structure.predicate.LessThan;
import de.featjar.formula.structure.predicate.NotEquals;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;

public class ComputeRedundantClausesTest {

    @BeforeAll
    public static void begin() {
        FeatJAR.testConfiguration().initialize();
    }
    
    @AfterAll
    public static void end() {
        FeatJAR.deinitialize();
    }

    @Test
    public void formulaWithOneVariableHasOneRedundantClause() {
    	final Variable a = new Variable("a", Double.class);
    	final Variable b = new Variable("b", Double.class);
        final Constant constant3 = new Constant(3L);
        
        final GreaterEqual greaterEqualA = new GreaterEqual(a, constant3);
        final LessEqual lessEqualA = new LessEqual(a, constant3);
        final Equals redundantClause = new Equals(a, constant3);
        final And and = new And(greaterEqualA, lessEqualA, redundantClause);
        final Reference formula = new Reference(and);
     
        // IFormula cnf = formula.toCNF().orElseThrow();
        final Result<List<IExpression>> result = Computations.of(formula)
        		.map(ComputeJavaSMTFormula::new)
        		.set(ComputeJavaSMTFormula.SOLVER, Solvers.Z3)
        		.map(ComputeRedundantClauses::new)
        		.computeResult();
        
        assertTrue(result.isPresent(), () -> Problem.printProblems(result.getProblems()));
        List<IExpression> resultRedundantClauses = result.get();       
    }
    
    @Test
    public void formulaWithTwoVariablesHasOneRedundantClause() {
    	final Variable a = new Variable("a", Double.class);
    	final Variable b = new Variable("b", Double.class);
    	
    	final Constant constant2 = new Constant(2L);
        final Constant constant3 = new Constant(3L);
        
        final GreaterThan greaterThanA = new GreaterThan(a, constant3);
        final LessThan lessThanB = new LessThan(b, constant2);
        final Equals equalsAB = new Equals(a, b);
        final Not redundantClause = new Not(equalsAB);
        final And and = new And(greaterThanA, lessThanB, redundantClause);
        final Reference formula = new Reference(and);
        
        // IFormula cnf = formula.toCNF().orElseThrow();
        final Result<List<IExpression>> result = Computations.of(formula)
        		.map(ComputeJavaSMTFormula::new)
        		.set(ComputeJavaSMTFormula.SOLVER, Solvers.Z3)
        		.map(ComputeRedundantClauses::new)
        		.computeResult();
        
        assertTrue(result.isPresent(), () -> Problem.printProblems(result.getProblems()));
        List<IExpression> resultRedundantClauses = result.get();      
    }
    
    @Test
    public void formulaWithTwoVariablesHasTwoRedundantClauses() {
    	final Variable a = new Variable("a", Double.class);
    	final Variable b = new Variable("b", Double.class);
        final Constant constant3 = new Constant(3L);
        
        final GreaterEqual greaterEqualA = new GreaterEqual(a, constant3);
        final LessEqual lessEqualA = new LessEqual(a, constant3);
        final Equals redundantClause1 = new Equals(a, constant3);
        final GreaterThan greaterThanB = new GreaterThan(b, constant3);
        final Equals equalsAB = new Equals(a, b);
        final Not redundantClause2 = new Not(equalsAB);
        final And and = new And(greaterEqualA, lessEqualA, redundantClause1, greaterThanB, redundantClause2);
        final Reference formula = new Reference(and);
        
        // IFormula cnf = formula.toCNF().orElseThrow();
        final Result<List<IExpression>> result = Computations.of(formula)
        		.map(ComputeJavaSMTFormula::new)
        		.set(ComputeJavaSMTFormula.SOLVER, Solvers.Z3)
        		.map(ComputeRedundantClauses::new)
        		.computeResult();
        
        assertTrue(result.isPresent(), () -> Problem.printProblems(result.getProblems()));
        List<IExpression> resultRedundantClauses = result.get();   
    }
}
