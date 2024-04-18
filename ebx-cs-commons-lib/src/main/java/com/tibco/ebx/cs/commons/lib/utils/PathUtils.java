package com.tibco.ebx.cs.commons.lib.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.DependenciesDefinitionContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.SchemaNodeContext;
import com.orchestranetworks.schema.info.AssociationLink;
import com.orchestranetworks.schema.info.AssociationLinkByLinkTable;
import com.orchestranetworks.schema.info.AssociationLinkByTableRefInverse;
import com.orchestranetworks.schema.info.SchemaInheritanceProperties;

/**
 * Utilities to convert strings to paths, find nodes, etc.
 * 
 * @author Mickaël Chevalier
 */
public final class PathUtils {
	private PathUtils() {
		super();
	}

	public static final String DEFAULT_SEPARATOR = ";";
	public static final String[] DEFAULT_VALID_PATH_PREFIXES = { Path.ROOT.format(), Path.SELF.format(), Path.PARENT.format() };

	/**
	 * Validates a Path String
	 * 
	 * @param context           SchemaNodeContext
	 * @param pathStr           path
	 * @param separator         separator
	 * @param validPathPrefixes Path prefixes list
	 */
	public static void validatePathString(final SchemaNodeContext context, final String pathStr, String separator, String[] validPathPrefixes) {
		if (separator == null) {
			separator = DEFAULT_SEPARATOR;
		}
		if (validPathPrefixes == null) {
			validPathPrefixes = DEFAULT_VALID_PATH_PREFIXES;
		}
		if (pathStr != null) {
			String[] arr = pathStr.split(separator);
			for (String str : arr) {
				boolean foundPrefix = false;
				for (int i = 0; !foundPrefix && i < validPathPrefixes.length; i++) {
					foundPrefix = str.startsWith(validPathPrefixes[i]);
				}
				if (!foundPrefix) {
					StringBuilder bldr = new StringBuilder();
					bldr.append("Path \"");
					bldr.append(str);
					bldr.append("\" is invalid. Must begin with ");
					for (int i = 0; i < validPathPrefixes.length; i++) {
						bldr.append("\"");
						bldr.append(validPathPrefixes[i]);
						bldr.append("\"");
						if (i < validPathPrefixes.length - 1) {
							bldr.append(" or ");
						}
						bldr.append(".");
					}
					context.addError(bldr.toString());
				}
			}
		}
	}

	/**
	 * Convert string to path Set
	 * 
	 * @param pathStr   path
	 * @param separator separator
	 * @return Set of Paths
	 */
	public static Set<Path> convertStringToPathSet(final String pathStr, final String separator) {
		Set<Path> paths = new HashSet<>();
		addPaths(paths, pathStr, separator);
		return paths;
	}

	/**
	 * Convert string to path List
	 * 
	 * @param pathStr   path
	 * @param separator separator
	 * @return List of Paths
	 */
	public static List<Path> convertStringToPathList(final String pathStr, final String separator) {
		List<Path> paths = new ArrayList<>();
		addPaths(paths, pathStr, separator);
		return paths;
	}

	private static void addPaths(final Collection<Path> paths, final String pathStr, final String separator) {
		String sep = separator == null ? DEFAULT_SEPARATOR : separator;
		if (pathStr != null) {
			String[] arr = pathStr.split(sep);
			for (String str : arr) {
				paths.add(Path.parse(str.trim()));
			}
		}
	}

	/**
	 * Convert path array to String
	 * 
	 * @param paths     paths
	 * @param separator separator
	 * @return Path string representation
	 */
	public static String convertPathArrayToString(final Path[] paths, final String separator) {
		String sep = separator == null ? null : DEFAULT_SEPARATOR;
		StringBuilder bldr = new StringBuilder();
		for (int i = 0; i < paths.length; i++) {
			bldr.append(paths[i].format());
			if (i < paths.length - 1) {
				bldr.append(sep);
			}
		}
		return bldr.toString();
	}

	/**
	 * Validate that the path list provided represents a legitimate path expression and return the resolved schema nodes for the paths.
	 * 
	 * @param root  root
	 * @param paths path list
	 * @return resolved schema nodes
	 */
	public static List<SchemaNode> validatePath(final SchemaNode root, final List<Path> paths) {
		SchemaNode currNode = root.getTableNode().getTableOccurrenceRootNode();
		List<SchemaNode> nodes = new ArrayList<>();
		for (Path path : paths) {
			currNode = currNode != null ? currNode.getNode(path) : currNode;
			if (currNode == null) {
				return nodes;
			}
			nodes.add(currNode);
			SchemaNode tableNode = SchemaUtils.getTableNodeForRelated(currNode);
			if (tableNode != null) {
				currNode = tableNode.getTableOccurrenceRootNode();
			}
		}
		return nodes;
	}

