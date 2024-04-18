/*
 *
 */
package com.tibco.ebx.cs.commons.ui.userservice.recordscomparison;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.SchemaNodeInformation;
import com.orchestranetworks.ui.ResourceType;
import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UICSSClasses;
import com.orchestranetworks.ui.UIComponentWriter;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.ui.util.Presales_UIUtils;

/**
 * Compare multiple records of the same schema. It allows to get the results of
 * the comparison and/or display the result table.
 *
 * @author Aurélien Ticot
 * @see #addComparisonTable
 * @see #getComparisonResult
 * @since 1.0.0
 */
public final class RecordComparison {
	private static final String JS_HIDE_FUNCTION_NAME = "hide";
	private static final String JS_SHOWALL_FUNCTION_NAME = "showAll";
	private static final String HTML_ID_TABLE_CONTAINER = "comparisonTableContainer";
	private static final String HTML_CLASS_TABLECONTAINER = "tableContainer";
	private static final String HTML_CLASS_TABLEHEADER = "tableHeader";
	private static final String HTML_CLASS_TABLESUBCONTAINER = "tableSubContainer";
	private static final String HTML_CLASS_PRODUCTCOMPARETABLE = "productCompareTable";
	private static final String HTML_CLASS_GROUPETR = "groupTr";
	private static final String HTML_CLASS_TVEVEN = "tvEven";
	private static final String HTML_CLASS_TVODD = "tvOdd";
	private static final String HTML_CLASS_COMPARISONEQUAL = "comparisonEqual";
	private static final String HTML_CLASS_COMPARISONNOTEQUAL = "comparisonNotEqual";
	private static final String HTML_CLASS_LABELTD = "labelTd";
	private static final String HTML_CLASS_LASTROWTD = "lastLabelRowTd";
	private static final String HTML_CLASS_ITEMPICTURE = "itemPicture";
	private static final String HTML_CLASS_THINNER = "th-inner";
	private static final String HTML_CLASS_GROUPCOL = "categoriesCol";
	private static final String HTML_CLASS_ITEMCOL = "itemCol";
	private static final String HTML_CLASS_HIDDENCOMPAREDROW = "hiddenComparedRow";
	private static final String HTML_CLASS_COMPARISONTABLEFOOTNOTE = "comparisonTableFootnote";

	private RecordComparisonOptions options;
	private List<Adaptation> comparedItems;
	private final List<RecordComparisonItemField> comparisonItemFields = new ArrayList<>();
	private final Locale locale;

	// ========================================================================

	/**
	 * Instantiate the RecordComparison class by setting the compared items.
	 *
	 * @param comparedItems the list of adaptation to compare. Same schema is
	 *                      required.
	 * @param locale        the locale
	 * @since 1.0.0
	 */
	public RecordComparison(final List<Adaptation> comparedItems, final Locale locale) {
		this.comparedItems = comparedItems;
		if (comparedItems == null) {
			this.comparedItems = new ArrayList<>();
		}
		this.options = new RecordComparisonOptions();
		this.locale = locale;

		this.initializeComparison();
	}

	/**
	 * Instantiate the RecordComparison class by setting the compared items and the
	 * options.
	 *
	 * @param comparedItems the list of adaptation to compare. Same model is
	 *                      required.
	 * @param options       the options bean to configure the record comparison.
	 * @param locale        the locale
	 * @since 1.0.0
	 */
	public RecordComparison(final List<Adaptation> comparedItems, final RecordComparisonOptions options,
			final Locale locale) {
		this.comparedItems = comparedItems;
		if (comparedItems == null) {
			this.comparedItems = new ArrayList<>();
		}

		this.options = options;
		if (options == null) {
			this.options = new RecordComparisonOptions();
		}

		this.locale = locale;

		this.initializeComparison();
	}

	/**
	 * Insert the comparison result table into your UI Services or UI Forms.
	 *
	 * @param pWriter the UIComponentWriter
	 * @since 1.0.0
	 */
	public void addComparisonTable(final UIComponentWriter pWriter) {
		if (this.comparisonItemFields.isEmpty() || this.comparedItems.isEmpty()) {
			pWriter.add("<p class=\"" + UICSSClasses.CONTAINER_WITH_TEXT + "\">");
			pWriter.add(Messages.get(this.getClass(), this.locale, "TheComparisonIsNotPossible", ""));
			pWriter.add("</p>");
		} else {
			this.insertComparisonTable(pWriter);
		}
	}

	/**
	 * Gets the comparison result.
	 *
	 * @return the list of RecordComparisonItemField defining for each compared
	 *         field the information of the comparison.
	 * @since 1.0.0
	 */
	public List<RecordComparisonItemField> getComparisonResult() {
		return this.comparisonItemFields;
	}

