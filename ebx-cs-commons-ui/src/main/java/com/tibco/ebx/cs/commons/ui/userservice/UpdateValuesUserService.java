package com.tibco.ebx.cs.commons.ui.userservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.selection.TableViewEntitySelection;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceExtended;
import com.orchestranetworks.userservice.UserServiceInitializeContext;
import com.orchestranetworks.userservice.UserServiceNext;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServiceProcessEventOutcomeContext;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceTransaction;
import com.orchestranetworks.userservice.UserServiceValidateContext;
import com.tibco.ebx.cs.commons.lib.procedure.RecordValuesBean;
import com.tibco.ebx.cs.commons.lib.procedure.UpdateRecordsProcedure;

/**
 * A UserService to update a selection of records with given values. This UserService in on {@link TableViewEntitySelection }.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class UpdateValuesUserService implements UserServiceExtended<TableViewEntitySelection> {
	private final HashMap<Path, Object> values;

	/**
	 * Instantiates a UpdateValuesUserService.
	 *
	 * @param pValues a map representing the paths and the values to apply.
	 * @throws NullPointerException if the pValue argument is null.
	 * @since 1.8.0
	 */
	public UpdateValuesUserService(final HashMap<Path, Object> pValues) throws NullPointerException {
		this.values = Objects.requireNonNull(pValues, "The values argument shall not be null");
	}

	/**
	 * Instantiates a UpdateValuesUserService.
	 *
	 * @param pPathToUpdate  the path to update
	 * @param pValueToUpdate the value to apply
	 * @throws NullPointerException if the pPathToUpdate argument is null.
	 * @since 1.8.0
	 */
	public UpdateValuesUserService(final Path pPathToUpdate, final Object pValueToUpdate) throws NullPointerException {
		Objects.requireNonNull(pPathToUpdate, "The path argument shall not be null");

		this.values = new HashMap<>();
		this.values.put(pPathToUpdate, pValueToUpdate);
	}

	@Override
	public UserServiceEventOutcome initialize(final UserServiceInitializeContext<TableViewEntitySelection> pContext) {
		TableViewEntitySelection entitySelection = pContext.getEntitySelection();
		RequestResult selectedRecordsRequestResult = entitySelection.getSelectedRecords().execute();

		List<RecordValuesBean> recordDefinitions = new ArrayList<>();

		try {
			Adaptation record = null;
			while ((record = selectedRecordsRequestResult.nextAdaptation()) != null) {
				RecordValuesBean bean = new RecordValuesBean(record, this.values);
				recordDefinitions.add(bean);
			}
		} finally {
			selectedRecordsRequestResult.close();
		}

		UpdateRecordsProcedure procedure = new UpdateRecordsProcedure(recordDefinitions);

		UserServiceTransaction transaction = pContext.createTransaction();
		transaction.add(procedure);
		transaction.execute();

		return UserServiceNext.nextClose();
	}

	@Override
	public UserServiceEventOutcome processEventOutcome(final UserServiceProcessEventOutcomeContext<TableViewEntitySelection> pContext, final UserServiceEventOutcome pEventOutcome) {
		return pEventOutcome;
	}

	@Override
	public void setupDisplay(final UserServiceSetupDisplayContext<TableViewEntitySelection> pContext, final UserServiceDisplayConfigurator pConfigurator) {
		// nothing to setup
	}

	@Override
	public void setupObjectContext(final UserServiceSetupObjectContext<TableViewEntitySelection> pContext, final UserServiceObjectContextBuilder pBuilder) {
		// nothing to setup
	}

	@Override
	public void validate(final UserServiceValidateContext<TableViewEntitySelection> pContext) {
		// nothing to validate
	}
}
