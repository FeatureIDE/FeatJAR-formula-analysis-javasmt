/*
 * Copyright (C) 2025 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-base.
 *
 * base is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * base is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with base. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-base> for further information.
 */

package de.featjar.analysis.javasmt.cli;

import java.util.List;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.structure.IExpression;

/**
 * Serializes a list of IExpression objects as text, as it is returned by {@link Expression#print()}.
 *
 * @param <T> the type of the read/written object
 *
 * @author Sebastian Krieter
 * @author Klara Surmeier
 */

public class ExpressionTextFormat<T> implements IFormat<T> {

    @Override
    public String getName() {
        return "Text";
    }

    @Override
    public String getFileExtension() {
        return "txt";
    }

    @Override
    public boolean supportsWrite() {
        return true;
    }

    @Override
    public Result<String> serialize(T object) {
        List<IExpression> expressions = (List<IExpression>) object;
    		
        String outputString = "";
    	for (IExpression expression : expressions) {
    		String expressionString = expression.print();
    		outputString += expressionString + "\n";
    	}
    	
    	return Result.of(outputString);
    }
}