	/**
	 * Gets the options.
	 *
	 * @return the RecordComparisonOptions defining the options set for the
	 *         comparison.
	 * @since 1.0.0
	 */
	public RecordComparisonOptions getOptions() {
		return this.options;
	}

	/**
	 * Set the list of compared items (records).
	 *
	 * @param pComparedItems the list of records to compare
	 * @since 1.0.0
	 */
	public void setComparedItems(final List<Adaptation> pComparedItems) {
		this.comparedItems = pComparedItems;
		this.initializeComparison();
	}

	/**
	 * Get the values of the items for each compared fields and compare them.
	 *
	 * @since 1.0.0
	 */
	private void compareItems() {
		// No comparison if no compared field
		if (this.comparisonItemFields.isEmpty()) {
			return;
		}

		// Iterate on compared fields
		for (RecordComparisonItemField fieldBean : this.comparisonItemFields) {
			// Field groups are not compared
			if (!fieldBean.isGroup()) {
				SchemaNode fieldNode = fieldBean.getNode();

				List<RecordComparisonItemValue> itemValues = new ArrayList<>();

				for (Adaptation item : this.comparedItems) {
					List<String> valueLabels = new ArrayList<>();
					List<Adaptation> valueAdaptations = new ArrayList<>();

					// Depending on the field type, values are get differently

					if (fieldBean.isAssociation()) {
						RequestResult associatedItems = fieldNode.getAssociationLink().getAssociationResult(item);
						try {
							if (associatedItems != null && associatedItems.isSizeGreaterOrEqual(1)) {
								Adaptation associatedItem = null;

								while ((associatedItem = associatedItems.nextAdaptation()) != null) {
									valueLabels.add(associatedItem.getLabel(this.locale));
									valueAdaptations.add(associatedItem);
								}

							}
						} finally {
							if (associatedItems != null) {
								associatedItems.close();
							}
						}
					} else if (item.get(fieldBean.getPath()) == null) {
						valueLabels.add("");
					} else if (fieldBean.isMultiOccurence()) {
						@SuppressWarnings("rawtypes")
						List list = item.getList(fieldBean.getPath());
						int nbOccurs = list.size();

						for (int k = 0; k < nbOccurs; k++) {
							ValueContext vc = item.createValueContext();
							valueLabels.add(fieldNode.displayOccurrence(list.get(k), true, vc, this.locale));
						}
					} else {
						ValueContext vc = item.createValueContext();
						if (fieldBean.isForeignKey()) {
							valueAdaptations.add(fieldNode.getFacetOnTableReference().getLinkedRecord(vc));
						}
						valueLabels
								.add(fieldNode.displayOccurrence(item.get(fieldBean.getPath()), true, vc, this.locale));
					}

					RecordComparisonItemValue itemValue = new RecordComparisonItemValue();
					itemValue.setLabels(valueLabels);
					itemValue.setRecords(valueAdaptations);

					itemValues.add(itemValue);
				}
				fieldBean.setEqual(RecordComparison.compareItemValues(itemValues));
				fieldBean.setItemValues(itemValues);
			}
		}
	}

	/**
	 * Compare the item values.
	 *
	 * @param pItemValues the list of item values
	 * @return true if values are equal, false if not.
	 * @since 1.0.0
	 */
	private static boolean compareItemValues(final List<RecordComparisonItemValue> pItemValues) {
		boolean equal = true;

		int nbItems = pItemValues.size();
		for (int i = 0; i < nbItems; i++) {
			if (i != 0) {
				if (!pItemValues.get(i).getLabels().equals(pItemValues.get(i - 1).getLabels())) {
					equal = false;
				}
			}
		}

		return equal;
	}

	/**
	 * Initialize comparison.
	 *
	 * @since 1.0.0
	 */
	private void initializeComparison() {
		if (!this.comparedItems.isEmpty()) {

			// Build the list of compared fields
			SchemaNode rootSchemaNode = this.comparedItems.get(0).getSchemaNode();
			this.prepareComparisonFields(rootSchemaNode);

			// Compared the values of the selected items
			this.compareItems();
		}
	}

