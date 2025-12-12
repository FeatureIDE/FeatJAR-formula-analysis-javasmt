package de.featjar.analysis.javasmt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

import de.featjar.analysis.javasmt.computation.ComputeJavaSMTFormula;
import de.featjar.analysis.javasmt.computation.ComputeRedundantClauses;
import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Computations;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.predicate.LessThan;
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
    public void formulaHasRedundantClauses() {
		
//		  final Variable a = new Variable("a", Double.class); final Variable b = new
//		  Variable("b", Double.class); final Variable c = new Variable("c",
//		  Double.class); final Variable d = new Variable("d", Double.class);
//		  
//		  final Equals equalsAB = new Equals(a, b); final Equals equalsCD = new
//		  Equals(c, d); final And formula = new And(equalsAB, new Not(equalsCD));
//		  
//		  List<List<Variable>> solutionAtomicSets = new ArrayList<>(); List<Variable>
//		  atomicSetAB = Arrays.asList(a, b); solutionAtomicSets.add(atomicSetAB);
		 
        
    	final Variable a = new Variable("a", Double.class);
        final Variable b = new Variable("b", Double.class);
        final Constant constant3 = new Constant(3L);
        final Constant constant7 = new Constant(7L);
        
        final LessThan lessThanA = new LessThan(a, constant3);
        final LessThan lessThanB = new LessThan(b, constant7);
        final And formula = new And(lessThanA, lessThanB);
     
        // IFormula cnf = formula.toCNF().orElseThrow();
        final Result<List<List<Variable>>> result = Computations.of(formula)
        		.map(ComputeJavaSMTFormula::new)
        		.set(ComputeJavaSMTFormula.SOLVER, Solvers.Z3)
        		.map(ComputeRedundantClauses::new)
        		.computeResult();
        
        assertTrue(result.isPresent(), () -> Problem.printProblems(result.getProblems()));
        List<List<Variable>> resultRedundantClauses = result.get();
        
        //assertEquals(solutionAtomicSets, resultAtomicSets);
        
    }
}
