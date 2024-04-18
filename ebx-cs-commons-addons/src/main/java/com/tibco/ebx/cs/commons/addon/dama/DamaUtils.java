package com.tibco.ebx.cs.commons.addon.dama;

import com.orchestranetworks.addon.dama.models.MediaType;
import com.orchestranetworks.schema.SchemaNode;

/**
 * DAMA Utilities
 * 
 * @author Mickael Chevalier
 */
public final class DamaUtils {

	private DamaUtils() {
		super();
	}

	/**
	 * Return true if the node is of DAMA type MediaType.
	 *
	 * @param pNode the node to be tested.
	 *
	 * @return true if the node is of DAMA type MediaType.
	 */
	public static boolean isNodeDAC(final SchemaNode pNode) {
		if (pNode.getJavaBeanClass() == null) {
			return false;
		}
		return MediaType.class.getCanonicalName().equals(pNode.getJavaBeanClass().getCanonicalName());
	}
}
