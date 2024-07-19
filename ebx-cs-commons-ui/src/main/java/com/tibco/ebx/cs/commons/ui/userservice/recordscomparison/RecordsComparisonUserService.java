/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice.recordscomparison;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ui.selection.TableViewEntitySelection;
import com.orchestranetworks.userservice.UserService;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServicePane;
import com.orchestranetworks.userservice.UserServicePaneContext;
import com.orchestranetworks.userservice.UserServicePaneWriter;
import com.orchestranetworks.userservice.UserServiceProcessEventOutcomeContext;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceValidateContext;

/**
 * RecordsComparisonUserService is a {@link UserService} on {@link TableViewEntitySelection} allowing to display records comparison with the feature {@link RecordComparison}.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class RecordsComparisonUserService implements UserService<TableViewEntitySelection>, UserServicePane {
	final List<Adaptation> comparedItems = new ArrayList<>();
	private UserMessage serviceTitle;
	private boolean headerDisplayed = true;
	private final RecordComparisonOptions recordComparisonOptions;

	/**
	 * Instantiate a new instance of {@link UserService} on {@link TableViewEntitySelection} for displaying records comparison.
	 *
	 * @param pRecordComparisonOptions the options for the RecordComparison
	 * @since 1.8.0
	 * @see RecordComparisonOptions
	 */
	public RecordsComparisonUserService(final RecordComparisonOptions pRecordComparisonOptions) {
		this.recordComparisonOptions = pRecordComparisonOptions;
	}

	@Override
	public UserServiceEventOutcome processEventOutcome(final UserServiceProcessEventOutcomeContext<TableViewEntitySelection> pContext, final UserServiceEventOutcome pEventOutcome) {
		return pEventOutcome;
	}

	/**
	 * Setter for the headerDisplayed allowing display the whole header with the service title or not. Default is true.
	 *
	 * @param pHeaderDisplayed true for displayed, false for not.
	 * @since 1.8.0
	 */
	public void setHeaderDisplayed(final boolean pHeaderDisplayed) {
		this.headerDisplayed = pHeaderDisplayed;
	}

	/**
	 * Setter for the serviceTitle parameter allowing to override the label defined in the UserServiceDeclaration.
	 *
	 * @param serviceTitle the service title.
	 * @since 1.8.0
	 */
	public void setServiceTitle(final UserMessage serviceTitle) {
		this.serviceTitle = serviceTitle;
	}

	@Override
	public void setupDisplay(final UserServiceSetupDisplayContext<TableViewEntitySelection> pContext, final UserServiceDisplayConfigurator pConfigurator) {
		pConfigurator.setHeaderDisplayed(this.headerDisplayed);
		if (this.serviceTitle != null) {
			pConfigurator.setTitle(this.serviceTitle);
		}
		pConfigurator.setLeftButtons(pConfigurator.newCloseButton());
		pConfigurator.setContent(this);
	}

	@Override
	public void setupObjectContext(final UserServiceSetupObjectContext<TableViewEntitySelection> pContext, final UserServiceObjectContextBuilder pBuilder) {
		RequestResult selectedRecordRequestResult = pContext.getEntitySelection().getSelectedRecords().execute();
		try {
			Adaptation selectedRecord = null;
			while ((selectedRecord = selectedRecordRequestResult.nextAdaptation()) != null) {
				this.comparedItems.add(selectedRecord);
			}
		} finally {
			selectedRecordRequestResult.close();
		}
	}

	@Override
	public void validate(final UserServiceValidateContext<TableViewEntitySelection> pContext) {
		// nothing to validate
	}

	@Override
	public void writePane(final UserServicePaneContext pPaneContext, final UserServicePaneWriter pWriter) {
		Locale locale = pWriter.getLocale();

		RecordComparison recordComparison = new RecordComparison(this.comparedItems, this.recordComparisonOptions, locale);

		recordComparison.addComparisonTable(pWriter);
	}
}
