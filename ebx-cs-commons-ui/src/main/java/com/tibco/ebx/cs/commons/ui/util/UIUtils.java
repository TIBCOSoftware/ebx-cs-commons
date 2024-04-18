package com.tibco.ebx.cs.commons.ui.util;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.ResourceType;
import com.orchestranetworks.ui.UIButtonIcon;
import com.orchestranetworks.ui.UIButtonLayout;
import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UIComponentWriter;
import com.orchestranetworks.ui.UIFormRequestContext;
import com.orchestranetworks.ui.form.UIFormContext;

/**
 * Utility class to support UI developments
 *
 * @author Aurélien Ticot
 */
public final class UIUtils {

	private UIUtils() {
		super();
	}

	/**
	 * Adds the css link.
	 *
	 * @author MCH
	 *
	 *         Add a link tag to import a stylesheet from the current module
	 *
	 * @param pWriter   the writer
	 * @param pLocation The location of the stylesheet from
	 *                  /www/<locale>/stylesheets
	 */
	public static void addCssLink(final UIComponentWriter pWriter, final String pLocation) {
		String location = pWriter.getURLForResource(ResourceType.STYLESHEET, pLocation);
		pWriter.add("<link rel=\"stylesheet\" href=\"" + location + "\" > ");
	}

	/**
	 * Adds the ebx private date widget.
	 *
	 * @param pId       used to build the identifier of the the different element
	 *                  that compose the widget.
	 * @param pWriter   the writer
	 * @param pCalendar the calendar
	 * @return the name of the getter
	 * @deprecated Add a the standard EBX date Widget in a UIFormRow (must be). This
	 *             method is deprecated because using EBX private API. It is not
	 *             guarantee to work in the future. It must be replaced by an other
	 *             implementation being close to the standard EBX one.
	 */
	@Deprecated
	public static void addEBXPrivateDateWidget(final String pId, final UIComponentWriter pWriter,
			final Calendar pCalendar) {
		String dayId = pId + "_day";
		String monthId = pId + "_month";
		String yearId = pId + "_year";

		String day = "";
		String month = "";
		String year = "";
		if (pCalendar != null) {
			day = pCalendar.get(Calendar.DAY_OF_MONTH) + "";
			month = pCalendar.get(Calendar.MONTH) + 1 + "";
			year = pCalendar.get(Calendar.YEAR) + "";
		}

		UIButtonSpecJSAction dateButton = new UIButtonSpecJSAction(UserMessage.createInfo("Open cal"),
				"EBX_Form.openCalendar('" + pId + "_year','" + pId + "_month','" + pId + "_day','" + pId + "');");
		dateButton.setButtonIcon(UIButtonIcon.CALENDAR);
		dateButton.setButtonLayout(UIButtonLayout.ICON_ONLY);
		dateButton.setCssClass("ebx_Button ebx_IconButton ebx_Calendar");
		pWriter.add("<span id=\"" + pId + "_dateInputs\" class=\"ebx_InputsDateContainer\">");
		pWriter.add(
				"<input type=\"text\" tabindex=\"1\" maxlength=\"2\" size=\"2\" style=\"width: 3ex;text-align: right;\""
						+ " value=\"" + day + "\" name=\"" + dayId + "\" id=\"" + dayId + "\"/>");
		pWriter.add(
				"/<input type=\"text\" tabindex=\"2\" maxlength=\"2\" size=\"2\" style=\"width: 3ex;text-align: right;\" value=\""
						+ month + "\" name=\"" + monthId + "\" id=\"" + monthId + "\" />");
		pWriter.add(
				"/<input type=\"text\" tabindex=\"3\" maxlength=\"4\" size=\"4\" style=\"width: 5ex;text-align: right;\" value=\""
						+ year + "\" name=\"" + yearId + "\" id=\"" + yearId + "\"/ >");
		pWriter.add("</span>");
		pWriter.addButtonJavaScript(dateButton);
	}

	/**
	 * Include an external stylesheet.
	 *
	 * @param pWriter   the writer
	 * @param pLocation the location of the external stylesheet
	 */
	public static void addExternalCssLink(final UIComponentWriter pWriter, final String pLocation) {
		pWriter.add("<link rel=\"stylesheet\" href=\"" + pLocation + "\" > ");
	}

