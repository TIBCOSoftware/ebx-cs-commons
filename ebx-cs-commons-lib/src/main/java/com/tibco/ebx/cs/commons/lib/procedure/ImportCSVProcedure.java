/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import java.io.File;

import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.service.ExportImportCSVSpec;
import com.orchestranetworks.service.ExportImportCSVSpec.Header;
import com.orchestranetworks.service.ImportSpec;
import com.orchestranetworks.service.ImportSpecMode;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;

/**
 * Import a CSV file. Default encoding is UTF-8 and default separator is ','
 * 
 * @author Mickaël Chevalier
 */
public class ImportCSVProcedure implements Procedure {

	/** The file to import. */
	private final File file;

	/** The destination table. */
	private final AdaptationTable table;

	/** The encoding. */
	private String encoding = "UTF-8";

	/** The field separator. */
	private String fieldSeparator = ",";

	/**
	 * Instantiates a new import csv procedure.
	 *
	 * @param pFile  the file
	 * @param pTable the table
	 */
	public ImportCSVProcedure(final File pFile, final AdaptationTable pTable) {
		this.file = pFile;
		this.table = pTable;
	}

	/*
	 * @see com.orchestranetworks.service.Procedure#execute(com.orchestranetworks .service.ProcedureContext)
	 */
	/*
	 * @see com.orchestranetworks.service.Procedure#execute(com.orchestranetworks.service.ProcedureContext)
	 */
	@Override
	public void execute(final ProcedureContext pContext) throws Exception {
		ImportSpec spec = new ImportSpec();
		spec.setImportMode(ImportSpecMode.UPDATE_OR_INSERT);
		spec.setSourceFile(this.file);
		spec.setTargetAdaptationTable(this.table);
		ExportImportCSVSpec csvSpec = new ExportImportCSVSpec();
		csvSpec.setEncoding(this.encoding);
		csvSpec.setFieldSeparator(this.fieldSeparator.charAt(0));
		csvSpec.setHeader(Header.LABEL);
		spec.setCSVSpec(csvSpec);
		pContext.doImport(spec);
	}

	/**
	 * Gets the encoding.
	 *
	 * @return the encoding
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * Gets the field separator.
	 *
	 * @return the field separator
	 */
	public String getFieldSeparator() {
		return this.fieldSeparator;
	}

	/**
	 * Gets the file to import.
	 *
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Gets the destination table.
	 *
	 * @return the table
	 */
	public AdaptationTable getTable() {
		return this.table;
	}

	/**
	 * Sets the encoding. Default is UTF-8.
	 *
	 * @param encoding the new encoding
	 */
	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Sets the field separator. Default is ','.
	 *
	 * @param fieldSeparator the new field separator
	 */
	public void setFieldSeparator(final String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

}
