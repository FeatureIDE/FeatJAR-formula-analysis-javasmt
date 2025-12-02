package de.featjar.analysis.javasmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sosy_lab.common.rationals.Rational;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

import de.featjar.analysis.javasmt.computation.ComputeAtomicSet;
import de.featjar.analysis.javasmt.computation.ComputeCore;
import de.featjar.analysis.javasmt.computation.ComputeJavaSMTFormula;
import de.featjar.analysis.javasmt.computation.VariableNamesList;
import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Computations;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Not;
import de.featjar.formula.structure.predicate.Equals;
import de.featjar.formula.structure.predicate.NotEquals;
import de.featjar.formula.structure.term.value.Variable;

public class ComputeAtomicSetTest {

    @BeforeAll
    public static void begin() {
        FeatJAR.testConfiguration().initialize();
    }
    
    @AfterAll
    public static void end() {
        FeatJAR.deinitialize();
    }

    @Test
    public void formulaHasOneAtomicSet() {
        final Variable a = new Variable("a", Double.class);
        final Variable b = new Variable("b", Double.class);
        final Variable c = new Variable("c", Double.class);
        final Variable d = new Variable("d", Double.class);
        //final Variable e = new Variable("e", Double.class);
        //final Variable f = new Variable("f", Double.class);
        
        final Equals equalsAB = new Equals(a, b);
        final Equals equalsCD = new Equals(c, d);
        //final Equals equalsEF = new Equals(e, f);
        final And formula = new And(equalsAB, new Not(equalsCD));
        
        List<List<Variable>> solutionAtomicSets = new ArrayList<>(); 
        List<Variable> atomicSetAB = Arrays.asList(a, b);
        //List<Variable> atomicSetCD = Arrays.asList(c, d);
        //List<Variable> atomicSetEF = Arrays.asList(e, f);
        solutionAtomicSets.add(atomicSetAB);
        //solutionAtomicSets.add(atomicSetCD);
        //solutionAtomicSets.add(atomicSetEF);
        
        // IFormula cnf = formula.toCNF().orElseThrow();
        final Result<List<List<Variable>>> result = Computations.of(formula)
        		.map(ComputeJavaSMTFormula::new)
        		.set(ComputeJavaSMTFormula.SOLVER, Solvers.Z3)
        		.map(ComputeAtomicSet::new)
        		.computeResult();
        		
        assertTrue(result.isPresent(), () -> Problem.printProblems(result.getProblems()));
        List<List<Variable>> resultAtomicSets = result.get();
        
        assertEquals(solutionAtomicSets, resultAtomicSets);
        
    }
}
