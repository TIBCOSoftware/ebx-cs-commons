/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ProcedureResult;
import com.orchestranetworks.service.ProgrammaticService;
import com.orchestranetworks.service.Session;

/**
 * Abstract class extended by specific procedures. Manage reset of features activation state after execution. Offer a shortcut to execute it via a ProgrammaticService.
 * 
 * @author Mickaël Chevalier
 */
public abstract class GenericProcedure implements Procedure {

	/** All privileges */
	private boolean allPrivileges = false;

	/** Trigger activation. */
	private boolean triggerActivation = true;

	/** History activation. */
	private boolean historyActivation = true;

	/** History activation. */
	private boolean databaseHistoryActivation = true;

	/** Blocking constraint disabled. */
	private boolean blockingConstraintDisabled = false;

	/**
	 * Execute the specific job of a procedure.
	 *
	 * @param pContext the context
	 * @throws Exception the exception
	 */
	protected abstract void doExecute(final ProcedureContext pContext) throws Exception;

	/*
	 * @see com.orchestranetworks.service.Procedure#execute(com.orchestranetworks.service.ProcedureContext)
	 */
	@Override
	public void execute(final ProcedureContext pContext) throws Exception {
		boolean originalPrivileges = pContext.isAllPrivileges();
		pContext.setAllPrivileges(this.allPrivileges);

		boolean originalTriggerActivation = pContext.isTriggerActivation();
		pContext.setTriggerActivation(this.triggerActivation);

		boolean originalHistoryActivation = pContext.isHistoryActivation();
		pContext.setHistoryActivation(this.historyActivation);

		boolean originalDatabaseHistoryActivation = pContext.isDatabaseHistoryActivation();
		pContext.setDatabaseHistoryActivation(this.databaseHistoryActivation);

		boolean originalBlockingConstraintDisabled = pContext.isBlockingConstraintsDisabled();
		pContext.setBlockingConstraintsDisabled(this.blockingConstraintDisabled);

		this.doExecute(pContext);

		pContext.setAllPrivileges(originalPrivileges);
		pContext.setTriggerActivation(originalTriggerActivation);
		pContext.setHistoryActivation(originalHistoryActivation);
		pContext.setDatabaseHistoryActivation(originalDatabaseHistoryActivation);
		pContext.setBlockingConstraintsDisabled(originalBlockingConstraintDisabled);
	}

	/**
	 * Execute with programatic service.
	 *
	 * @param pHome    the home
	 * @param pSession the session
	 * @return the procedure result
	 */
	public ProcedureResult executeWithProgrammaticService(final AdaptationHome pHome, final Session pSession) {
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pHome);
		return srv.execute(this);
	}

	/**
	 * Checks if is all privileges are granted.
	 *
	 * @return true, if is all privileges
	 */
	public boolean isAllPrivileges() {
		return this.allPrivileges;
	}

	/**
	 * Checks if is blocking constraint are disabled.
	 *
	 * @return true, if is blocking constraint disabled
	 */
	public boolean isBlockingConstraintDisabled() {
		return this.blockingConstraintDisabled;
	}

	/**
	 * Checks if is database history is active.
	 *
	 * @return true, if database history is active.
	 */
	public boolean isDatabaseHistoryActivation() {
		return this.databaseHistoryActivation;
	}

	/**
	 * Checks if is history is active.
	 *
	 * @return true, if history is active.
	 */
	public boolean isHistoryActivation() {
		return this.historyActivation;
	}

	/**
	 * Checks if is triggers are active.
	 *
	 * @return true, if is triggers are active
	 */
	public boolean isTriggerActivation() {
		return this.triggerActivation;
	}

	/**
	 * Sets all privileges.
	 *
	 * @param allPrivileges the new all privileges
	 */
	public void setAllPrivileges(final boolean allPrivileges) {
		this.allPrivileges = allPrivileges;
	}

	/**
	 * Sets the blocking constraint disabled.
	 *
	 * @param blockingConstraintDisabled the new blocking constraint disabled
	 */
	public void setBlockingConstraintDisabled(final boolean blockingConstraintDisabled) {
		this.blockingConstraintDisabled = blockingConstraintDisabled;
	}

	/**
	 * Sets the database history activation.
	 *
	 * @param databaseHistoryActivation the new database history activation
	 */
	public void setDatabaseHistoryActivation(final boolean databaseHistoryActivation) {
		this.databaseHistoryActivation = databaseHistoryActivation;
	}

	/**
	 * Sets the history activation.
	 *
	 * @param historyActivation the new history activation
	 */
	public void setHistoryActivation(final boolean historyActivation) {
		this.historyActivation = historyActivation;
	}

	/**
	 * Sets the trigger activation.
	 *
	 * @param triggerActivation the new trigger activation
	 */
	public void setTriggerActivation(final boolean triggerActivation) {
		this.triggerActivation = triggerActivation;
	}

}
