package com.tibco.ebx.cs.commons.beans.generator.template;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.orchestranetworks.schema.Path;

/**
 * Java Bean representing a an element in a data model in EBX.
 *
 * @author Mickaël Chevalier
 * @since 2.0.3
 */
public class EBXBean {
	protected final List<Path> loadedPaths = new ArrayList<>();

	protected Set<Path> inheritedFields = new HashSet<>();

	/**
	 * Check if the field inheritance is activated at a given path.
	 *
	 * @param pPath The path of a field in an EBX data model.
	 *
	 * @return true if the field is inheriting its value.
	 */
	public boolean doesInherit(final Path pPath) {
		return this.inheritedFields.contains(pPath);
	}

	/**
	 * Activate the inheritance of a field at a given path.
	 *
	 * @param pPath The path of a field in an EBX data model.
	 *
	 * @return true if the field inheritance was not already activated.
	 */
	public boolean inherit(final Path pPath) {
		return this.inheritedFields.add(pPath);
	}

	/**
	 * Deactivate the inheritance of a field at a given path.
	 *
	 * @param path The path of a field in an EBX data model.
	 *
	 * @return true if the field inheritance was indeed activated.
	 */
	public boolean overwrite(final Path path) {
		return this.inheritedFields.remove(path);
	}
}
