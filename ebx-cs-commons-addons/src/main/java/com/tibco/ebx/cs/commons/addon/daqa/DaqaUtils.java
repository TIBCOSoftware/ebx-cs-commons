package com.tibco.ebx.cs.commons.addon.daqa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.addon.daqa.MatchingState;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.form.UIFormWriter;

/**
 * Class providing methods about matching
 *
 * @author Lionel Luquet
 * @since 1.5.0
 */
public final class DaqaUtils {

	private DaqaUtils() {
		super();
	}

	/**
	 * Get default state to trigger matching
	 * 
	 * @param matchingState the matching state
	 * @return the list of all matching states admitted to trigger matching on the state
	 * @since 1.5.0
	 */
	public static List<MatchingState> getDefaultStateToTriggerMatching(final MatchingState matchingState) {
		List<MatchingState> result = new ArrayList<>();
		result.add(MatchingState.GOLDEN);
		result.add(matchingState);

		return result;
	}

	/**
	 * Get default state to trigger matching (Full mode)
	 * 
	 * @param matchingState the matching state
	 * @return the list of all matching states admitted to trigger matching on the state in full mode
	 * @since 1.5.0
	 */
	public static List<MatchingState> getDefaultStateToTriggerMatchingFullMode(final MatchingState matchingState) {
		List<MatchingState> result = new ArrayList<>();
		if (MatchingState.TO_BE_MATCHED.equals(matchingState)) {
			result.add(MatchingState.TO_BE_MATCHED);
			result.add(MatchingState.GOLDEN);
			result.add(MatchingState.SUSPECT);
			result.add(MatchingState.PIVOT);
		} else if (MatchingState.UNMATCHED.equals(matchingState)) {
			result.add(MatchingState.UNMATCHED);
			result.add(MatchingState.GOLDEN);
			result.add(MatchingState.SUSPECT);
			result.add(MatchingState.PIVOT);
		}

		return result;
	}

	/**
	 * Displays daqa data according to daqa state. If one of the path parameter is null, data associated is not displayed.
	 *
	 * @param pWriter             the writer
	 * @param pRecord             the record displayed
	 * @param pStatePath          the state path
	 * @param pClusterPath        the cluster path
	 * @param pTargetRecordPath   the target record path
	 * @param pMergedRecordsPath  the merged records path (association to define in the model based on the daqa target fk)
	 * @param pSuspectRecordsPath the suspect records path; an association to define in the datamodel with a predicate. For example, you can define the following xpath expression :
	 *                            ./DaqaMetaData/ClusterId=${../DaqaMetaData/ClusterId} and (./DaqaMetaData/State='Suspect' or ./DaqaMetaData/State='Pivot') and
	 *                            not(./Identification/Id=${../Identification/Id})
	 * @throws NullPointerException if the writer, record or state path argument is null.
	 * @since 1.9.0
	 */
	public static void displayDaqaData(final UIFormWriter pWriter, final Adaptation pRecord, final Path pStatePath, final Path pClusterPath, final Path pTargetRecordPath,
			final Path pMergedRecordsPath, final Path pSuspectRecordsPath) throws NullPointerException {
		Objects.requireNonNull(pWriter, "The writer argument shall not be null");
		Objects.requireNonNull(pRecord, "The record argument shall not be null");
		Objects.requireNonNull(pStatePath, "The state path argument shall not be null");

		String state = pRecord.getString(pStatePath);
		boolean isGolden = MatchingState.GOLDEN.getValue().equals(state);
		boolean isPivot = MatchingState.PIVOT.getValue().equals(state);
		boolean isSuspect = MatchingState.SUSPECT.getValue().equals(state);
		boolean isMerged = MatchingState.MERGED.getValue().equals(state);

		pWriter.addFormRow(pStatePath);
		if (pClusterPath != null) {
			pWriter.addFormRow(pClusterPath);
		}

		if (isMerged && pTargetRecordPath != null) {
			pWriter.addFormRow(pTargetRecordPath);
		}

		if ((isGolden || isPivot) && pMergedRecordsPath != null) {
			pWriter.addFormRow(pMergedRecordsPath);
		}

		if ((isPivot || isSuspect) && pSuspectRecordsPath != null) {
			pWriter.addFormRow(pSuspectRecordsPath);
		}
	}
}
