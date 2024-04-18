package com.tibco.ebx.cs.commons.component.enumeration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaLocation;
import com.tibco.ebx.cs.commons.lib.utils.HomeCollector;

/**
 * 
 * @author Mickaël Chevalier
 *
 */
public class DataModelEnumeration implements ConstraintEnumeration<String> {
	/**
	 * Dataset path
	 *
	 */
	private Path datasetPath;

	public Path getDatasetPath() {
		return datasetPath;
	}

	public void setDatasetPath(final Path datasetPath) {
		this.datasetPath = datasetPath;
	}

	public static Adaptation getAnyDataset(final Repository pRepository, final String pSchemaLocation) {
		Map<String, Adaptation> map = getUsedSchemasLocationsWithOneDataset(pRepository);
		return map.get(pSchemaLocation);
	}

	private static Map<String, Adaptation> getUsedSchemasLocationsWithOneDataset(final Repository repository) {
		Map<String, Adaptation> datasets = new HashMap<>();
		HomeCollector collector = new HomeCollector();
		collector.setIncludingVersion(false);
		List<AdaptationHome> dataspaces = collector.collectHomes(repository.getReferenceBranch(), true);
		for (AdaptationHome dataspace : dataspaces) {
			for (Adaptation instance : dataspace.findAllRoots()) {
				datasets.put(instance.getSchemaLocation().format(), instance);
			}
		}
		return datasets;
	}

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// nothing to check
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		String valueStr = pValue.toString();
		int endModuleName = valueStr.substring(15, valueStr.length()).indexOf(":") + 15;
		String moduleName = valueStr.substring(15, endModuleName);
		File file = SchemaLocation.parse(valueStr).getFileOrNull();
		if (file == null) {
			return valueStr;
		}
		String fileName = file.getName();

		return moduleName + " - " + fileName;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		Map<String, Adaptation> map = getUsedSchemasLocationsWithOneDataset(pContext.getHome().getRepository());
		Map<String, Adaptation> finalMap = map;
		if (this.datasetPath != null) {
			String dataset = (String) pContext.getValue(this.datasetPath);
			if (dataset != null) {
				Map<String, Adaptation> filteredMap = map.entrySet().stream().filter(entry -> entry.getValue().getAdaptationName().getStringName().equals(dataset))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
				finalMap = filteredMap;
			}
		}
		return Arrays.asList(finalMap.keySet().toArray(new String[finalMap.keySet().size()]));
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		// nothing to setup
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return null;
	}

}
