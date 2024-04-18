package com.tibco.ebx.cs.commons.ui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class providing useful methods in UI Service
 *
 * @author Aurélien Ticot
 * @since 1.2.0
 */
public final class ServiceUtils {

	/**
	 * Extract the parameters corresponding to the given UUID, the UUID shall be
	 * part of the name of the parameter (ex: using UUID='EBXRocks_' the parameter
	 * looks like 'EBXRocks_MyParamName'). The UUID is then removed from the
	 * parameter name in the returned map.
	 *
	 * @param pParameters the parameters of the url/request
	 * @param pUUID       the uuid to look for
	 * @return the filtered parameters as an HashMap name/value
	 * @throws IllegalArgumentException if the parameters argument is null or empty
	 *                                  and if the UUID is null or equals to ""
	 * @since 1.4.0
	 */
	public static HashMap<String, String> extractParameters(final Map<String, String[]> pParameters, final String pUUID)
			throws IllegalArgumentException {
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

	private ServiceUtils() {
	}
}
