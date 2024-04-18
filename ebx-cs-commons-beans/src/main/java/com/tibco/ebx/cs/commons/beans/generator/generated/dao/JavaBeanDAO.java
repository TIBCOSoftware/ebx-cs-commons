/**
* This class has been automatically generated by ebx-cs-commons 2.0.12-SNAPSHOT
*/
package com.tibco.ebx.cs.commons.beans.generator.generated.dao;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.ValueContextForUpdate;
import com.orchestranetworks.instance.ValueContext;
import com.tibco.ebx.cs.commons.beans.generator.template.TableDAO;
import com.tibco.ebx.cs.commons.beans.generator.template.PrimaryKeySetter;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.JavaBean;
import com.onwbp.adaptation.AdaptationValue;
import com.orchestranetworks.service.SessionPermissions;
import java.util.ArrayList;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;
import java.util.List;
import com.onwbp.adaptation.AdaptationTable;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;
import com.tibco.ebx.cs.commons.beans.generator.exception.BeansTechnicalException;
import java.util.Optional;

/**
* Data access object (DAO) automatically generated by service 'Generate Java Accessers' of EBX Commons
* It aims to access records of table at path /root/JavaBean in data model urn:ebx:module:ebx-cs-commons:/WEB-INF/ebx/schemas/DataModels.xsd.
*
* @author EBX Commons
*/
public class JavaBeanDAO extends TableDAO<JavaBean> {

	private static final JavaBeanDAO instance = new JavaBeanDAO();

	public static final Path path_to_field_identifier = Path.parse("./identifier");
	public static final Path path_to_field_dataModel = Path.parse("./dataModel");
	public static final Path path_to_field_table = Path.parse("./table");
	public static final Path path_to_field_field = Path.parse("./field");
	public static final Path path_to_field_type = Path.parse("./type");
	public static final Path path_to_field_javaPackage = Path.parse("./javaPackage");
	public static final Path path_to_field_javaClassName = Path.parse("./javaClassName");
	public static final Path path_to_field_specific = Path.parse("./specific");
	public static final Path path_to_field_generationActive = Path.parse("./generationActive");

	@Override
	protected void setValuesForUpdate(final ValueContextForUpdate pContext, final JavaBean pJavaBean) throws EBXCommonsException {
		pContext.setValue(pJavaBean.getIdentifier(),path_to_field_identifier);
		if(pJavaBean.getDataModel() != null){
			pContext.setValue(DataModelDAO.getInstance().getRecordPrimaryKey(pJavaBean.getDataModel()),path_to_field_dataModel);
		}
		if(pJavaBean.getTable() != null){
			pContext.setValue(ModelTableDAO.getInstance().getRecordPrimaryKey(pJavaBean.getTable()),path_to_field_table);
		}
		if(pJavaBean.getField() != null){
			pContext.setValue(FieldDAO.getInstance().getRecordPrimaryKey(pJavaBean.getField()),path_to_field_field);
		}
		pContext.setValue(pJavaBean.getType(),path_to_field_type);
		if(pJavaBean.doesInherit(path_to_field_javaPackage)){
			pContext.setValue(AdaptationValue.INHERIT_VALUE,path_to_field_javaPackage);
		}else{
			pContext.setValue(pJavaBean.getJavaPackage(),path_to_field_javaPackage);
		}
		pContext.setValue(pJavaBean.getJavaClassName(),path_to_field_javaClassName);
		pContext.setValue(pJavaBean.getSpecific(),path_to_field_specific);
		pContext.setValue(pJavaBean.getGenerationActive(),path_to_field_generationActive);
	}

	@Override
	protected void getValuesFromAdaptation(final JavaBean pJavaBean, final Adaptation pRecord, final Optional<SessionPermissions> pPermissions) {
		if(!pPermissions.isPresent() || !pPermissions.get().getNodeAccessPermission(pRecord.getSchemaNode().getNode(path_to_field_identifier), pRecord).isHidden()){
			pJavaBean.setIdentifier((Integer) pRecord.get(path_to_field_identifier));
		}
		if(!pPermissions.isPresent() || !pPermissions.get().getNodeAccessPermission(pRecord.getSchemaNode().getNode(path_to_field_type), pRecord).isHidden()){
			pJavaBean.setType((String) pRecord.get(path_to_field_type));
		}
		if(!pPermissions.isPresent() || !pPermissions.get().getNodeAccessPermission(pRecord.getSchemaNode().getNode(path_to_field_javaPackage), pRecord).isHidden()){
			pJavaBean.setJavaPackage((String) pRecord.get(path_to_field_javaPackage));
		}
		if(AdaptationValue.INHERIT_VALUE.equals(pRecord.getValueWithoutResolution(path_to_field_javaPackage))){
			pJavaBean.inherit(path_to_field_javaPackage);
		}else{
			pJavaBean.overwrite(path_to_field_javaPackage);
		}
		if(!pPermissions.isPresent() || !pPermissions.get().getNodeAccessPermission(pRecord.getSchemaNode().getNode(path_to_field_javaClassName), pRecord).isHidden()){
			pJavaBean.setJavaClassName((String) pRecord.get(path_to_field_javaClassName));
		}
		if(!pPermissions.isPresent() || !pPermissions.get().getNodeAccessPermission(pRecord.getSchemaNode().getNode(path_to_field_specific), pRecord).isHidden()){
			pJavaBean.setSpecific((Boolean) pRecord.get(path_to_field_specific));
		}
		if(!pPermissions.isPresent() || !pPermissions.get().getNodeAccessPermission(pRecord.getSchemaNode().getNode(path_to_field_generationActive), pRecord).isHidden()){
			pJavaBean.setGenerationActive((Boolean) pRecord.get(path_to_field_generationActive));
		}
	}

