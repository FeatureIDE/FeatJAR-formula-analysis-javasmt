package de.featjar.analysis.javasmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Or;
import de.featjar.formula.structure.predicate.Equals;
import de.featjar.formula.structure.predicate.GreaterEqual;
import de.featjar.formula.structure.predicate.GreaterThan;
import de.featjar.formula.structure.predicate.LessEqual;
import de.featjar.formula.structure.predicate.LessThan;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;

public class JavaSMTFormulaGenerator {
	 private final Random random;
	
	public JavaSMTFormulaGenerator(long seed) {
		this.random = new Random(seed);
	}
	
	public IFormula generate(int numVariables, int numConstraints, int numAtomicSets) {
		List<Variable> variables = new ArrayList<>();
        for (int i = 0; i < numVariables; i++) {
            variables.add(new Variable("var_" + i, Double.class));
        }

        List<IFormula> constraints = new ArrayList<>();
        for (int i = 0; i < numConstraints; i++) {
            constraints.add(generateRandomConstraint(variables));
        }
        
        for (int i = 0; i < numAtomicSets; i++) {
            constraints.add(generateAtomicSet(variables));
        }
        
        And formula = new And(constraints);
        return formula;
	}
	
	private IFormula generateRandomConstraint(List<Variable> variables) {
		List<IFormula> literals = new ArrayList<>();
		
		for (int i = 0; i < 3; i++) {
			int varIndex = this.random.nextInt(variables.size());
			Variable variable = variables.get(varIndex);
			
			Constant constant = new Constant(Double.valueOf(this.random.nextInt(20)));
			
			int opType = this.random.nextInt(5);
			IFormula integerToBoolean = null;
			
			switch (opType) {
            case 0:
                integerToBoolean = new GreaterThan(variable, constant);
                break;
            case 1:
            	integerToBoolean = new LessThan(variable, constant);
                break;
            case 2:
            	integerToBoolean = new GreaterEqual(variable, constant);
                break;
            case 3:
            	integerToBoolean = new LessEqual(variable, constant);
                break;
            case 4:
            	integerToBoolean = new Equals(variable, constant); 
                break;
           }
			
		   literals.add(integerToBoolean);
        }
		
		return new Or(literals);
	}
	
	private IFormula generateAtomicSet(List<Variable> variables) {
        	int varIndex1 = this.random.nextInt(variables.size());
			Variable variable1 = variables.get(varIndex1);
			
			int varIndex2 = this.random.nextInt(variables.size());
			Variable variable2 = variables.get(varIndex2);
			
			IFormula atomicSet = new Equals(variable1, variable2);
			
			return atomicSet;
	}
	
}