	/**
	 * Many component setup methods need to take a path and get the related field, and report errors when the path is missing or if the path is not a field, etc.
	 * 
	 * @param context       SchemaNodeContext e.g. from most setup methods
	 * @param path          path representing a field from the SchemaNodeContext's table node
	 * @param pathName      the name for the error message
	 * @param addDependency -- for constraint context, optionally add field as a dependency
	 * @return SchemaNode the node corresponding to the field
	 */
	public static SchemaNode setupFieldNode(final SchemaNodeContext context, final Path path, final String pathName, final boolean addDependency) {
		return setupFieldNode(context, path, pathName, true, addDependency);
	}

	/**
	 * Many component setup methods need to take a path and get the related field, and report errors when the path is missing or if the path is not a field, etc.
	 * 
	 * @param context       SchemaNodeContext e.g. from most setup methods
	 * @param path          path representing a field from the SchemaNodeContext's table node
	 * @param pathName      the name for the error message
	 * @param required      true if the field is required
	 * @param addDependency -- for constraint context, optionally add field as a dependency
	 * @return SchemaNode the node corresponding to the field
	 */
	public static SchemaNode setupFieldNode(final SchemaNodeContext context, final Path path, final String pathName, final boolean required, final boolean addDependency) {
		return setupFieldNode(context, null, path, pathName, required, addDependency);
	}

	/**
	 * Many component setup methods need to take a path and get the related field, and report errors when the path is missing or if the path is not a field, etc.
	 * 
	 * @param context       SchemaNodeContext e.g. from most setup methods
	 * @param parentNode    optional node from a related field, perhaps
	 * @param path          path representing a field from the SchemaNodeContext's table node
	 * @param paramName     the name for the error message
	 * @param required      true if the field is required
	 * @param addDependency -- for constraint context, optionally add field as a dependency
	 * @return SchemaNode the node corresponding to the field
	 */
	public static SchemaNode setupFieldNode(final SchemaNodeContext context, SchemaNode parentNode, final Path path, final String paramName, final boolean required, final boolean addDependency) {
		if (path == null) {
			if (required) {
				context.addError(paramName + " must be specified.");
			}
		} else {
			if (parentNode == null) {
				parentNode = context.getSchemaNode().getTableNode().getTableOccurrenceRootNode();
			}
			SchemaNode node = parentNode.getNode(path);
			if (node == null) {
				context.addError(paramName + " " + path.format() + " does not exist.");
			} else if (addDependency && context instanceof DependenciesDefinitionContext) {
				addDependency((DependenciesDefinitionContext) context, node);
			}
			return node;
		}
		return null;
	}

	/**
	 * Add a fieldNode as a dependency. If the field is an association field, add dependency to the association table.
	 * 
	 * @param context   DependenciesDefinitionContext
	 * @param fieldNode SchemaNode
	 */
	public static void addDependency(final DependenciesDefinitionContext context, final SchemaNode fieldNode) {
		if (fieldNode.isAssociationNode()) {
			AssociationLink link = fieldNode.getAssociationLink();
			if (link.isLinkTable()) {
				AssociationLinkByLinkTable alink = (AssociationLinkByLinkTable) link;
				Path tablePath = alink.getLinkTablePath();
				SchemaNode tableNode = fieldNode.getNode(tablePath);
				context.addDependencyToInsertDeleteAndModify(tableNode);
			} else if (link.isTableRefInverse()) {
				AssociationLinkByTableRefInverse alink = (AssociationLinkByTableRefInverse) link;
				Path tablePath = alink.getFieldToSourcePath();
				SchemaNode tableNode = fieldNode.getNode(tablePath).getTableNode();
				context.addDependencyToInsertDeleteAndModify(tableNode);
			}
		} else {
			SchemaInheritanceProperties sip = fieldNode.getInheritanceProperties();
			if (sip == null) {
				context.addDependencyToInsertDeleteAndModify(fieldNode);
			}
		}
	}

	/**
	 * get the value of a field in a record starting from the value context of another field
	 * 
	 * @param context ValueContext
	 * @param path    Path
	 * @return value
	 */
	public static Object getOtherFieldFromFieldContext(final ValueContext context, final Path path) {
		return context.getValue(getRelativePathFromFieldContext(context, path));
	}

	/**
	 * get the relative path of a field in a record starting from the value context of another field
	 * 
	 * @param context ValueContext
	 * @param path    Path
	 * @return value
	 */
	public static Path getRelativePathFromFieldContext(final ValueContext context, final Path path) {
		Path fieldPath = context.getNode().getPathInAdaptation();
		int depth = fieldPath.getSize();
		Path computedPath = path;
		for (int i = 0; i < depth; i++) {
			computedPath = Path.PARENT.add(computedPath);
		}
		return computedPath;
	}

}
