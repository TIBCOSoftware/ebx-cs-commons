package com.tibco.ebx.cs.commons.addon.daqa;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.form.UIFormContext;
import com.orchestranetworks.ui.form.UIFormPane;
import com.orchestranetworks.ui.form.UIFormPaneWriter;
import com.orchestranetworks.ui.form.UIFormWriter;

/**
 * Pane to display useful DAQA metadata and appropriate according to DAQA state.
 *
 * @since 1.9.0
 */
public class UIFormDaqaPane implements UIFormPane {

	private final Path mergedRecordsPath;
	private final Path clusterPath;
	private final Path statePath;
	private final Path targetRecordPath;
	private final Path suspectRecordsPath;

	/**
	 * State path is mandatory but other path can be null, and if so, data associated will not be displayed.
	 *
	 * @param pStatePath          the state path
	 * @param pClusterPath        the cluster path
	 * @param pMergedRecordsPath  the merged records path; an association to define in the datamodel using the fk 'targetRecord'
	 * @param pTargetRecordPath   the target record path
	 * @param pSuspectRecordsPath the suspect records path; an association to define in the datamodel by predicate. Example of predicate:
	 *                            <code>./DaqaMetaData/ClusterId=${../DaqaMetaData/ClusterId} and (./DaqaMetaData/State='Suspect' or ./DaqaMetaData/State='Pivot') and not(./Identification/Id=${../Identification/Id})</code>
	 * @see DaqaUtils#displayDaqaData(UIFormWriter, Adaptation, Path, Path, Path, Path, Path)
	 * @since 1.9.0
	 */
	public UIFormDaqaPane(final Path pStatePath, final Path pClusterPath, final Path pMergedRecordsPath, final Path pTargetRecordPath, final Path pSuspectRecordsPath) {
		if (pStatePath == null) {
			throw new IllegalArgumentException("State path can not be null.");
		}

		this.statePath = pStatePath;
		this.clusterPath = pClusterPath;
		this.mergedRecordsPath = pMergedRecordsPath;
		this.targetRecordPath = pTargetRecordPath;
		this.suspectRecordsPath = pSuspectRecordsPath;
	}

	@Override
	public void writePane(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
		Adaptation currentRecord = pContext.getCurrentRecord();
		if (currentRecord == null) {
			return;
		}

		pWriter.startTableFormRow();
		DaqaUtils.displayDaqaData(pWriter, currentRecord, this.statePath, this.clusterPath, this.targetRecordPath, this.mergedRecordsPath, this.suspectRecordsPath);

		pWriter.endTableFormRow();
	}

}
