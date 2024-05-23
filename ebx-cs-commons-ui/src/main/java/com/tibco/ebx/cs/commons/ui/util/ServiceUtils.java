package com.tibco.ebx.cs.commons.ui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.ServiceContext;
import com.orchestranetworks.ui.UIButtonSpec;
import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UIFormSpec;
import com.orchestranetworks.ui.UIServiceComponentWriter;
import com.tibco.ebx.cs.commons.lib.message.Messages;

/**
 * Utility class providing useful methods in UI Service
 *
 * @author Aurélien Ticot
 * @since 1.2.0
 */
public final class ServiceUtils {
	/**
	 * Use the getURLForEndingService method of the service context to end the service and redirect to the initial page taht launch the service.
	 *
	 * @param pContext the context
	 * @throws IllegalArgumentException if the argument pContext is null.
	 * @since 1.2.0
	 * @deprecated since 1.8.0 as related to old Service.
	 */
	@Deprecated
	public static void endService(final ServiceContext pContext) throws IllegalArgumentException {
		if (pContext == null) {
			throw new IllegalArgumentException("pContext argument shall not be null");
		}

		String redirectURL = pContext.getURLForEndingService();
		UIServiceComponentWriter writer = pContext.getUIComponentWriter();
		writer.add("<script>");
		writer.add(ServiceUtils.getUrlRedirectJsCommand(redirectURL) + ";");
		writer.add("</script>");
	}

	/**
	 * Extract the parameters corresponding to the given UUID, the UUID shall be part of the name of the parameter (ex: using UUID='EBXRocks_' the parameter looks like 'EBXRocks_MyParamName'). The
	 * UUID is then removed from the parameter name in the returned map.
	 *
	 * @param pRequest the request to get the parameters from
	 * @param pUUID    the uuid to look for
	 * @return the filtered parameters as an HashMap name/value
	 * @throws IllegalArgumentException if the parameters argument is null or empty and if the UUID is null or equals to ""
	 * @since 1.4.0
	 * @deprecated since 1.8.0 as related to old Service.
	 */
	@Deprecated
	public static HashMap<String, String> extractParameters(final HttpServletRequest pRequest, final String pUUID) throws IllegalArgumentException {
		if (pRequest == null) {
			throw new IllegalArgumentException("pRequest argument shall not be null or empty");
		}
		Map<String, String[]> parameters = pRequest.getParameterMap();

		if (parameters == null || parameters.isEmpty()) {
			return new HashMap<>();
		} else {
			return ServiceUtils.extractParameters(parameters, pUUID);
		}
	}