	@Override
	protected void getValuesFromValueContext(final JavaBean pJavaBean, final ValueContext pContext, Optional<SessionPermissions> pPermissions) {
		Path pathToRecordRoot = SchemaUtils.getPathToRecordRoot(pContext.getNode());
		Optional<Adaptation> record = AdaptationUtils.getRecordForValueContext(pContext);
		if(!pPermissions.isPresent() || record.isPresent() && !pPermissions.get().getNodeAccessPermission(record.get().getSchemaNode().getNode(path_to_field_identifier), record.get()).isHidden() || !pPermissions.get().getNodeAccessPermission(pContext.getNode(pathToRecordRoot.add(path_to_field_identifier)), pContext.getAdaptationInstance()).isHidden()){
			pJavaBean.setIdentifier((Integer) pContext.getValue(pathToRecordRoot.add(path_to_field_identifier)));
		}
		if(!pPermissions.isPresent() || record.isPresent() && !pPermissions.get().getNodeAccessPermission(record.get().getSchemaNode().getNode(path_to_field_type), record.get()).isHidden() || !pPermissions.get().getNodeAccessPermission(pContext.getNode(pathToRecordRoot.add(path_to_field_type)), pContext.getAdaptationInstance()).isHidden()){
			pJavaBean.setType((String) pContext.getValue(pathToRecordRoot.add(path_to_field_type)));
		}
		if(!pPermissions.isPresent() || record.isPresent() && !pPermissions.get().getNodeAccessPermission(record.get().getSchemaNode().getNode(path_to_field_javaPackage), record.get()).isHidden() || !pPermissions.get().getNodeAccessPermission(pContext.getNode(pathToRecordRoot.add(path_to_field_javaPackage)), pContext.getAdaptationInstance()).isHidden()){
			pJavaBean.setJavaPackage((String) pContext.getValue(pathToRecordRoot.add(path_to_field_javaPackage)));
		}
		if(record.isPresent()){
			if(AdaptationValue.INHERIT_VALUE.equals(record.get().getValueWithoutResolution(path_to_field_javaPackage))){
				pJavaBean.inherit(path_to_field_javaPackage);
			}else{
				pJavaBean.overwrite(path_to_field_javaPackage);
			}
		}
		if(!pPermissions.isPresent() || record.isPresent() && !pPermissions.get().getNodeAccessPermission(record.get().getSchemaNode().getNode(path_to_field_javaClassName), record.get()).isHidden() || !pPermissions.get().getNodeAccessPermission(pContext.getNode(pathToRecordRoot.add(path_to_field_javaClassName)), pContext.getAdaptationInstance()).isHidden()){
			pJavaBean.setJavaClassName((String) pContext.getValue(pathToRecordRoot.add(path_to_field_javaClassName)));
		}
		if(!pPermissions.isPresent() || record.isPresent() && !pPermissions.get().getNodeAccessPermission(record.get().getSchemaNode().getNode(path_to_field_specific), record.get()).isHidden() || !pPermissions.get().getNodeAccessPermission(pContext.getNode(pathToRecordRoot.add(path_to_field_specific)), pContext.getAdaptationInstance()).isHidden()){
			pJavaBean.setSpecific((Boolean) pContext.getValue(pathToRecordRoot.add(path_to_field_specific)));
		}
		if(!pPermissions.isPresent() || record.isPresent() && !pPermissions.get().getNodeAccessPermission(record.get().getSchemaNode().getNode(path_to_field_generationActive), record.get()).isHidden() || !pPermissions.get().getNodeAccessPermission(pContext.getNode(pathToRecordRoot.add(path_to_field_generationActive)), pContext.getAdaptationInstance()).isHidden()){
			pJavaBean.setGenerationActive((Boolean) pContext.getValue(pathToRecordRoot.add(path_to_field_generationActive)));
		}
	}

	public static JavaBeanDAO getInstance() {
		return instance;
	}

	@Override
	public JavaBean getInstanceOfBean(){
		JavaBean instance = new JavaBean();
		return instance;
	}

	@Override
	public JavaBean getInstanceOfBean(final Adaptation pDataset){
		JavaBean instance = new JavaBean();
		instance.setType("TableBean");
		instance.inherit(path_to_field_javaPackage);
		instance.setSpecific(Boolean.FALSE);
		instance.setGenerationActive(Boolean.TRUE);
		return instance;
	}

	@Override
	protected String[] getPrimaryKeysGetters() {
		return new String[] { "getIdentifier" };
	}

	@Override
	protected List<PrimaryKeySetter> getPrimaryKeysSetters() throws BeansTechnicalException {
		List<PrimaryKeySetter> setters = new ArrayList<>();
		try {
			setters.add(new PrimaryKeySetter(JavaBean.class.getMethod("setIdentifier", Integer.class), Optional.empty()));
		} catch (NoSuchMethodException ex) {
			throw new BeansTechnicalException(ex);
		}
		return setters;
	}

	@Override
	protected DataModelsDAO getDatasetDAO(final Adaptation pDataset) {
		return new DataModelsDAO(pDataset);
	}

	@Override
	protected AdaptationTable getAdaptationTable(final Adaptation pDataset) {
		return this.getDatasetDAO(pDataset).getJavaBeanTable();
	}
}