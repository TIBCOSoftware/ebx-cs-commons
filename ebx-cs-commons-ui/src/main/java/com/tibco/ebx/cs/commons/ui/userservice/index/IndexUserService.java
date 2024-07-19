/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice.index;

import com.onwbp.adaptation.Request;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.selection.TableViewEntitySelection;
import com.orchestranetworks.userservice.UserService;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServiceProcessEventOutcomeContext;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceValidateContext;

/**
 * IndexUserService is a {@link UserService} on {@link TableViewEntitySelection} to display table's records in an "index" kind of view. Records are organized by label and a description can be
 * displayed as well.<br>
 * <br>
 * <strong>Note</strong>: This service is not based on record selection but get all records of the table.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class IndexUserService implements UserService<TableViewEntitySelection> {
	private final Path labelPath;
	private final Path descriptionPath;
	private boolean headerDisplayed = true;
	private boolean colorIndexTitle = true;
	private boolean colorIndexSlider = true;
	private UserMessage serviceTitle;

	/**
	 * Instantiate IndexUserService with the required arguments: the label path (the description path is optional, can be null).<br>
	 * Check the setters for fine-tuning the service.<br>
	 * <br>
	 * <strong>Note</strong>: As for Presales activities, this service intentionally doesn't care of compatibility with old browser and uses recent (and sometimes experimental) features of HTML, JS
	 * and CSS.<br>
	 * <strong>Note</strong>: Might not be optimized for large volume.
	 *
	 * @param pLabelPath       the path in the record to get the label (Mandatory).
	 * @param pDescriptionPath the path in the record to get a description (Optional).
	 * @throws IllegalArgumentException if the label path argument is null.
	 * @since 1.8.0
	 */
	public IndexUserService(final Path pLabelPath, final Path pDescriptionPath) throws IllegalArgumentException {
		if (pLabelPath == null) {
			throw new IllegalArgumentException("The label path argument shall not be null");
		}
		this.labelPath = pLabelPath;
		this.descriptionPath = pDescriptionPath;
	}

	@Override
	public UserServiceEventOutcome processEventOutcome(final UserServiceProcessEventOutcomeContext<TableViewEntitySelection> pContext, final UserServiceEventOutcome pEventOutcome) {
		return pEventOutcome;
	}

	/**
	 * Setter for the colorIndexSlider parameter defining if the slider index is colored (true) or not (false). Default is true.
	 *
	 * @param pColorIndexSlider true for colored, false for not.
	 * @since 1.8.0
	 */
	public void setColorIndexSlider(final boolean pColorIndexSlider) {
		this.colorIndexSlider = pColorIndexSlider;
	}

	/**
	 * Setter for the colorIndexTitle parameter defining if the index title is colored (true) or not (false). Default is true.
	 *
	 * @param pColorIndexTitle true for colored, false for not.
	 * @since 1.8.0
	 */
	public void setColorIndexTitle(final boolean pColorIndexTitle) {
		this.colorIndexTitle = pColorIndexTitle;
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
	 * Setter for the serviceTitle parameter allowing to overwrite the label defined in the UserServiceDeclaration.
	 *
	 * @param pServiceTitle the service title.
	 * @since 1.8.0
	 */
	public void setServiceTitle(final UserMessage pServiceTitle) {
		this.serviceTitle = pServiceTitle;
	}

	@Override
	public void setupDisplay(final UserServiceSetupDisplayContext<TableViewEntitySelection> pContext, final UserServiceDisplayConfigurator pConfigurator) {
		pConfigurator.setHeaderDisplayed(this.headerDisplayed);
		if (this.serviceTitle != null) {
			pConfigurator.setTitle(this.serviceTitle);
		}

		Request request = pContext.getEntitySelection().getAllRecords();

		IndexUserServicePane indexUserServicePane = new IndexUserServicePane(request, this.labelPath, this.descriptionPath);
		indexUserServicePane.setColorIndexSlider(this.colorIndexSlider);
		indexUserServicePane.setColorIndexTitle(this.colorIndexTitle);
		pConfigurator.setContent(indexUserServicePane);

		pConfigurator.setLeftButtons(pConfigurator.newCloseButton());
	}

	@Override
	public void setupObjectContext(final UserServiceSetupObjectContext<TableViewEntitySelection> pContext, final UserServiceObjectContextBuilder pBuilder) {
		// no implementation
	}

	@Override
	public void validate(final UserServiceValidateContext<TableViewEntitySelection> pContext) {
		// no implementation
	}
}
