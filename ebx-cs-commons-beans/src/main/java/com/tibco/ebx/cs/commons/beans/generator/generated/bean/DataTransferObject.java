/**
* This class has been automatically generated by ebx-cs-commons 2.0.12-SNAPSHOT
*/
package com.tibco.ebx.cs.commons.beans.generator.generated.bean;

import com.tibco.ebx.cs.commons.beans.generator.template.TableBean;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.ModelTableDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.DataTransferObjectDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.ServiceDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.IncludedRelationDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.FieldDAO;
import java.util.Objects;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;
import java.util.List;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.IgnoredFieldDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.MapperDAO;
import java.util.Optional;

/**
* Java Bean of type TableBean automatically generated by service 'Generate Java Accessers' of EBX Commons
* It represents node at path /root/DataTransferObject in data model urn:ebx:module:ebx-cs-commons:/WEB-INF/ebx/schemas/DataModels.xsd.
*
* @author EBX Commons
*/
public class DataTransferObject extends TableBean {

	private Integer identifier;
	private Table table;
	private Field field;
	private String javaPackage;
	private String javaClassName;
	private Boolean generationActive;
	private Mapper mapper;

	/**
	* Default constructor
	*/
	public DataTransferObject(){
	}

	public Integer getIdentifier(){
		return this.identifier;
	}

	public void setIdentifier(final Integer identifier){
		this.identifier = identifier;
	}

	public Table getTable() throws EBXCommonsException {
		if(this.table == null && isBoundToEBX()){
			this.table = ModelTableDAO.getInstance().read(this, DataTransferObjectDAO.path_to_field_table).orElse(null);
		}
		return this.table;
	}

	public void setTable(final Table table){
		this.table = table;
	}

	public Field getField() throws EBXCommonsException {
		if(this.field == null && isBoundToEBX()){
			this.field = FieldDAO.getInstance().read(this, DataTransferObjectDAO.path_to_field_field).orElse(null);
		}
		return this.field;
	}

	public void setField(final Field field){
		this.field = field;
	}

	public String getJavaPackage(){
		return this.javaPackage;
	}

	public void setJavaPackage(final String javaPackage){
		this.overwrite(this.getDAO().path_to_field_javaPackage);
		this.javaPackage = javaPackage;
	}

	public String getJavaClassName(){
		return this.javaClassName;
	}

	public void setJavaClassName(final String javaClassName){
		this.javaClassName = javaClassName;
	}

	public Boolean getGenerationActive(){
		return this.generationActive;
	}

	public void setGenerationActive(final Boolean generationActive){
		this.generationActive = generationActive;
	}

	public Mapper getMapper() throws EBXCommonsException {
		if(this.mapper == null && isBoundToEBX()){
			this.mapper = MapperDAO.getInstance().read(this, DataTransferObjectDAO.path_to_field_mapper).orElse(null);
		}
		return this.mapper;
	}

	public void setMapper(final Mapper mapper){
		this.mapper = mapper;
	}

	public List<IgnoredField> getIgnoredFields(){
		return IgnoredFieldDAO.getInstance().readAll(this, DataTransferObjectDAO.path_to_field_ignoredFields);
	}

	public List<IgnoredField> getIgnoredFields(final String predicate) {
		return IgnoredFieldDAO.getInstance().readAll(this, DataTransferObjectDAO.path_to_field_ignoredFields, Optional.of(predicate));
	}

	public List<IncludedRelation> getIncludedRelations(){
		return IncludedRelationDAO.getInstance().readAll(this, DataTransferObjectDAO.path_to_field_includedRelations);
	}

	public List<IncludedRelation> getIncludedRelations(final String predicate) {
		return IncludedRelationDAO.getInstance().readAll(this, DataTransferObjectDAO.path_to_field_includedRelations, Optional.of(predicate));
	}

	public List<Service> getServices(){
		return ServiceDAO.getInstance().readAll(this, DataTransferObjectDAO.path_to_field_services);
	}

	public List<Service> getServices(final String predicate) {
		return ServiceDAO.getInstance().readAll(this, DataTransferObjectDAO.path_to_field_services, Optional.of(predicate));
	}

	@Override
	protected DataTransferObjectDAO getDAO() {
		return DataTransferObjectDAO.getInstance();
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier,table,field,javaPackage,javaClassName,generationActive,mapper);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataTransferObject)) {
			return false;
		}
		DataTransferObject other = ( DataTransferObject) obj;
		return Objects.equals(identifier, other.identifier)
			 && Objects.equals(table, other.table)
			 && Objects.equals(field, other.field)
			 && Objects.equals(javaPackage, other.javaPackage)
			 && Objects.equals(javaClassName, other.javaClassName)
			 && Objects.equals(generationActive, other.generationActive)
			 && Objects.equals(mapper, other.mapper);
	}
}