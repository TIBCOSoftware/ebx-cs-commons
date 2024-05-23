package com.tibco.ebx.cs.commons.beans.generator.template;

/**
 * Table DTO abstract class
 * 
 * @author Mickaël Chevalier
 *
 * @param <T> TableBean
 */
public abstract class TableDTO<T extends TableBean> extends EBXDTO<T> {

	protected abstract TableDAO<T> getDAO();
}
