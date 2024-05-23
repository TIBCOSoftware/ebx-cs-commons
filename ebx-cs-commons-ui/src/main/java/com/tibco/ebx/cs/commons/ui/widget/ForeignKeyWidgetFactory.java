package com.tibco.ebx.cs.commons.ui.widget;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.UIWidget;
import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;
import com.tibco.ebx.cs.commons.lib.repository.RepositoryUtils;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;
import com.tibco.ebx.cs.commons.ui.util.Presales_UIUtils;

/**
 * <strong>Using non public API</strong><br>
 * <br>
 * Widget factory to mimic the Foreign Key native widget but taking parameter to get the dataspace, dataset and table as well as the value of the field itself representing the Primary Key.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class ForeignKeyWidgetFactory implements UIWidgetFactory<UISimpleCustomWidget> {
	// USING_NON_PUBLIC_API

	private Path dataspacePath;
	private Path datasetPath;
	private Path tablePath;

	/**
	 * Constructor
	 */
	public ForeignKeyWidgetFactory() {
		super();
	}

	/**
	 * Getter for the datasetPath parameter.
	 *
	 * @return the path of the dataset node.
	 * @since 1.8.0
	 */
	public Path getDatasetPath() {
		return this.datasetPath;
	}

	/**
	 * Getter for the dataspacePath parameter.
	 *
	 * @return the path of the dataspace node.
	 * @since 1.8.0
	 */
	public Path getDataspacePath() {
		return this.dataspacePath;
	}

	private Adaptation getRecord(final ValueContext pContext) {
		AdaptationTable table = this.getTable(pContext);
		if (table == null) {
			return null;
		}
		String recordPK = (String) pContext.getValue();
		if (recordPK == null) {
			return null;
		}

		return table.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(recordPK));
	}

	private AdaptationTable getTable(final ValueContext pContext) {
		String dataspaceName = (String) pContext.getValue(this.getDataspacePath());
		if (dataspaceName == null || dataspaceName.equals(CommonsConstants.THIS)) {
			AdaptationHome thisDataspace = pContext.getAdaptationInstance().getHome();
			dataspaceName = thisDataspace.getKey().getName();
		}

		String datasetName = (String) pContext.getValue(this.getDatasetPath());
		if (datasetName == null || datasetName.equals(CommonsConstants.THIS)) {
			Adaptation thisDataset = pContext.getAdaptationInstance();
			datasetName = thisDataset.getAdaptationName().getStringName();
		}

		String tablePathString = (String) pContext.getValue(this.getTablePath());
		if (tablePathString != null && tablePathString.equals(CommonsConstants.THIS)) {
			AdaptationTable thisTable = pContext.getAdaptationTable();
			tablePathString = thisTable.getTablePath().format();
		}

		return RepositoryUtils.getTable(dataspaceName, datasetName, tablePathString);
	}

	/**
	 * Getter for the tablePath parameter.
	 *
	 * @return the path of the table node.
	 * @since 1.8.0
	 */
	public Path getTablePath() {
		return this.tablePath;
	}

	@Override
	public UISimpleCustomWidget newInstance(final WidgetFactoryContext pContext) {

		UISimpleCustomWidget widget = new UISimpleCustomWidget(pContext) {
			private void addDefaultWidget(final WidgetWriter pWriter) {
				UIWidget defaultWidget = pWriter.newBestMatching(Path.SELF);
				pWriter.addWidget(defaultWidget);
			}

			private void addPreviewButton(final WidgetWriter pWriter, final Adaptation pRecord) {
				String jsCommand = Presales_UIUtils.getPopUpLinkCommand(pWriter, pRecord);
				pWriter.add("<button type='button' tabindex='-1' title='preview' class='ebx_FlatButton ebx_IconButton ebx_Open' onclick=\"" + jsCommand + "\">");
				pWriter.add("<span class='ebx_Icon'>&nbsp;</span>");
				pWriter.add("</button>");
			}

			@Override
			public void write(final WidgetWriter pWriter, final WidgetDisplayContext pWidgetContext) {
				ValueContext valueContext = pWidgetContext.getValueContext();
				Adaptation record = ForeignKeyWidgetFactory.this.getRecord(valueContext);

				if (pWidgetContext.isDisplayedInTable()) {
					pWriter.add("<table class='ebx_tableCellWithButtonOnRight_table'>");
					pWriter.add("<tbody>");
					pWriter.add("<tr>");
					pWriter.add("<td class='ebx_tableCellWithButtonOnRight_td_label'>");
					this.addDefaultWidget(pWriter);
					pWriter.add("</td>");
					if (record != null) {
						pWriter.add("<td class='ebx_tableCellWithButtonOnRight_td_button'>");
						this.addPreviewButton(pWriter, record);
						pWriter.add("</td>");
					}
					pWriter.add("</tr>");
					pWriter.add("</tbody>");
					pWriter.add("</table>");
				} else {
					this.addDefaultWidget(pWriter);
					if (record != null) {
						this.addPreviewButton(pWriter, record);
					}
				}
			}

		};
		return widget;
	}

	/**
	 * Setter for the datasetPath parameter. Can be null.
	 *
	 * @param pDatasetPath the path of the dataset node.
	 * @since 1.8.0
	 */
	public void setDatasetPath(final Path pDatasetPath) {
		this.datasetPath = pDatasetPath;
	}

	/**
	 * Setter for the dataspacePath parameter. Can be null.
	 *
	 * @param pDataspacePath the path of the dataspace node.
	 * @since 1.8.0
	 */
	public void setDataspacePath(final Path pDataspacePath) {
		this.dataspacePath = pDataspacePath;
	}

	/**
	 * Setter for the tablePath parameter.
	 *
	 * @param pTablePath the path of the table node.
	 * @since 1.8.0
	 */
	public void setTablePath(final Path pTablePath) {
		this.tablePath = pTablePath;
	}

	@Override
	public void setup(final WidgetFactorySetupContext pContext) {
		SchemaNode schemaNode = pContext.getSchemaNode();

		if (this.dataspacePath != null) {
			SchemaNode dataspaceNode = schemaNode.getNode(this.dataspacePath);
			if (dataspaceNode == null) {
				pContext.addError("The path [" + this.dataspacePath.format() + "] does not exist in the table");
			}
		}

		if (this.datasetPath != null) {
			SchemaNode datasetNode = schemaNode.getNode(this.datasetPath);
			if (datasetNode == null) {
				pContext.addError("The path [" + this.datasetPath.format() + "] does not exist in the table");
			}
		}

		if (this.tablePath != null) {
			SchemaNode tableNode = schemaNode.getNode(this.tablePath);
			if (tableNode == null) {
				pContext.addError("The path [" + this.tablePath.format() + "] does not exist in the table");
			}
		} else {
			pContext.addError("The tablePath parameter shall not be null");
		}
	}
}