	/**
	 * Adds the external js library.
	 *
	 * Add a script tag to import a javascript file.
	 *
	 * @param pWriter the writer
	 * @param pURL    The URL of the javascript library
	 */
	public static void addExternalJSLibrary(final UIComponentWriter pWriter, final String pURL) {
		pWriter.add("<script src=\"" + pURL + "\" type=\"text/javascript\"></script>");
	}

	/**
	 * Adds the js ajax call.
	 *
	 *
	 * Add a call to the javascript function based on the EBX Ajax prototype to call
	 * an Ajax Component.
	 *
	 * @param pWriter           UIComponentWriter
	 * @param pURL              url of the ajax component to call
	 * @param pParameter        parameter
	 * @param pAjaxFunctionName the name of the ajax function
	 * @param string
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static void addJSAjaxCall(final UIComponentWriter pWriter, final String pURL, final String pParameter,
			final String pAjaxFunctionName) throws UnsupportedEncodingException {
		pWriter.addJS_cr("var handler = new " + pAjaxFunctionName + "();");
		pWriter.addJS("handler.sendRequest('" + pURL);
		if (StringUtils.isBlank(pParameter)) {
			pWriter.addJS_cr("');");
		} else {
			pWriter.addJS_cr("&'+" + pParameter + ");");
		}
	}

	/**
	 * Adds the js ajax component.
	 *
	 *
	 * Add a javascript function based on the EBX Ajax prototype to call an Ajax
	 * Component Facade to the javascript framework offered by EBX
	 *
	 * @param pWriter           the writer
	 * @param pAjaxFunctionName the name of the ajax function
	 * @param pAjaxSuccessJS    the javascript to execute if the ajax call succeed
	 * @param pAjaxFailedJS     the javascript to execute if the ajax call failed
	 */
	public static void addJSAjaxComponent(final UIComponentWriter pWriter, final String pAjaxFunctionName,
			final String pAjaxSuccessJS, final String pAjaxFailedJS) {
		pWriter.addJS_cr(pAjaxFunctionName + " = function(){");
		{
			pWriter.addJS_cr("this.handleAjaxResponseSuccess = function(responseContent){");
			if (pAjaxSuccessJS != null) {
				pWriter.addJS_cr(pAjaxSuccessJS);
			}
			pWriter.addJS_cr("};");
		}
		{
			pWriter.addJS_cr("this.handleAjaxResponseFailed = function(responseContent){");
			if (pAjaxFailedJS != null) {
				pWriter.addJS_cr(pAjaxFailedJS);
			}
			pWriter.addJS_cr("};");
		}
		pWriter.addJS_cr("};");

		pWriter.addJS_cr(pAjaxFunctionName + ".prototype = new EBX_AJAXResponseHandler();");
	}

	/**
	 * Adds the js library.
	 *
	 *
	 * Add a script tag to import a javascript file.
	 *
	 * @param pWriter   the writer
	 * @param pLocation The location of the javascript library from
	 *                  /www/<locale>/jscripts
	 */
	public static void addJSLibrary(final UIComponentWriter pWriter, final String pLocation) {
		String location = pWriter.getURLForResource(ResourceType.JSCRIPT, pLocation);
		pWriter.add("<script src=\"" + location + "\" type=\"text/javascript\"></script>");
	}

	/**
	 * Get the current adaptation of a UIForm. A dataset if in creation mode, a
	 * record otherwise.
	 *
	 *
	 * @param pContext a UIFormContext
	 * @return a record or a dataset
	 */
	public static Adaptation getCurrentAdaptation(final UIFormContext pContext) {
		Adaptation curentAdaptation = pContext.getCurrentRecord();
		if (curentAdaptation == null) {
			curentAdaptation = pContext.getCurrentDataSet();
		}
		return curentAdaptation;
	}

	/**
	 * Get the current adaptation of a UIFormRequestContext. A dataset if in
	 * creation mode, a record otherwise.
	 *
	 *
	 * @param pContext a UIFormRequestContext
	 * @return a record or a dataset
	 */
	public static Adaptation getCurrentAdaptation(final UIFormRequestContext pContext) {
		Adaptation dataset = pContext.getCurrentDataSet();
		if (pContext.isCreatingRecord()) {
			return dataset;
		}
		AdaptationTable table = dataset.getTable(pContext.getTableNode().getPathInSchema());
		return table.lookupAdaptationByPrimaryKey(pContext.getValueContext(Path.SELF));
	}

