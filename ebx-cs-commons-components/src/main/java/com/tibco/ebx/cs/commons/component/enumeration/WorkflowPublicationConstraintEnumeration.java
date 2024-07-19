/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.workflow.PublishedProcess;
import com.orchestranetworks.workflow.PublishedProcessKey;
import com.orchestranetworks.workflow.WorkflowEngine;
import com.tibco.ebx.cs.commons.lib.message.Messages;

/**
 * Constraint enumeration to list enabled workflow publications.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class WorkflowPublicationConstraintEnumeration implements ConstraintEnumeration<String> {
	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// no implementation
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		Repository repository = pContext.getHome().getRepository();
		Map<String, UserMessage> processPublications = this.getProcessPublications(repository);

		UserMessage label = processPublications.get(pValue);
		if (label == null) {
			return pValue;
		}
		return label.formatMessage(pLocale);
	}

	private Map<String, UserMessage> getProcessPublications(final Repository pRepository) {
		WorkflowEngine workflowEngine = WorkflowEngine.getFromRepository(pRepository, null);

		List<PublishedProcessKey> keys = workflowEngine.getPublishedKeys();

		HashMap<String, UserMessage> processPublications = new HashMap<>();
		for (PublishedProcessKey key : keys) {
			PublishedProcess publishedProcess = workflowEngine.getPublishedProcess(key);
			String publicationName = publishedProcess.getPublicationName();
			UserMessage label = publishedProcess.getLabel();

			processPublications.put(publicationName, label);
		}

		return processPublications;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		Repository repository = pContext.getHome().getRepository();
		Map<String, UserMessage> processPublications = this.getProcessPublications(repository);

		return new ArrayList<>(processPublications.keySet());
	}

	@Override
	public void setup(final ConstraintContext pContext) {
		// no implementation
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.get(this.getClass(), pLocale, "WorkflowPublicationConstraintEnumeration.userDocumentation");
	}
}
