package de.featjar.analysis.javasmt.computation;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.Formula;

import de.featjar.analysis.javasmt.solver.JavaSMTFormula;
import de.featjar.analysis.javasmt.solver.JavaSMTSolver;
import de.featjar.analysis.javasmt.solver.FormulaToJavaSMT.VariableReference;
import de.featjar.base.computation.Computations;
import de.featjar.base.computation.ComputeConstant;
import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.structure.term.value.Variable;


public class ComputeCore extends AJavaSMTAnalysis<Map<Variable, Object>> {
	public static final Dependency<VariableNamesList> VARIABLES_OF_INTEREST =
			Dependency.newDependency(VariableNamesList.class);

    public ComputeCore(IComputation<? extends JavaSMTFormula> formula) {
        super(
        		formula, 
        		new ComputeConstant<>(new VariableNamesList()));
    }

    protected ComputeCore(AJavaSMTAnalysis<Map<Variable, Object>> other) {
        super(other);
    }

    @Override
    public Result<Map<Variable, Object>> compute(List<Object> dependencyList, Progress progress) {
        JavaSMTSolver solver = initializeSolver(dependencyList);
        Solvers solverName = solver.getSolverFormula().getSolverName();
        
        List<Solvers> compatibleSolvers = Arrays.asList(Solvers.Z3);
        
        if (!(compatibleSolvers.contains(solverName))) {
        	return Result.empty(new UnsupportedOperationException(solverName + " does not support ComputeMaximalRanges."));
        }
        
        List<String> variableNames = solver.getSolverFormula().getVariableMap().getVariableNames();
        for (String variableName : variableNames) {
        	System.out.println(variableName);
        }
        
        List<String> variablesOfInterest = VARIABLES_OF_INTEREST.get(dependencyList);
        List<VariableReference> variablesToJavaSMT = solver.getSolverFormula().getTranslator().getMappings(variablesOfInterest);
        
        Map<Variable, Object> variabelsToMinimalRanges = new HashMap<Variable, Object>();
        Map<Variable, Object> variabelsToMaximalRanges = new HashMap<Variable, Object>();
        for (VariableReference variableToJavaSMT : variablesToJavaSMT) {
        	Formula javaSMTVariable = variableToJavaSMT.getJavaSmtVariable();
        	
        	Object minimalRange = solver.minimize(javaSMTVariable);
        	Object maximalRange = solver.maximize(javaSMTVariable);
        	
        	variabelsToMinimalRanges.put(variableToJavaSMT.getVariable(), minimalRange);
        	variabelsToMaximalRanges.put(variableToJavaSMT.getVariable(), maximalRange);
        }
        
        Map<Variable, Object> coreVariableRanges = new HashMap<Variable, Object>();
        for (Map.Entry<Variable, Object> variableToMinimalRange : variabelsToMinimalRanges.entrySet()) {
        	Variable variable = variableToMinimalRange.getKey();
        	Object minimalRange = variableToMinimalRange.getValue();
        	
        	if (variabelsToMaximalRanges.get(variable).equals(minimalRange)) {
        		coreVariableRanges.put(variable, minimalRange);
        	}
        }
        
        return Result.ofNullable(coreVariableRanges);
    }
}