	/**
	 * Gets the field label.
	 *
	 *
	 * Gets the label for a field
	 *
	 * @param pAdaptation           the record or data set (for cases where you want
	 *                              a data set level field)
	 * @param pFieldPath            the path of the field within the given
	 *                              adaptation
	 * @param pSession              the user's session
	 * @param pIncludingGroupLabels Include the labels of the parent group(s) of the
	 *                              field
	 * @return the label
	 */
	public static String getFieldLabel(final Adaptation pAdaptation, final Path pFieldPath, final Session pSession,
			final boolean pIncludingGroupLabels) {
		SchemaNode node = pAdaptation.getSchemaNode().getNode(pFieldPath);
		StringBuilder bldr = new StringBuilder();
		Locale locale = pSession.getLocale();
		bldr.append(node.getLabel(locale));
		if (pIncludingGroupLabels) {
			Path tablePath = node.getTableNode().getPathInSchema();
			// Loop through the parents until you get to a table node and for each group add
			// its label
			for (SchemaNode parentNode = node; (parentNode = parentNode.getNode(Path.PARENT)) != null
					&& !tablePath.equals(parentNode.getPathInSchema());) {
				bldr.insert(0, " / ");
				bldr.insert(0, parentNode.getLabel(locale));
			}
		}
		return bldr.toString();
	}

	/**
	 * Gets the node path from unique web identier.
	 *
	 *
	 * Retrieve the xpath of a given node from its unique web identifier generated
	 * by getUniqueWebIdentifierForNode
	 *
	 * @param pNodeId a schema node unique identifier
	 * @return the schema node's xpath
	 * @see getUniqueWebIdentifierForNode
	 */
	public static Path getNodePathFromUniqueWebIdentier(final String pNodeId) {
		return Path.parse(pNodeId.replaceAll("___", "/"));
	}

	/**
	 * Gets the unique web identifier for node.
	 *
	 *
	 * Generate a unique web identifier based on the schema node xpath. The
	 * character '/' is replaced by 3 '_'. The same construction with 2 '_' is
	 * already used by EBX.
	 *
	 * UIResponseContext.getWebName() can be used as a unique identifier but cannot
	 * be used from a UIForm and is not well supported anymore.
	 *
	 * @param pNode a a schema node.
	 * @return a unique identifier for the node in parameter.
	 */
	public static String getUniqueWebIdentifierForNode(final SchemaNode pNode) {
		String id = pNode.getPathInAdaptation().format().replace("/", "___");
		return id.replace(".", "");
	}

	/**
	 * Gets the unique web identifier for a record.
	 *
	 *
	 * Generate a unique web identifier based on the record primary key. The
	 * character '|' is replaced by 3 '_'. The same construction with 2 '_' is
	 * already used by EBX.
	 *
	 * UIResponseContext.getWebName() can be used as a unique identifier but cannot
	 * be used from a UIForm and is not well supported anymore.
	 *
	 * @param pRecord an Adaptation corresponding to a record.
	 * @return a unique identifier for the record in parameter.
	 */
	public static String getUniqueWebIdentifierForRecord(final Adaptation pRecord) {
		return pRecord.getOccurrencePrimaryKey().format().replace("|", "___");
	}

	/**
	 * Gets the value setter name.
	 *
	 * @author MCH
	 * @param pNode the node
	 * @return the name of the setter created with addJSValueSetter
	 * @see addJSValueSetter
	 */
	public static String getValueSetterName(final SchemaNode pNode) {
		String id = UIUtils.getUniqueWebIdentifierForNode(pNode);
		return "set" + id + "Value";
	}

	/**
	 * Verify if a field is writable
	 *
	 * @param pNode    a node
	 * @param pContext a form context
	 * @return true if the field is not writable.
	 */
	public static boolean isNodeReadOnlyOrHidden(final SchemaNode pNode, final UIFormContext pContext) {
		Adaptation adaptation = pContext.getCurrentRecord();
		if (adaptation == null) {
			adaptation = pContext.getCurrentDataSet();
		}
		return isNodeReadOnlyOrHidden(pNode, pContext.getSession(), adaptation);
	}

	/**
	 * Verify if a field is writable
	 *
	 * @param pNode            a node
	 * @param pSession         a session
	 * @param pRecordOrDataset a record or a dataset
	 * @return true if the field is not writable.
	 */
	public static boolean isNodeReadOnlyOrHidden(final SchemaNode pNode, final Session pSession,
			final Adaptation pRecordOrDataset) {
		return !pSession.getPermissions().getNodeAccessPermission(pNode, pRecordOrDataset).isReadWrite();
	}
}