	/**
	 * Insert menu buttons to manipulate the table.
	 * <ul>
	 * <li>Hide similars button</li>
	 * <li>Hide differences button</li>
	 * <li>Show all button</li>
	 * </ul>
	 *
	 * @param pWriter the component writer
	 * @since 1.0.0
	 */
	private void insertButtons(final UIComponentWriter pWriter) {
		pWriter.add("<div id=\"buttonsDiv\" style=\"margin:15px;\">");

		// Hide similars button
		UserMessage hideSimilarLabel = Messages.getInfo(this.getClass(), "HideSimilarities", "");
		UIButtonSpecJSAction jsButtonHideSimilarSpec = new UIButtonSpecJSAction(hideSimilarLabel,
				RecordComparison.JS_HIDE_FUNCTION_NAME + "('" + RecordComparison.HTML_CLASS_COMPARISONEQUAL + "')");
		pWriter.addButtonJavaScript(jsButtonHideSimilarSpec);

		// Hide differences button
		UserMessage hideDiffLabel = Messages.getInfo(this.getClass(), "HideDifferences", "");
		UIButtonSpecJSAction jsButtonHideDiffSpec = new UIButtonSpecJSAction(hideDiffLabel,
				RecordComparison.JS_HIDE_FUNCTION_NAME + "('" + RecordComparison.HTML_CLASS_COMPARISONNOTEQUAL + "')");
		pWriter.addButtonJavaScript(jsButtonHideDiffSpec);

		// Show all button
		UserMessage showAllLabel = Messages.getInfo(this.getClass(), "ShowAll", "");
		UIButtonSpecJSAction jsButtonShowAllSpec = new UIButtonSpecJSAction(showAllLabel,
				RecordComparison.JS_SHOWALL_FUNCTION_NAME + "()");
		pWriter.addButtonJavaScript(jsButtonShowAllSpec);

		pWriter.add("</div>");
	}

	/**
	 * Insert the comparison table.
	 *
	 * @param pWriter the component writer
	 * @since 1.0.0
	 */
	private void insertComparisonTable(final UIComponentWriter pWriter) {
		final int nbItems = this.comparedItems.size();

		// Table dimensions -----------------------------------------
		// Height is ajusted to the workspace via JS, default value could be
		// changed
		final int firstColWidth = this.options.getCategoryColumnWidth();
		final int itemColWidth = this.options.getItemColumnWidth();

		// table width is computed according to the column's widths and the
		// number of column
		final int tableWidth = firstColWidth + nbItems * itemColWidth;
		final String tableWidthpx = String.valueOf(tableWidth) + "px";

		// Header height, default value could be changed
		final int headerHeight = this.options.getTableHeaderHeight();

		// Style definition -----------------------------------------
		final String containerWidth = "width:" + tableWidthpx + ";";
		final String containerPaddingTop = "padding-top: " + headerHeight + "px;";
		final String headerHeightStyle = "height: " + headerHeight + "px;";

		// Dependencies -------------------------------------------------------
		this.insertDependencies(pWriter);

		// Buttons ------------------------------------------------------------
		if (this.options.displayFunctionsMenu()) {
			this.insertButtons(pWriter);
		}

		// Insert the table ---------------------------------------------------

		// Start main container
		String mainContainerClass = RecordComparison.HTML_CLASS_TABLECONTAINER + " ebx_ColoredBorder";
		String mainContainerStyle = containerWidth + containerPaddingTop;
		pWriter.add("<div id=\"" + RecordComparison.HTML_ID_TABLE_CONTAINER + "\" class=\"" + mainContainerClass
				+ "\" style=\"" + mainContainerStyle + "\">");

		// fixed header
		String tableHeaderClass = RecordComparison.HTML_CLASS_TABLEHEADER
				+ " ebx_LightColoredBackground ebx_ColoredBorder";
		String tableHeaderStyle = headerHeightStyle + containerWidth;
		pWriter.add("<div class=\"" + tableHeaderClass + "\" style=\"" + tableHeaderStyle + "\">");
		pWriter.add("</div>");

		// Start table container
		String tableContainerClass = RecordComparison.HTML_CLASS_TABLESUBCONTAINER;
		String tableContainerStyle = containerWidth;
		pWriter.add("<div class=\"" + tableContainerClass + "\" style=\"" + tableContainerStyle + "\">");

		// Start table
		String tableClass = RecordComparison.HTML_CLASS_PRODUCTCOMPARETABLE;
		String tableStyle = containerWidth;
		pWriter.add("<table class=\"" + tableClass + "\" style=\"" + tableStyle + "\">");

		// Table header -------------------------------------------------------
		this.insertTableHeader(pWriter, headerHeight, itemColWidth);

		// Table body ---------------------------------------------------------
		this.insertTableBody(pWriter);

		pWriter.add("</table>");

		pWriter.add("</div>"); // End table container
		pWriter.add("</div>"); // End main container

		// Informative message ------------------------------------------------

		String footnoteClass = RecordComparison.HTML_CLASS_COMPARISONTABLEFOOTNOTE;
		pWriter.add("<p class=\"" + footnoteClass + "\">");
		pWriter.add(Messages.get(this.getClass(), this.locale, "ThoseFieldsAreNotCompared", ""));
		pWriter.add("</p>");

		// Insert JavaScript --------------------------------------------------

		RecordComparison.insertResizingFunction(pWriter, tableWidth);
		if (this.options.displayFunctionsMenu()) {
			RecordComparison.insertHideShowFunctions(pWriter);
		}
	}

	/**
	 * Insert the stylesheet and script dependencies.
	 *
	 * @param pWriter the component writer
	 * @since 1.0.0
	 */
	private void insertDependencies(final UIComponentWriter pWriter) {
		this.insertStyle(pWriter);
	}

