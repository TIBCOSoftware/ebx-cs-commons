package com.tibco.ebx.cs.commons.ui.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.selection.DatasetEntitySelection;
import com.orchestranetworks.userservice.ObjectKey;
import com.orchestranetworks.userservice.UserService;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServicePaneContext;
import com.orchestranetworks.userservice.UserServicePaneWriter;
import com.orchestranetworks.userservice.UserServiceProcessEventOutcomeContext;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceValidateContext;
import com.tibco.ebx.cs.commons.beans.generator.exception.BeansTechnicalException;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.DataModel;
import com.tibco.ebx.cs.commons.beans.generator.util.BeansRepositoryUtils;
import com.tibco.ebx.cs.commons.lib.utils.CommonsLogger;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;

/**
 * GenerateDataModelDocumentation User Service
 * 
 * @author Mickaël Chevalier
 */
public class GenerateDataModelDocumentation implements UserService<DatasetEntitySelection> {

	private static final String CR = "\n";

	private final ObjectKey key = ObjectKey.forName("model");

	private final UserServiceEventOutcome step = CustomOutcome.GENERATION_STEP;

	private SchemaNode datasetNode;

	private enum CustomOutcome implements UserServiceEventOutcome {
		CONFIGURATION_STEP, GENERATION_STEP
	}

	/**
	 * Constructor
	 */
	public GenerateDataModelDocumentation() {
		super();
	}

	@Override
	public UserServiceEventOutcome processEventOutcome(final UserServiceProcessEventOutcomeContext<DatasetEntitySelection> pContext, final UserServiceEventOutcome pOutcome) {
		return null;
	}

	@Override
	public void setupDisplay(final UserServiceSetupDisplayContext<DatasetEntitySelection> pContext, final UserServiceDisplayConfigurator pConfigurator) {

		this.datasetNode = pContext.getEntitySelection().getDataset().getSchemaNode();
		if (this.step == CustomOutcome.CONFIGURATION_STEP) {
			pConfigurator.setContent(this::writeConfigurationPane);
		} else {
			pConfigurator.setContent(this::writeGenerationPane);
		}
	}

	private void writeConfigurationPane(final UserServicePaneContext pContext, final UserServicePaneWriter pWriter) {
		// nothing to implement
	}

	private void writeGenerationPane(final UserServicePaneContext pContext, final UserServicePaneWriter pWriter) {
		String ebxHome = System.getProperty("ebx.home");
		if (StringUtils.isBlank(ebxHome)) {
			pWriter.add("<p style='color:red'>System property 'ebx.home' not found.</p>");
			return;
		}
		Optional<String> modelFileName = SchemaUtils.getModelFileName(this.datasetNode.getSchemaLocation());
		String fileName = null;
		if (modelFileName.isPresent()) {
			fileName = modelFileName.get();
		} else {
			fileName = System.currentTimeMillis() + "";
		}
		File file = new File(ebxHome + "/" + fileName + ".md");
		try (FileWriter writer = new FileWriter(file)) {
			for (SchemaNode tableNode : SchemaUtils.getTableNodes(this.datasetNode)) {
				writer.write("# " + tableNode.getLabel(pContext.getSession()) + CR);
				writer.write(CR);
				writer.write("<table>" + CR);
				writer.write("<tr><th>Field</th><th>Description</th><th>Technical name</th></tr>" + CR);
				writer.write("<tbody>" + CR);
				for (SchemaNode terminalNode : SchemaUtils.getTerminalNodes(tableNode.getTableOccurrenceRootNode())) {
					if (terminalNode.getDefaultViewProperties().isHidden() && terminalNode.getDefaultViewProperties().isHiddenInDataServices()) {
						continue;
					}
					writer.write("<tr class=\"odd\">" + CR);
					writer.write("<td>" + terminalNode.getLabel(pContext.getSession()) + "</td>" + CR);
					writer.write("<td>" + CR);
					if (terminalNode.getMinOccurs() > 0) {
						writer.write("<p><sup>This field is mandatory.</sup></p>" + CR);
					}
					String description = terminalNode.getDescription(pContext.getSession());
					if (description != null) {
						writer.write("<p>" + description + "</p>" + CR);
					}
					writer.write("</td>" + CR);
					writer.write("<td>" + terminalNode.getPathInSchema().getLastStep().format() + "</td>" + CR);
					writer.write("</tr>" + CR);
				}
				writer.write("</tbody>" + CR);
				writer.write("</table>" + CR);
				writer.write(CR);
			}
		} catch (IOException ex) {
			pWriter.add("<p style='color:red'>" + ex.getMessage() + "</p>");
		}
		pWriter.add("<p>Documentation exported in file '" + file.getAbsolutePath() + "'.</p>");
		CommonsLogger.getLogger().info("Documentation exported in file '" + file.getAbsolutePath() + "'.");
	}

	@Override
	public void setupObjectContext(final UserServiceSetupObjectContext<DatasetEntitySelection> pContext, final UserServiceObjectContextBuilder pBuilder) {
		DataModel model;
		try {
			model = BeansRepositoryUtils.getDataModel(pContext.getRepository(), pContext.getEntitySelection().getDataset().getSchemaLocation());
		} catch (BeansTechnicalException ex) {
			throw new RuntimeException(ex);
		}
		if (model == null) {
			pBuilder.registerNewRecord(this.key, BeansRepositoryUtils.getDataModelTable(pContext.getRepository()));
		} else {
			pBuilder.registerRecordOrDataSet(this.key, model.getEbxRecord());
		}
	}

	@Override
	public void validate(final UserServiceValidateContext<DatasetEntitySelection> arpContextg0) {
		// nothing to implement
	}

}
