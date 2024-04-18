package com.tibco.ebx.cs.commons.lib.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import com.orchestranetworks.scheduler.ScheduledExecutionContext;

/**
 * Utility class aims to be used in scheduled task.
 *
 * @author Mickaël Chevalier
 */
public final class SchedulerUtils {

	/**
	 *
	 * Execute a command line.
	 *
	 * @param pContext     the context
	 * @param pCommandLine the command line
	 */
	public static void executeCommandLine(final ScheduledExecutionContext pContext, final String pCommandLine) {
		CommandLine command = CommandLine.parse(pCommandLine);
		DefaultExecutor executor = DefaultExecutor.builder().get();
		int exitValue = 0;
		try {
			exitValue = executor.execute(command);
		} catch (Exception ex) {
			pContext.addExecutionInformation("Failed to run command line '" + pCommandLine + "'.");
			pContext.addExecutionInformation(ex.getLocalizedMessage());
			return;
		}
		if (exitValue < 0) {
			pContext.addExecutionInformation("Failed to run command line '" + pCommandLine + "'.");
		} else {
			pContext.addExecutionInformation("Command line '" + pCommandLine + "' ran successfully.");
		}
	}

	private SchedulerUtils() {
		super();
	}
}
