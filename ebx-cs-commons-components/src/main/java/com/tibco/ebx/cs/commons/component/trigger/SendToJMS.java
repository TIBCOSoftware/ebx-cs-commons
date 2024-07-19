/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.trigger;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.Request;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.TableTrigger;
import com.orchestranetworks.schema.trigger.TriggerSetupContext;
import com.orchestranetworks.service.ExportSpec;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;

/**
 * @author Gilles Mayer
 *
 *         Sends created and modified records as XML to a JMS destination
 */
public class SendToJMS extends TableTrigger {

	private String connectionFactory;
	private String destination;

	private ConnectionFactory jmsConnectionFactory;
	private Context envContext;
	private boolean includesTechnicalData;
	private String viewPublication;
	private boolean checkAccessRules;

	private String exportToXml(final AdaptationTable table, final Adaptation record, final ProcedureContext procedureContext) throws OperationException {
		final Request request = table.createRequest();
		request.setXPathFilter(record.toXPathPredicateString());

		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		final ExportSpec spec = new ExportSpec();
		spec.setRequest(request);
		spec.setCheckAccessRules(this.checkAccessRules);
		spec.setIncludesTechnicalData(this.includesTechnicalData);
		if (this.viewPublication != null && !this.viewPublication.isEmpty()) {
			spec.setViewPublication(this.viewPublication);
		}
		spec.setDestinationStream(out);

		procedureContext.doExport(spec);

		try {
			return out.toString(StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	@Override
	public void handleAfterCreate(final AfterCreateOccurrenceContext aContext) throws OperationException {
		final AdaptationTable table = aContext.getTable();
		final Adaptation record = aContext.getAdaptationOccurrence();
		final ProcedureContext procedureContext = aContext.getProcedureContext();

		this.sendRecordData(table, record, procedureContext);
	}

	@Override
	public void handleAfterModify(final AfterModifyOccurrenceContext aContext) throws OperationException {
		final AdaptationTable table = aContext.getTable();
		final Adaptation record = aContext.getAdaptationOccurrence();
		final ProcedureContext procedureContext = aContext.getProcedureContext();

		this.sendRecordData(table, record, procedureContext);
	}

	private void sendRecordData(final AdaptationTable table, final Adaptation record, final ProcedureContext procedureContext) throws OperationException {
		try (Connection connection = this.jmsConnectionFactory.createConnection();
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer((Destination) this.envContext.lookup(this.destination))) {
			TextMessage message = session.createTextMessage(this.exportToXml(table, record, procedureContext));
			producer.send(message);
		} catch (NamingException | JMSException e) {
			throw OperationException.createError(e);
		}
	}

	public void setCheckAccessRules(final boolean checkAccessRules) {
		this.checkAccessRules = checkAccessRules;
	}

	public void setConnectionFactory(final String connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public void setDestination(final String destination) {
		this.destination = destination;
	}

	public void setIncludesTechnicalData(final boolean includesTechnicalData) {
		this.includesTechnicalData = includesTechnicalData;
	}

	@Override
	public void setup(final TriggerSetupContext context) {
		if (this.connectionFactory == null || this.destination == null) {
			context.addError("connectionFactory and queue are required");
		}

		try {
			InitialContext initCtx = new InitialContext();
			this.envContext = (Context) initCtx.lookup("java:comp/env");
			this.jmsConnectionFactory = (ConnectionFactory) this.envContext.lookup(this.connectionFactory);

		} catch (NamingException e) {
			context.addError("Cannot setup trigger " + e, e);
		}
	}

	public void setViewPublication(final String viewPublication) {
		this.viewPublication = viewPublication;
	}

}