	/**
	 * Extract the parameters corresponding to the given UUID, the UUID shall be part of the name of the parameter (ex: using UUID='EBXRocks_' the parameter looks like 'EBXRocks_MyParamName'). The
	 * UUID is then removed from the parameter name in the returned map.
	 *
	 * @param pParameters the parameters of the url/request
	 * @param pUUID       the uuid to look for
	 * @return the filtered parameters as an HashMap name/value
	 * @throws IllegalArgumentException if the parameters argument is null or empty and if the UUID is null or equals to ""
	 * @since 1.4.0
	 */
	public static HashMap<String, String> extractParameters(final Map<String, String[]> pParameters, final String pUUID) throws IllegalArgumentException {
		if (pParameters == null || pParameters.isEmpty()) {
			throw new IllegalArgumentException("pParameters argument shall not be null or empty");
		}
		if (pUUID == null || pUUID.equals("")) {
			throw new IllegalArgumentException("pUUID argument shall not be null or equals to \"\"");
		}

		HashMap<String, String> extractedParameterValues = new HashMap<>();
		Iterator<Entry<String, String[]>> iterator = pParameters.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String[]> parameter = iterator.next();
			String key = parameter.getKey();
			if (key.contains(pUUID)) {
				String parameterName = key.replace(pUUID, "");
				String[] parameterValueList = parameter.getValue();
				String parameterValue = parameterValueList[0];
				extractedParameterValues.put(parameterName, parameterValue);
			}
		}
		return extractedParameterValues;
	}

	/**
	 * Gets a default form spec with a close button.
	 *
	 * @param pContext the context
	 * @return the default form spec
	 * @throws IllegalArgumentException if the argument pContext is null.
	 * @since 1.2.0
	 * @deprecated since 1.8.0 as related to old Service.
	 */
	@Deprecated
	public static UIFormSpec getDefaultFormSpec(final ServiceContext pContext) throws IllegalArgumentException {
		return ServiceUtils.getDefaultFormSpec(pContext, null);
	}

	/**
	 * Gets a default form spec with a close button.
	 *
	 * @param pContext          the context
	 * @param pCloseButtonLabel the close button label
	 * @return the default form spec
	 * @throws IllegalArgumentException if the argument pContext is null.
	 * @since 1.2.0
	 * @deprecated since 1.8.0 as related to old Service.
	 */
	@Deprecated
	public static UIFormSpec getDefaultFormSpec(final ServiceContext pContext, final UserMessage pCloseButtonLabel) throws IllegalArgumentException {
		if (pContext == null) {
			throw new IllegalArgumentException("pContext argument shall not be null");
		}

		UIFormSpec formSpec = new UIFormSpec();
		UserMessage buttonLabel = pCloseButtonLabel;
		if (buttonLabel == null) {
			buttonLabel = Messages.getInfo(ServiceUtils.class, "close.button.label");
		}
		String endServiceURL = pContext.getURLForEndingService();
		UIButtonSpec closeButtonSpec = new UIButtonSpecJSAction(buttonLabel, ServiceUtils.getUrlReplaceJsCommand(endServiceURL));
		formSpec.addActionBackInBottomBar(closeButtonSpec);

		return formSpec;
	}

	/**
	 * Gets the records as a list for the service according to the way it is called:<br>
	 * <ul>
	 * <li>the selected records if called on selection</li>
	 * <li>the whole table if called on table</li>
	 * </ul>
	 *
	 * @param pContext the service context.
	 * @return the list of adaptation. null if the service is called from a Home or an Instance.
	 * @throws UnsupportedOperationException if too many records are specified.
	 * @throws IllegalArgumentException      if the service context in argument is null.
	 * @since 1.4.0
	 * @deprecated since 1.8.0 as related to old Service.
	 */
	@Deprecated
	public static List<Adaptation> getRecordsAsList(final ServiceContext pContext) throws UnsupportedOperationException, IllegalArgumentException {
		if (pContext == null) {
			throw new IllegalArgumentException("The service context argument shall not be null");
		}

		List<Adaptation> records = new ArrayList<>();

		if (pContext.isCalledOnHome() || pContext.isCalledOnInstance()) {
			return null;
		} else if (pContext.isCalledOnHierarchyNode()) {
			Adaptation currentRecord = pContext.getCurrentAdaptation();
			records.add(currentRecord);
			return records;
		} else if (pContext.isCalledOnTable()) {
			SchemaNode tableNode = pContext.getCurrentTable();
			Adaptation dataSet = pContext.getCurrentAdaptation();
			AdaptationTable table = dataSet.getTable(tableNode.getPathInSchema());
			return table.selectOccurrences(null);
		} else {
			records.addAll(pContext.getSelectedOccurrences());
			return records;
		}
	}

	/**
	 * Gets the records as a request result for the service according to the way it is called:<br>
	 * <ul>
	 * <li>the selected records if called on selection</li>
	 * <li>the whole table if called on table</li>
	 * </ul>
	 *
	 * @param pContext the service context.
	 * @return the RequestResult. null if the service is called from a Home or an Instance.
	 * @throws IllegalArgumentException if the service context in argument is null.
	 * @since 1.4.0
	 * @deprecated since 1.8.0 as related to old Service.
	 */
	@Deprecated
	public static RequestResult getRecordsAsRequestResult(final ServiceContext pContext) throws IllegalArgumentException {
		if (pContext == null) {
			throw new IllegalArgumentException("The service context argument shall not be null");
		}

		if (pContext.isCalledOnHome() || pContext.isCalledOnInstance()) {
			return null;
		} else if (pContext.isCalledOnTable()) {
			Request request = pContext.getCurrentRequest();
			return request.execute();
		} else {
			Request request = pContext.getCurrentRequestOnSelectedOccurrences();
			return request.execute();
		}
	}

	/**
	 * Gets the url redirect js command.
	 *
	 * @param pRedirectUrl the redirect url
	 * @return the url redirect js command
	 * @since 1.2.0
	 */
	private static String getUrlRedirectJsCommand(final String pRedirectUrl) {
		return "window.location.href='" + pRedirectUrl + "'";
	}

	/**
	 * Gets the url replace js command.
	 *
	 * @param pReplaceUrl the replace url
	 * @return the url replace js command
	 * @since 1.2.0
	 */
	private static String getUrlReplaceJsCommand(final String pReplaceUrl) {
		return "window.location.replace('" + pReplaceUrl + "')";
	}

	private ServiceUtils() {
	}
}