	/**
	 * Insert a td in a row corresponding to the first column (field label).
	 *
	 * @param pWriter     the component writer
	 * @param pLabel      the label of the field to be displayed
	 * @param pIsCompared a boolean indicating the field is compared or not
	 * @param pIsLastRow  a boolean indicating if the row is the last of the table
	 *                    (require a specific style)
	 * @since 1.0.0
	 */
	private static void insertFieldLabelCell(final UIComponentWriter pWriter, final String pLabel,
			final boolean pIsCompared, final boolean pIsLastRow) {
		String tdClass = RecordComparison.HTML_CLASS_LABELTD + " ebx_ColoredBorder";
		if (pIsLastRow) {
			tdClass += " " + RecordComparison.HTML_CLASS_LASTROWTD;
		}
		pWriter.add("<td class=\"" + tdClass + "\">");
		pWriter.add(pLabel);
		// Add a '*' symbol after the label indicating the field is not compared
		if (!pIsCompared) {
			pWriter.add("*");
		}
		pWriter.add("</td>");
	}

	/**
	 * Insert a row representing an item field.
	 *
	 * @param pWriter    the component writer
	 * @param pBean      the bean of the row, corresponding to the field
	 * @param pIsEven    a boolean indicating if the row is even
	 * @param pIsLastRow a boolean indicating if the row is the last of the table
	 *                   (require a specific style)
	 * @since 1.0.0
	 */
	private void insertFieldRow(final UIComponentWriter pWriter, final RecordComparisonItemField pBean,
			final boolean pIsEven, final boolean pIsLastRow) {
		String trClass = "";
		// Add the relevant even or odd class
		if (pIsEven) {
			trClass += RecordComparison.HTML_CLASS_TVEVEN;
		} else {
			trClass += RecordComparison.HTML_CLASS_TVODD;
		}
		// Add the relevant class if the values are equal or not
		trClass += " ";
		if (!pBean.isEqual() && pBean.isCompared()) {
			trClass += RecordComparison.HTML_CLASS_COMPARISONNOTEQUAL;
		} else if (pBean.isEqual() && pBean.isCompared()) {
			trClass += RecordComparison.HTML_CLASS_COMPARISONEQUAL;
		}
		pWriter.add("<tr class=\"" + trClass + "\">");

		// Field label cell ----------------------------------------------
		RecordComparison.insertFieldLabelCell(pWriter, pBean.getLabel(), pBean.isCompared(), pIsLastRow);

		// Item cell --------------------------------------------------
		List<RecordComparisonItemValue> itemValues = pBean.getItemValues();
		for (RecordComparisonItemValue itemValue : itemValues) {
			if (pBean.isPicture()) {
				String pictureRef = itemValue.getLabels().get(0);
				this.insertItemPictureCell(pWriter, pictureRef, pBean.isIDAMPicture(), pIsLastRow);
			} else if (pBean.isAssociation() || pBean.isMultiOccurence()) {
				this.insertItemCell(pWriter, itemValue, true, pIsLastRow);
			} else {
				this.insertItemCell(pWriter, itemValue, false, pIsLastRow);
			}
		}
		pWriter.add("</tr>");
	}

	/**
	 * Insert a row representing a group.
	 *
	 * @param pWriter the component writer
	 * @param pLabel  the label to be used
	 * @since 1.0.0
	 */
	private void insertGroupRow(final UIComponentWriter pWriter, final String pLabel) {
		final int nbItems = this.comparedItems.size();
		pWriter.add("<tr class=\"" + RecordComparison.HTML_CLASS_GROUPETR + "\">");
		pWriter.add(
				"<td colspan=\"" + String.valueOf(nbItems + 1) + "\" class=\"ebx_ColoredBorder\">" + pLabel + "</td>");
		pWriter.add("</tr>");
	}

