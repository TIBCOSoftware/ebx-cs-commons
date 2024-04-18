package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.Map;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;

/**
 * Procedures interface
 * 
 * @author Mickaël Chevalier
 */
public interface Procedures {

	public static class Create {
//		public static Adaptation execute(
//			final Session session,
//			final AdaptationTable adaptationTable,
//			final Map<Path, Object> pathValueMap) throws OperationException
//		{
//			return Procedures.Create.execute(session, adaptationTable, pathValueMap, true, false);
//		}

//		public static Adaptation execute(
//			final Session session,
//			final AdaptationTable adaptationTable,
//			final Map<Path, Object> pathValueMap,
//			final boolean enableAllPrivileges,
//			final boolean disableTriggerActivation) throws OperationException
//		{
//			CreateRecordProcedure procedure = new CreateRecordProcedure(
//				adaptationTable,
//				pathValueMap);
//			procedure.setAllPrivileges(enableAllPrivileges);
//			procedure.setTriggerActivation(!disableTriggerActivation);
//			procedure.execute(session);
//			return procedure.getCreatedRecord();
//		}

		public static Adaptation execute(final ProcedureContext pContext, final AdaptationTable adaptationTable, final Map<Path, Object> pathValueMap) throws OperationException {
			return Procedures.Create.execute(pContext, adaptationTable, pathValueMap, true, false);
		}

		public static Adaptation execute(final ProcedureContext pContext, final AdaptationTable adaptationTable, final Map<Path, Object> pathValueMap, final boolean enableAllPrivileges,
				final boolean disableTriggerActivation) throws OperationException {
			CreateRecordProcedure procedure = new CreateRecordProcedure(adaptationTable, pathValueMap);
			procedure.setAllPrivileges(enableAllPrivileges);
			procedure.setTriggerActivation(!disableTriggerActivation);
			try {
				procedure.execute(pContext);
			} catch (Exception e) {
				LoggingCategory.getKernel().error("", e);
			}
			return procedure.getCreatedRecord();
		}
	}

	public static class Delete {
//		public static void execute(final Session session, final Adaptation adaptation)
//			throws OperationException
//		{
//			Procedures.Delete.execute(session, adaptation, true, false, false);
//		}

//		public static void execute(
//			final Session session,
//			final Adaptation adaptation,
//			final boolean enableAllPrivileges,
//			final boolean disableTriggerActivation,
//			final boolean deletingChildren) throws OperationException
//		{
//			DeleteRecordProcedure procedure = new DeleteRecordProcedure(adaptation);
//			procedure.setAllPrivileges(enableAllPrivileges);
//			procedure.setTriggerActivation(!disableTriggerActivation);
//			procedure.setDeletingChildren(deletingChildren);
//			procedure.execute(session);
//		}

		public static void execute(final ProcedureContext pContext, final Adaptation adaptation) throws OperationException {
			Delete.execute(pContext, adaptation, true, false, false);
		}

		public static void execute(final ProcedureContext pContext, final Adaptation adaptation, final boolean enableAllPrivileges, final boolean disableTriggerActivation,
				final boolean deletingChildren) throws OperationException {
			DeleteRecordProcedure procedure = new DeleteRecordProcedure(adaptation);
			procedure.setAllPrivileges(enableAllPrivileges);
			procedure.setTriggerActivation(!disableTriggerActivation);
			procedure.setDeletingChildren(deletingChildren);
			try {
				procedure.execute(pContext);
			} catch (Exception e) {
				LoggingCategory.getKernel().error("", e);
			}
		}
	}

	public static class Modify {
//		public static Adaptation execute(
//			final Session session,
//			final Adaptation adaptation,
//			final Map<Path, Object> pathValueMap) throws OperationException
//		{
//			return Procedures.Modify.execute(session, adaptation, pathValueMap, true, false);
//		}
//
//		public static Adaptation execute(
//			final Session session,
//			final Adaptation adaptation,
//			final Map<Path, Object> pathValueMap,
//			final boolean enableAllPrivileges,
//			final boolean disableTriggerActivation) throws OperationException
//		{
//			ModifyValuesProcedure procedure = new ModifyValuesProcedure(adaptation, pathValueMap);
//			procedure.setAllPrivileges(enableAllPrivileges);
//			procedure.setTriggerActivation(!disableTriggerActivation);
//			procedure.execute(session);
//			return procedure.getAdaptation();
//		}

		public static Adaptation execute(final ProcedureContext pContext, final Adaptation adaptation, final Map<Path, Object> pathValueMap) throws OperationException {
			return Procedures.Modify.execute(pContext, adaptation, pathValueMap, true, false);
		}

		public static Adaptation execute(final ProcedureContext pContext, final Adaptation adaptation, final Map<Path, Object> pathValueMap, final boolean enableAllPrivileges,
				final boolean disableTriggerActivation) throws OperationException {
			ModifyValuesProcedure procedure = new ModifyValuesProcedure(adaptation, pathValueMap);
			procedure.setAllPrivileges(enableAllPrivileges);
			procedure.setTriggerActivation(!disableTriggerActivation);
			try {
				procedure.execute(pContext);
			} catch (Exception e) {
				LoggingCategory.getKernel().error("", e);
			}
			return procedure.getAdaptation();
		}
	}

	public static class Duplicate {
//		public static Adaptation execute(
//			final Session session,
//			final Adaptation adaptation,
//			final Map<Path, Object> pathValueMap) throws OperationException
//		{
//			return Procedures.Duplicate.execute(session, adaptation, pathValueMap, true, false);
//		}
//
//		public static Adaptation execute(
//			final Session session,
//			final Adaptation adaptation,
//			final Map<Path, Object> pathValueMap,
//			final boolean enableAllPrivileges,
//			final boolean disableTriggerActivation) throws OperationException
//		{
//			DuplicateRecordProcedure procedure = new DuplicateRecordProcedure(
//				adaptation,
//				pathValueMap);
//			procedure.setAllPrivileges(enableAllPrivileges);
//			procedure.setTriggerActivation(!disableTriggerActivation);
//			procedure.execute(session);
//			return procedure.getCreatedRecord();
//		}

		public static Adaptation execute(final ProcedureContext pContext, final Adaptation adaptation, final Map<Path, Object> pathValueMap) throws OperationException {
			return Procedures.Duplicate.execute(pContext, adaptation, pathValueMap, true, false);
		}

		public static Adaptation execute(final ProcedureContext pContext, final Adaptation adaptation, final Map<Path, Object> pathValueMap, final boolean enableAllPrivileges,
				final boolean disableTriggerActivation) throws OperationException {
			DuplicateRecordProcedure procedure = new DuplicateRecordProcedure(adaptation, pathValueMap);
			procedure.setAllPrivileges(enableAllPrivileges);
			procedure.setTriggerActivation(!disableTriggerActivation);
			try {
				procedure.execute(pContext);
			} catch (Exception e) {
				LoggingCategory.getKernel().error("", e);
			}
			return procedure.getCreatedRecord();
		}
	}
}
