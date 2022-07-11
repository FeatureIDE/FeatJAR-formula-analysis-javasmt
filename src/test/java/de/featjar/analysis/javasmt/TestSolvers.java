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
package de.featjar.analysis.javasmt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.SolverContext;

import de.featjar.util.logging.Logger;
import de.featjar.util.os.OperatingSystem;

public class TestSolvers {

	private void solversWindows() {
		testAvailability(Solvers.MATHSAT5);
		testAvailability(Solvers.PRINCESS);
		testAvailability(Solvers.SMTINTERPOL);
		testAvailability(Solvers.Z3);
	}

	private void solversUnix() {
		testAvailability(Solvers.BOOLECTOR);
		testAvailability(Solvers.CVC4);
		testAvailability(Solvers.MATHSAT5);
		testAvailability(Solvers.PRINCESS);
		testAvailability(Solvers.SMTINTERPOL);
		testAvailability(Solvers.Z3);
	}

	private void solversMac() {
		testAvailability(Solvers.PRINCESS);
		testAvailability(Solvers.SMTINTERPOL);
		testAvailability(Solvers.Z3);
	}

	@Test
	public void solvers() {
		try {
			if (OperatingSystem.IS_UNIX) {
				solversUnix();
			}
			if (OperatingSystem.IS_MAC) {
				solversMac();
			}
			if (OperatingSystem.IS_WINDOWS) {
				solversWindows();
			}
		} catch (final Exception e) {
			Logger.logError(e);
			fail();
		}
	}

	public void testAvailability(Solvers solver) {
		final Configuration config = Configuration.defaultConfiguration();
		final LogManager logger = LogManager.createNullLogManager();
		final ShutdownNotifier notifier = ShutdownNotifier.createDummy();

		try (SolverContext context = SolverContextFactory.createSolverContext(config, logger, notifier, solver)) {
			assertNotNull(context.getVersion());
		} catch (final InvalidConfigurationException e) {
			fail(solver + " not available!");
		}
	}

}