	/**
	 * Insert the JavaScript functions to hide / show the comparison fields.
	 *
	 * @param pWriter the component writer
	 * @since 1.0.0
	 */
	private static void insertHideShowFunctions(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + RecordComparison.JS_HIDE_FUNCTION_NAME + "(type) {");
		pWriter.addJS_cr("    var rows = document.getElementsByTagName('tr');");
		pWriter.addJS_cr("    for (var i = 0, c = rows.length; i < c; i++) {");
		pWriter.addJS_cr("        if(rows[i].className.indexOf(type) > -1){");
		pWriter.addJS_cr("            rows[i].style.display = 'none';");
		pWriter.addJS_cr("        } else {");
		pWriter.addJS_cr("            rows[i].style.display = '';");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + RecordComparison.JS_SHOWALL_FUNCTION_NAME + "() {");
		pWriter.addJS_cr("    var rows = document.getElementsByTagName('tr');");
		pWriter.addJS_cr("    for (var i = 0, c = rows.length; i < c; i++) {");
		pWriter.addJS_cr("        rows[i].style.display = '';");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Insert a td in a row representing the value of an item.
	 *
	 * @param pWriter                         the component writer
	 * @param pItemValue                      the bean aggregating the value of a
	 *                                        single item
	 * @param pIsAssociationOrMultiOccurrence a boolean to define if the cell is an
	 *                                        association or a multi-occurenced
	 *                                        value (true).
	 * @param pIsLastRow                      a boolean indicating if the row is the
	 *                                        last of the table (require a specific
	 *                                        style)
	 * @since 1.0.0
	 */
	private void insertItemCell(final UIComponentWriter pWriter, final RecordComparisonItemValue pItemValue,
			final boolean pIsAssociationOrMultiOccurrence, final boolean pIsLastRow) {
		String tdClass = "ebx_ColoredBorder";
		if (pIsLastRow) {
			tdClass += " " + RecordComparison.HTML_CLASS_LASTROWTD;
		}

		pWriter.add("<td class=\"" + tdClass + "\">");

		// If the field is a multi-occurence or an association, a list will
		// be displayed
		if (pIsAssociationOrMultiOccurrence) {
			pWriter.add("<ul>");
			for (int i = 0; i < pItemValue.getNbValue(); i++) {
				pWriter.add("<li>");
				RecordComparison.insertValue(pWriter, pItemValue, i);
				pWriter.add("</li>");
			}
			pWriter.add("</ul>");
		} else {
			RecordComparison.insertValue(pWriter, pItemValue, 0);
		}
		pWriter.add("</td>");
	}

	/**
	 * Insert a td in a row corresponding to a picture, the picture is displayed.
	 *
	 * @param pWriter        the component writer
	 * @param pPictureRef    the reference to the picture as string
	 * @param pIsIDAMPicture a boolean indicating if the picture is managed by IDAM
	 * @param pIsLastRow     a boolean indicating if the row is the last of the
	 *                       table (require a specific style)
	 * @since 1.0.0
	 */
	private void insertItemPictureCell(final UIComponentWriter pWriter, final String pPictureRef,
			final boolean pIsIDAMPicture, final boolean pIsLastRow) {
		String tdClass = "ebx_ColoredBorder";
		if (pIsLastRow) {
			tdClass += " " + RecordComparison.HTML_CLASS_LASTROWTD;
		}
		if (pIsIDAMPicture) {
			tdClass += " " + RecordComparison.HTML_CLASS_ITEMPICTURE;
		}
		pWriter.add("<td class=\"" + tdClass + "\">");

		this.insertPicture(pWriter, pPictureRef);

		pWriter.add("</td>");
	}

	/**
	 * Insert a picture from the resources.
	 *
	 * @param pWriter     the component writer
	 * @param pPictureRef the reference of the picture
	 * @since 1.0.0
	 */
	private void insertPicture(final UIComponentWriter pWriter, final String pPictureRef) {
		String src = pWriter.getURLForResource(ResourceType.IMAGE, pPictureRef, this.locale);
		int width = this.options.getPictureSize();
		String style = "display:block;margin:auto;";
		pWriter.add("<img src=\"" + src + "\"  width=\"" + String.valueOf(width) + "\" style=\"" + style + "\">");
	}

	/**
	 * Insert the JavaScript function to resize the table according to the workspace
	 * size.
	 *
	 * @param pWriter     the component writer
	 * @param pTableWidth the size of the table, according to the number and the
	 *                    width of each column
	 * @since 1.0.0
	 */
	private static void insertResizingFunction(final UIComponentWriter pWriter, final int pTableWidth) {

		String jsFnListenerName = "syncContainerToWorkspace";
		pWriter.addJS_cr();
		pWriter.addJS("function ").addJS(jsFnListenerName).addJS("(size) {");
		pWriter.addJS_cr("    var tableWidth = " + String.valueOf(pTableWidth) + ";");
		pWriter.addJS_cr(
				"    var container = document.getElementById('" + RecordComparison.HTML_ID_TABLE_CONTAINER + "');");
		pWriter.addJS_cr("    container.style.height = (size.h * 1)- 128 + 'px';");
		// -115px to take into account the buttons on top and the message at
		// bottom
		pWriter.addJS_cr("    if(size.w < tableWidth){");
		pWriter.addJS_cr("        container.style.width = (size.w * 1) - 40 + 'px';");
		// -40px to take into account the margin on both side
		pWriter.addJS_cr("    } else {");
		pWriter.addJS_cr("        container.style.width = tableWidth + 'px';");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
		pWriter.addJS_addResizeWorkspaceListener(jsFnListenerName);
	}

	/**
	 * Insert a style html tag to define the classes and style of the comparison
	 * table. Not recommended to insert style tag in the body tag of an html
	 * structure but this avoid managing a css file in the resources.
	 *
	 * @param pWriter the component writer
	 * @since 1.0.0
	 */
	private void insertStyle(final UIComponentWriter pWriter) {

		Color color = this.options.getDifferenceColor();
		String red = String.valueOf(color.getRed());
		String green = String.valueOf(color.getGreen());
		String blue = String.valueOf(color.getBlue());
		String cssColor = "rgb(" + red + "," + green + "," + blue + ")";

		String style = "";
		style += "." + RecordComparison.HTML_CLASS_TABLECONTAINER
				+ "{margin:0 15px;position:relative;border-width:1px;border-style:solid;overflow-x:auto;overflow-y:hidden}";
		style += "." + RecordComparison.HTML_CLASS_TABLEHEADER
				+ "{position:absolute;top:0;right:0;left:0;border-width:0 0 1px;border-style:solid}";
		style += "." + RecordComparison.HTML_CLASS_TABLESUBCONTAINER
				+ "{height:100%;overflow-x:hidden;overflow-y:auto}";
		style += "." + RecordComparison.HTML_CLASS_PRODUCTCOMPARETABLE
				+ "{overflow-x:hidden;overflow-y:auto;border-collapse:separate;width:100%;border-spacing:0}";
		style += "." + RecordComparison.HTML_CLASS_PRODUCTCOMPARETABLE + " thead{vertical-align:middle}";
		style += "." + RecordComparison.HTML_CLASS_PRODUCTCOMPARETABLE + " th{padding:0 5px}";
		style += "." + RecordComparison.HTML_CLASS_THINNER
				+ "{font-size:13px;position:absolute;top:0;padding-left:5px;margin-left:-5px;border-width:0 0 0 1px;border-style:solid;overflow:hidden}";
		style += "." + RecordComparison.HTML_CLASS_GROUPCOL + " ." + RecordComparison.HTML_CLASS_THINNER
				+ "{width:191px;padding-right:30px;padding-left:6px;border-left:none}";
		style += "." + RecordComparison.HTML_CLASS_PRODUCTCOMPARETABLE + " tbody{font-size:13px}";
		style += "." + RecordComparison.HTML_CLASS_PRODUCTCOMPARETABLE
				+ " td{border-width:0 0 1px 1px;border-style:solid;padding:5px 10px}";
		style += "." + RecordComparison.HTML_CLASS_COMPARISONNOTEQUAL + "{background-color:" + cssColor + "}";
		style += "." + RecordComparison.HTML_CLASS_GROUPETR
				+ "{font-weight:700;text-align:center;vertical-align:bottom;height:40px}";
		style += "." + RecordComparison.HTML_CLASS_GROUPCOL + "{text-align:right;width:200px;padding:0 10px}";
		style += "." + RecordComparison.HTML_CLASS_GROUPETR + " td{border-left-width:0!important}";
		style += "." + RecordComparison.HTML_CLASS_ITEMCOL + "{width:200px;padding:0 10px}";
		style += "." + RecordComparison.HTML_CLASS_LABELTD
				+ "{text-align:right;border-left-width:0!important;padding-right:30px!important}";
		style += "." + RecordComparison.HTML_CLASS_LASTROWTD + "{border-bottom-width:0!important}";
		style += "." + RecordComparison.HTML_CLASS_HIDDENCOMPAREDROW + "{display:none}";
		style += "." + RecordComparison.HTML_CLASS_ITEMPICTURE + "{text-align:center}";
		style += "." + RecordComparison.HTML_CLASS_ITEMPICTURE + " img{max-height:180px;max-width:180px}";
		style += "." + RecordComparison.HTML_CLASS_COMPARISONTABLEFOOTNOTE + "{color:grey;margin-left:15px}";
		style += "." + RecordComparison.HTML_CLASS_PRODUCTCOMPARETABLE
				+ " ul{margin-bottom:0;margin-top:0;padding-left:20px}";

		pWriter.add("<style>");
		pWriter.add(style);
		pWriter.add("</style>");
	}

	/**
	 * Insert the body tag of the table.
	 *
	 * @param pWriter the component writer
	 * @since 1.0.0
	 */
	private void insertTableBody(final UIComponentWriter pWriter) {
		final int nbTableRows = this.comparisonItemFields.size();
		int nbCreatedRows = 0;
		int nbCreatedRowsInGroup = 0;
		boolean isLastRow = false;
		boolean isPreviousGroup = false;

		pWriter.add("<tbody>");
		for (RecordComparisonItemField itemField : this.comparisonItemFields) {
			if (nbCreatedRows + 1 == nbTableRows) {
				isLastRow = true;
			}
			if (itemField.isGroup()) {
				this.insertGroupRow(pWriter, itemField.getLabel());
				isPreviousGroup = true;
				nbCreatedRowsInGroup = 0;
			} else {
				boolean isEven = false;
				if (isPreviousGroup || nbCreatedRowsInGroup % 2 == 0) {
					isEven = true;
				}

				this.insertFieldRow(pWriter, itemField, isEven, isLastRow);
				isPreviousGroup = false;
				nbCreatedRowsInGroup++;
			}
			nbCreatedRows++;
		}
		pWriter.add("</tbody>");
	}

	/**
	 * Insert the thead tag of the table.
	 *
	 * @param pWriter       the component writer.
	 * @param pHeaderHeight the height of the header.
	 * @param pItemColWidth the width of the item column.
	 * @since 1.0.0
	 */
	private void insertTableHeader(final UIComponentWriter pWriter, final int pHeaderHeight, final int pItemColWidth) {
		String thInnerStyle = "line-height:" + pHeaderHeight + "px;";
		thInnerStyle += "height:" + pHeaderHeight + "px;";
		thInnerStyle += "max-height:" + pHeaderHeight + "px;";
		thInnerStyle += "max-width:" + (pItemColWidth - 10) + "px;";

		// --------------------------------------------------------------------

		pWriter.add("<thead>");
		pWriter.add("<tr>");

		String groupThClass = RecordComparison.HTML_CLASS_GROUPCOL;
		String thInnerClass = RecordComparison.HTML_CLASS_THINNER + " ebx_ColoredBorder";
		pWriter.add("<th class=\"" + groupThClass + "\">");
		pWriter.add("<div class=\"" + thInnerClass + "\" style=\"" + thInnerStyle + "\">");
		pWriter.add(Messages.get(this.getClass(), this.locale, "ComparisonTableCategoryColumnHeaderLabel", ""));
		pWriter.add("</div>");
		pWriter.add("</th>");

		String itemThClass = RecordComparison.HTML_CLASS_ITEMCOL;
		for (Adaptation item : this.comparedItems) {
			pWriter.add("<th class=\"" + itemThClass + "\">");
			pWriter.add("<div class=\"" + thInnerClass + "\" style=\"" + thInnerStyle + "\">");
			pWriter.add(item.getLabel(this.locale));
			pWriter.add("</div>");
			pWriter.add("</th>");
		}

		pWriter.add("</tr>");
		pWriter.add("</thead>");
	}

	/**
	 * Insert the value of a field for an item and add a pop-up link to the
	 * associated/linked record.
	 *
	 * @param pWriter    the component writer
	 * @param pItemValue The object containing the labels and adaptations
	 * @param pIndex     The index of the value
	 * @since 1.0.0
	 */
	private static void insertValue(final UIComponentWriter pWriter, final RecordComparisonItemValue pItemValue,
			final int pIndex) {
		if (pItemValue.getRecords().size() - 1 >= pIndex && pItemValue.getLabels().size() - 1 >= pIndex) {
			// insert the label with preview pop-up link
			Presales_UIUtils.addPopUpLink(pWriter, pItemValue.getRecords().get(pIndex),
					pItemValue.getLabels().get(pIndex));
		} else if (pItemValue.getLabels().size() - 1 >= pIndex) {
			// Insert the label only
			pWriter.add(pItemValue.getLabels().get(pIndex));
		}
	}

	/**
	 * Check whether the field must be compared or not.
	 *
	 * @param pNode the schema node to check
	 * @return true if the field must be compared, false if not.
	 * @since 1.0.0
	 */
	private boolean isComparedNode(final SchemaNode pNode) {
		Path nodePath = pNode.getPathInAdaptation();
		if (this.options.getNotComparedPaths() != null && !this.options.getNotComparedPaths().isEmpty()) {
			for (Path notComparedPath : this.options.getNotComparedPaths()) {
				if (notComparedPath.startsWith(Path.SELF)) {
					notComparedPath = notComparedPath.getSubPath(1);
				}
				if (nodePath.equals(notComparedPath)) {
					return false;
				}
			}
		}

		String nodeInformationToExclude = this.options.getNotComparedNodeInformation();
		SchemaNodeInformation nodeInformation = pNode.getInformation();
		if (nodeInformation != null && nodeInformationToExclude != null) {
			String information = nodeInformation.getInformation();
			if (information.contains(nodeInformationToExclude)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether the field must be excluded or not.
	 *
	 * @param pNode the schema node to check
	 * @return true if the node must be excluded, false if not.
	 * @since 1.0.0
	 */
	private boolean isExcludedNode(final SchemaNode pNode) {
		Path nodePath = pNode.getPathInAdaptation();
		if (this.options.getExcludedPaths() != null && !this.options.getExcludedPaths().isEmpty()) {
			for (Path excludedPath : this.options.getExcludedPaths()) {
				if (excludedPath.startsWith(Path.SELF)) {
					excludedPath = excludedPath.getSubPath(1);
				}
				if (nodePath.equals(excludedPath)) {
					return true;
				}
			}
		}

		String nodeInformationToExclude = this.options.getExcludedNodeInformation();
		SchemaNodeInformation nodeInformation = pNode.getInformation();
		if (nodeInformation != null && nodeInformationToExclude != null) {
			String information = nodeInformation.getInformation();
			if (information.contains(nodeInformationToExclude)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the field is an picture managed by the IDAM module.
	 *
	 * @param pNode the schema node to check
	 * @return true if the picture node is linked to IDAM, false if not.
	 * @since 1.0.0
	 */
	private boolean isIdamPicture(final SchemaNode pNode) {
		Path nodePath = pNode.getPathInAdaptation();
		if (this.options.getIdamPicturePaths() != null && !this.options.getIdamPicturePaths().isEmpty()) {
			for (Path idamPicturePath : this.options.getIdamPicturePaths()) {
				if (idamPicturePath.startsWith(Path.SELF)) {
					idamPicturePath = idamPicturePath.getSubPath(1);
				}
				if (nodePath.equals(idamPicturePath)) {
					return true;
				}
			}
		}

		String nodeInformationToExclude = this.options.getIdamPictureNodeInformation();
		SchemaNodeInformation nodeInformation = pNode.getInformation();
		if (nodeInformation != null && nodeInformationToExclude != null) {
			String information = nodeInformation.getInformation();
			if (information.contains(nodeInformationToExclude)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the field is a picture or not.
	 *
	 * @param pNode the schema node to check
	 * @return if the node is a picture, false if not.
	 * @since 1.0.0
	 */
	private boolean isPictureNode(final SchemaNode pNode) {
		Path nodePath = pNode.getPathInAdaptation();
		if (this.options.getPicturePaths() != null && !this.options.getPicturePaths().isEmpty()) {
			for (Path picturePath : this.options.getPicturePaths()) {
				if (picturePath.startsWith(Path.SELF)) {
					picturePath = picturePath.getSubPath(1);
				}
				if (nodePath.equals(picturePath)) {
					return true;
				}
			}
		}

		String nodeInformationToExclude = this.options.getPictureNodeInformation();
		SchemaNodeInformation nodeInformation = pNode.getInformation();
		if (nodeInformation != null && nodeInformationToExclude != null) {
			String information = nodeInformation.getInformation();
			if (information.contains(nodeInformationToExclude)) {
				return true;
			}
		}

		return (this.isIdamPicture(pNode));
	}

	/**
	 * Check whether the field is the root node of the schema.
	 *
	 * @author ATI
	 * @param pNode the schema node to check
	 * @return true, if is root node
	 * @since 1.0.0
	 */
	private static boolean isRootNode(final SchemaNode pNode) {
		return pNode.getPathInAdaptation().equals(Path.ROOT);
	}

	/**
	 * Initialize the comparison by getting all the compared fields from the data
	 * model. Each field is defined in a ItemFieldComparisonBean. The list of bean
	 * represents the list of field.
	 *
	 * @param pNode the node to analyze
	 * @since 1.0.0
	 */
	private void prepareComparisonFields(final SchemaNode pNode) {
		// If field not displayed, exit the method. Children not browsed
		if (this.isExcludedNode(pNode)) {
			return;
		}

		// The root node is skipt
		if (!RecordComparison.isRootNode(pNode)) {
			// Create the bean
			RecordComparisonItemField bean = new RecordComparisonItemField(pNode);

			// Set the label of the field
			bean.setLabel(pNode.getLabel(this.locale));

			// Set whether the values shall be compared
			if (!this.isComparedNode(pNode)) {
				bean.setCompared(false);
			}

			// Set whether the node is an association
			if (pNode.isAssociationNode()) {
				bean.setAssociation(true);
			} else if (pNode.isTerminalValue()) {
				// Set whether the node is a FK
				if (pNode.getFacetOnTableReference() != null) {
					bean.setForeignKey(true);
				}

				// Set whether the node is a picture
				if (this.isPictureNode(pNode)) {
					bean.setPicture(true);
					bean.setCompared(false);
					if (this.isIdamPicture(pNode)) {
						bean.setIDAMPicture(true);
					}
				}

				// Set whether the field is multi-occurenced
				if (pNode.getMaxOccurs() > 1) {
					bean.setMultiOccurence(true);
				}
			} else {
				// Set the field as group
				bean.setGroup(true);
			}

			// Add the bean in the list, if picture it's added in first position
			if (bean.isPicture()) {
				this.comparisonItemFields.add(0, bean);
			} else {
				this.comparisonItemFields.add(bean);
			}
		}

		// Browse children ---------------------------------------------------
		SchemaNode[] nodeChildren = pNode.getNodeChildren();
		for (SchemaNode childrenNode : nodeChildren) {
			this.prepareComparisonFields(childrenNode);
		}
	}
}