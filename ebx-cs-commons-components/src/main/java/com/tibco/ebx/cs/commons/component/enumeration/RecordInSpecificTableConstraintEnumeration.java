package com.tibco.ebx.cs.commons.component.enumeration;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.Path;

/**
 * When a field in a table represents a foreign key, but for varying reasons, a fk constraint is not desired, this constraint enumeration can be used for selecting a record in a specific foreign
 * table.
 * 
 * @author Mickaël Chevalier
 *
 */
public class RecordInSpecificTableConstraintEnumeration extends AbstractRecordInTableConstraintEnumeration {
	private String branchKey;
	private String datasetName;
	private Path tablePath;

	public Path getTablePath() {
		return tablePath;
	}

	public void setTablePath(final Path tablePath) {
		this.tablePath = tablePath;
	}

	@Override
	public void setup(final ConstraintContext context) {
		// not implemented
	}

	@Override
	public Adaptation getDataSet(final ValueContext context) {
		Adaptation currDataSet = context.getAdaptationInstance();
		if (datasetName != null) {
			AdaptationHome branch = currDataSet.getHome();
			if (branchKey != null) {
				branch = currDataSet.getHome().getRepository().lookupHome(HomeKey.forBranchName(branchKey));
			}
			return branch.findAdaptationOrNull(AdaptationName.forName(datasetName));
		}
		return currDataSet;
	}

	public String getBranchKey() {
		return branchKey;
	}

	public void setBranchKey(final String branchKey) {
		this.branchKey = branchKey;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(final String datasetName) {
		this.datasetName = datasetName;
	}

	@Override
	protected Path getTablePath(final ValueContext context) {
		return tablePath;
	}

}
