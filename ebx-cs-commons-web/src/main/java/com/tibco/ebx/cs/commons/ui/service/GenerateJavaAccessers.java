package com.tibco.ebx.cs.commons.ui.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.UnavailableContentError;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.addon.dama.models.MediaType;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContextForInputValidation;
import com.orchestranetworks.schema.ConstraintViolationException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaLocation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ProcedureResult;
import com.orchestranetworks.service.ProgrammaticService;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.UIButtonSpecSubmit;
import com.orchestranetworks.ui.UICSSClasses;
import com.orchestranetworks.ui.selection.DatasetEntitySelection;
import com.orchestranetworks.userservice.ObjectKey;
import com.orchestranetworks.userservice.UserService;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEvent;
import com.orchestranetworks.userservice.UserServiceEventContext;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServicePane;
import com.orchestranetworks.userservice.UserServicePaneContext;
import com.orchestranetworks.userservice.UserServicePaneWriter;
import com.orchestranetworks.userservice.UserServiceProcessEventOutcomeContext;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceTab;
import com.orchestranetworks.userservice.UserServiceTabbedPane;
import com.orchestranetworks.userservice.UserServiceValidateContext;
import com.tibco.ebx.cs.commons.addon.dama.DamaUtils;
import com.tibco.ebx.cs.commons.beans.generator.exception.BeansTechnicalException;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.DataAccessObject;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.DataModel;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.DataTransferObject;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.Field;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.IgnoredField;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.IncludedRelation;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.JavaBean;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.Mapper;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.RestService;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.Service;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.Table;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.DataAccessObjectDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.DataModelDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.DataTransferObjectDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.FieldDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.JavaBeanDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.MapperDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.ModelTableDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.RestServiceDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.ServiceDAO;
import com.tibco.ebx.cs.commons.beans.generator.util.BeansRepositoryUtils;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;
import com.tibco.ebx.cs.commons.lib.utils.CommonsLogger;
import com.tibco.ebx.cs.commons.lib.utils.CommonsProperties;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;

/**
 *
 * This service can be launched from any dataset to generate Data Access Object
 * for the underlying data model
 *
 * A Java Bean is then generated for every table and terminal complex types.
 *
 * This beans can be construct either from a record (Adaptation) of for a
 * ValueContext. It also provide a method to save in creating or modifying the
 * record.
 *
 * Foreign keys are valued with the java bean of the target table if this last
 * is part of the same dataset, with its string representation otherwise.
 *
 * Any association is resolved when its getter is called.
 *
 * @author Mickaël Chevalier
 */
public class GenerateJavaAccessers implements UserService<DatasetEntitySelection> {

	/**
	 * Default constructor
	 */
	public GenerateJavaAccessers() {
		super();
	}

	/**
	 *
	 * Wizard first step displaying the configuration of the java bean generation,
	 * allowing to specify the destination folder and package.
	 *
	 */
	private class ConfigurationStep implements DatasetWizardStep {

		@Override
		public void setupDisplay(final UserServiceSetupDisplayContext<DatasetEntitySelection> pContext,
				final UserServiceDisplayConfigurator pConfigurator) {

			UserServiceTabbedPane tabbedPane = pConfigurator.newTabbedPane();
			UserServiceTab tab = tabbedPane
					.newTab(GenerateJavaAccessers.this.objectKeys.get(GenerateJavaAccessers.this.schema), Path.SELF);
			Optional<String> modelFileName = SchemaUtils.getModelFileName(GenerateJavaAccessers.this.schema);
			if (modelFileName.isPresent()) {
				tab.setTitle(modelFileName.get());
			} else {
				tab.setTitle(GenerateJavaAccessers.this.schema.format());
			}
			tabbedPane.addTab(tab);

			for (SchemaLocation schema : GenerateJavaAccessers.this.schemas) {
				if (!schema.equals(GenerateJavaAccessers.this.schema)) {
					tab = tabbedPane.newTab(GenerateJavaAccessers.this.objectKeys.get(schema), Path.SELF);
					modelFileName = SchemaUtils.getModelFileName(schema);
					if (modelFileName.isPresent()) {
						tab.setTitle(modelFileName.get());
					} else {
						tab.setTitle(schema.format());
					}
					tabbedPane.addTab(tab);
				}
			}
			pConfigurator.setContent(tabbedPane);
			pConfigurator.setLeftButtons(pConfigurator.newCloseButton());
			UIButtonSpecSubmit next = pConfigurator.newNextButton(new UserServiceEvent() {

				@Override
				public UserServiceEventOutcome processEvent(final UserServiceEventContext pEventContext) {
					GenerateJavaAccessers.this.DATA_MODELS.clear();
					GenerateJavaAccessers.this.terminalComplexElements.clear();
					for (SchemaLocation schema : GenerateJavaAccessers.this.schemas) {
						ObjectKey key = GenerateJavaAccessers.this.objectKeys.get(schema);
						ProcedureResult result = pEventContext.save(key);
						if (result.hasFailed()) {
							pContext.addError(result.getExceptionFullMessage(pContext.getSession().getLocale()));
						}
						DataModel dataModel = DataModelDAO.getInstance().read(AdaptationUtils
								.getRecordForValueContext(pEventContext.getValueContext(key)).orElse(null));
						GenerateJavaAccessers.this.DATA_MODELS.put(schema, dataModel);
					}
					return CustomOutcome.GENERATION_STEP;
				}
			});
			next.setDefaultButton(true);
			next.setLabel(UserMessage.createInfo("Generate"));
			pConfigurator.setRightButtons(next);
		}
	}

	private enum CustomOutcome implements UserServiceEventOutcome {
		CONFIGURATION_STEP, GENERATION_STEP
	}

	/**
	 *
	 * Wizard second step generating Java Beans and displaying the report of
	 * generation indicating which Java Beans must be set on complex types in the
	 * data model.
	 *
	 */
	private class GenerateStep implements DatasetWizardStep {
		@Override
		public void setupDisplay(final UserServiceSetupDisplayContext<DatasetEntitySelection> pContext,
				final UserServiceDisplayConfigurator pConfigurator) throws OperationException {
			try {
				GenerateJavaAccessers.this.initConfiguration(pContext.getRepository());
			} catch (EBXCommonsException ex) {
				throw OperationException.createError(ex);
			}
			pConfigurator.setContent(new JavaBeansGenerationPane());
			pConfigurator.setLeftButtons(pConfigurator.newCloseButton());
			UIButtonSpecSubmit next = pConfigurator.newNextButton(pEventContext -> CustomOutcome.CONFIGURATION_STEP);
			next.setDefaultButton(true);
			next.setLabel(UserMessage.createInfo("Details"));
			pConfigurator.setRightButtons(next);
		}
	}

	/**
	 *
	 * Pane of the wizard second step to display configuration form.
	 *
	 */
	public class JavaBeansGenerationPane implements UserServicePane {
		@Override
		public void writePane(final UserServicePaneContext pContext, final UserServicePaneWriter pWriter) {
			pWriter.add("<div class='" + UICSSClasses.CONTAINER_WITH_TEXT + "'>");

			ProgrammaticService srv = ProgrammaticService.createForSession(pWriter.getSession(),
					BeansRepositoryUtils.getCommonsDataspace(pContext.getRepository()));

			srv.execute(new Procedure() {
				@Override
				public void execute(final ProcedureContext pContext) throws Exception {

					GenerateJavaAccessers.this.procedureContext = pContext;

					for (SchemaLocation schema : GenerateJavaAccessers.this.schemas) {
						try {
							GenerateJavaAccessers.this.generateBeanForSchema(schema);
							GenerateJavaAccessers.this.generateDatasetDAO(schema);
						} catch (UnavailableContentError | OperationException ex) {
							pWriter.add("<span style='color:red'>Failed to execute service : " + ex.getMessage()
									+ "</span><br />");
							CommonsLogger.getLogger().error(ex.getMessage(), ex);
						}
					}
					pWriter.add("<b>Generation complete</b><br />");
					pWriter.add("<span>" + GenerateJavaAccessers.this.numberOfBeans + " Java beans</span><br />");
					pWriter.add("<span>" + GenerateJavaAccessers.this.numberOfDAOs + " DAO</span><br />");
					pWriter.add("<span>" + GenerateJavaAccessers.this.numberOfDTOs + " DTO</span><br />");
					pWriter.add("<span>" + GenerateJavaAccessers.this.numberOfMappers + " mappers</span><br />");
					pWriter.add("<span>" + GenerateJavaAccessers.this.numberOfServices + " services</span><br />");
					pWriter.add("<span>" + GenerateJavaAccessers.this.numberOfRS + " REST services</span><br />");
					pWriter.add("<br />");

					if (!GenerateJavaAccessers.this.terminalComplexElements.isEmpty()) {
						pWriter.add(
								"<span>Some Java beans have been generated for below terminal complex elements.</span><br />");
						pWriter.add(
								"<span>They must be set to the data model in order for the generated classes to work properly.</span><br />");
						pWriter.add("<ul>");
						for (SchemaNode terminalComplexNode : GenerateJavaAccessers.this.terminalComplexElements) {
							pWriter.add("<li>");
							pWriter.add("<b>"
									+ GenerateJavaAccessers.getClassSignatureForBean(
											GenerateJavaAccessers.this.getOrRegisterBean(terminalComplexNode))
									+ "</b>");
							pWriter.add(" (Path:" + terminalComplexNode.getPathInSchema().format() + " in model "
									+ SchemaUtils.getModelFileName(terminalComplexNode.getSchemaLocation()) + ")");
							pWriter.add("</li>");
						}
						pWriter.add("</ul>");
					}
				}
			});

			pWriter.add("</div>");
		}
	}

	private static final Map<String, String> TYPES_MAP = new HashMap<>();
	private static final Map<String, String> IMPORT_MAP = new HashMap<>();
	private static final String INTEGER_TYPE = SchemaTypeName.XS_INTEGER.toString();
	private static final String INT_TYPE = SchemaTypeName.XS_INT.toString();
	private static final String URI_TYPE = SchemaTypeName.XS_ANY_URI.toString();
	private static final String LOCALE_TYPE = SchemaTypeName.OSD_LOCALE.toString();
	private static final String DATE_TYPE = SchemaTypeName.XS_DATE.toString();
	private static final String DATETIME_TYPE = SchemaTypeName.XS_DATETIME.toString();
	private static final String BOOLEAN_TYPE = SchemaTypeName.XS_BOOLEAN.toString();
	private static final String BIG_DECIMAL_TYPE = SchemaTypeName.XS_DECIMAL.toString();

	static {
		TYPES_MAP.put(INTEGER_TYPE, "Integer");
		TYPES_MAP.put(INT_TYPE, "Integer");
		TYPES_MAP.put(URI_TYPE, "URI");
		TYPES_MAP.put(LOCALE_TYPE, "Locale");
		TYPES_MAP.put(DATE_TYPE, "Date");
		TYPES_MAP.put(DATETIME_TYPE, "Date");
		TYPES_MAP.put(BOOLEAN_TYPE, "Boolean");
		TYPES_MAP.put(BIG_DECIMAL_TYPE, "BigDecimal");
		IMPORT_MAP.put(BIG_DECIMAL_TYPE, "java.math.BigDecimal");
		IMPORT_MAP.put(DATE_TYPE, "java.util.Date");
		IMPORT_MAP.put(DATETIME_TYPE, "java.util.Date");
		IMPORT_MAP.put(URI_TYPE, "java.net.URI");
		IMPORT_MAP.put(LOCALE_TYPE, "java.util.Locale");
	}
	private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.US);

	private static final DateFormat DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
			DateFormat.DEFAULT, Locale.US);

	private static final String SUFFIX_DAO = "DAO";
	private static final String SUFFIX_DTO = "DTO";
	private static final String SUFFIX_MAPPER = "Mapper";
	private static final String SUFFIX_SERVICE = "Service";
	private static final String SUFFIX_REST_SERVICE = "RS";
	private static final String TO = "To";

	private static final String CR = System.lineSeparator();
	private static final String[] TAB = new String[] { "", "\t", "\t\t", "\t\t\t", "\t\t\t\t" };

	private static final String DATASET_BEAN_TYPE = "DatasetBean";
	private static final String TABLE_BEAN_TYPE = "TableBean";
	private static final String COMPLEX_TYPE_BEAN_TYPE = "ComplexTypeBean";

	private static final String SIMPLE_FIELD = "Simple";
	private static final String COMPLEX_FIELD = "Complex";
	private static final String PK_FIELD = "PK";
	private static final String FK_FIELD = "FK";
	private static final String ASSOCIATION_FIELD = "Association";

	/**
	 * Initialize the repository
	 *
	 * @param pRepository repository
	 * @param pSession    session
	 * @param pModuleName module name
	 * @throws OperationException OperationException
	 */
	public static void initRepository(final Repository pRepository, final Session pSession, final String pModuleName)
			throws OperationException {
		BeansRepositoryUtils.getOrCreateDataModelsDataset(pRepository, pSession);
	}

	private final Map<SchemaLocation, DataModel> DATA_MODELS = new HashMap<>();
	private final Map<SchemaLocation, Map<String, Table>> TABLES = new HashMap<>();
	private final Map<SchemaLocation, Map<String, Field>> FIELDS = new HashMap<>();
	private final Map<SchemaLocation, Map<String, JavaBean>> BEANS = new HashMap<>();
	private final Map<SchemaLocation, Map<String, DataAccessObject>> DAOS = new HashMap<>();
	private final Map<SchemaLocation, Map<String, List<DataTransferObject>>> DTOS = new HashMap<>();
	private final Map<SchemaLocation, Map<String, List<Service>>> SERVICES = new HashMap<>();
	private final Map<SchemaLocation, Map<String, List<RestService>>> REST_SERVICES = new HashMap<>();
	private final List<String> GENERATED_DTOS = new ArrayList<>();

	private DatasetWizardStep step = new ConfigurationStep();

	private Map<SchemaLocation, ObjectKey> objectKeys;
	private Map<SchemaLocation, Adaptation> DATASETS;
	private Set<SchemaLocation> schemas;
	private SchemaLocation schema;
	protected ProcedureContext procedureContext;
	private Adaptation DATA_MODELS_DATASET;
	private int numberOfBeans = 0;
	private int numberOfDAOs = 0;
	private int numberOfDTOs = 0;
	private int numberOfMappers = 0;
	private int numberOfServices = 0;
	private int numberOfRS = 0;
	private final List<SchemaNode> terminalComplexElements = new ArrayList<>();

	private static void checkFolderExist(final UserServiceValidateContext<DatasetEntitySelection> pContext,
			final ValueContextForInputValidation vcfip, final String folder) {
		if (StringUtils.isBlank(folder)) {
			return;
		}
		File file = new File(folder);
		if (file != null && !file.exists()) {
			vcfip.addError("No folder exists at " + file.getAbsolutePath());
		} else if (!file.isDirectory()) {
			pContext.addError(file.getAbsolutePath() + " is not a folder.");
		}
	}

	/**
	 * Generates instance variables declarations for all fields below a certain
	 * position indicated by the schema node in parameter. It identifies on the fly
	 * packages to be imported in case extra treatments are necessary such as for
	 * foreign key fields. It reports on the Wizard second step pane the eventual
	 * generation of Java Beans for complex types.
	 *
	 * @param pNode         The node from which to generate variables declaration
	 *                      for other elements.
	 * @param pExtraImports Set collecting extra packages to be imported.
	 * @param pAnnotate
	 *
	 * @return The instance variable declaration as String
	 * @throws EBXCommonsException
	 */
	private String generateAttributesForFieldsBelowForBean(final SchemaNode pNode, final Set<String> pExtraImports)
			throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();

		for (SchemaNode node : pNode.getNodeChildren()) {
			this.getOrRegisterField(node);
			if (!node.isAssociationNode()
					&& (node.isTerminalValue() && !node.isTableNode() || node.isTerminalValueDescendant())) {
				if (node.isComplex() && (node.isTerminalValue() || node.getMaxOccurs() > 1)
						&& !DamaUtils.isNodeDAC(node)) {
					this.generateBeanForComplexType(node);
				}
				if (!node.isTerminalValueDescendant() || node.isComplex() && node.getMaxOccurs() > 1
						|| node.getNodeChildren().length == 0 || DamaUtils.isNodeDAC(node)) {
					str.append(this.getAttributeDeclarationForNodeInBean(node, pExtraImports));
				}
			} else {
				str.append(this.generateAttributesForFieldsBelowForBean(node, pExtraImports));
			}
		}
		return str.toString();
	}

	/**
	 * Generates instance variables declarations for all fields below a certain
	 * position indicated by the schema node in parameter. It identifies on the fly
	 * packages to be imported in case extra treatments are necessary such as for
	 * foreign key fields. It reports on the Wizard second step pane the eventual
	 * generation of Java Beans for complex types.
	 *
	 * @param pNode         The node from which to generate variables declaration
	 *                      for other elements.
	 * @param pDTO
	 * @param pExtraImports Set collecting extra packages to be imported.
	 * @param pAnnotate
	 *
	 * @return The instance variable declaration as String
	 * @throws EBXCommonsException
	 */
	private String generateAttributesForFieldsBelowForDTO(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();

		for (SchemaNode node : pNode.getNodeChildren()) {
			if (GenerateJavaAccessers.isFieldIgnored(pNode, pDTO)) {
				continue;
			}
			if (node.isTerminalValue() && !node.isTableNode() || node.isTerminalValueDescendant()) {
				if (node.isComplex() && (node.isTerminalValue() || node.getMaxOccurs() > 1)
						&& !DamaUtils.isNodeDAC(node)) {
					this.generateDTOForComplexType(node);
				}
				if (!node.isTerminalValueDescendant() || node.isComplex() && node.getMaxOccurs() > 1
						|| node.getNodeChildren().length == 0 || DamaUtils.isNodeDAC(node)) {
					str.append(this.getAttributeDeclarationForNodeInDTO(node, pDTO, pExtraImports));
				}
			} else {
				str.append(this.generateAttributesForFieldsBelowForDTO(node, pDTO, pExtraImports));
			}
		}
		return str.toString();
	}

	private static boolean isFieldIgnored(final SchemaNode pNode, final DataTransferObject pDTO)
			throws EBXCommonsException {
		for (IgnoredField ignoredField : pDTO.getIgnoredFields()) {
			if (ignoredField.getField().getPath().equals(pNode.getPathInAdaptation().format())) {
				return true;
			}
		}
		return false;
	}

	private String generateBean(final SchemaNode pNode, final JavaBean pBean)
			throws OperationException, EBXCommonsException {

		Set<String> extraImports = new HashSet<>();

		StringBuilder str = new StringBuilder();
		str.append("/**" + CR);
		str.append("* Java Bean of type " + pBean.getType()
				+ " automatically generated by service 'Generate Java Accessers' of EBX Commons" + CR);
		str.append("* It represents node at path " + pNode.getPathInSchema().format() + " in data model "
				+ pNode.getSchemaLocation().format() + "." + CR);
		str.append("*" + CR);
		str.append("* @author EBX Commons" + CR);
		str.append("*/" + CR);
		str.append("public class " + pBean.getJavaClassName() + " extends " + pBean.getType() + " {" + CR);
		str.append(CR);
		str.append(this.generateAttributesForFieldsBelowForBean(pNode, extraImports));
		str.append(CR);
		str.append(GenerateJavaAccessers.generateBeanConstructor(pBean));
		str.append(CR);
		str.append(this.generateBeanAccessersForFieldsBelow(pNode, pBean, extraImports));
		if (pNode.getPathInAdaptation().isRoot() && pNode.isTableOccurrenceNode()) {
			str.append(this.generateGetDAO(pNode, extraImports));
			str.append(CR);
		}
		str.append(this.generateHashCodeAndEquals(pNode, pBean.getJavaClassName(), extraImports));
		str.append("}");

		StringBuilder classContent = new StringBuilder();
		classContent.append("package " + pBean.getJavaPackage() + ";" + CR);
		classContent.append(CR);
		classContent.append("import com.tibco.ebx.cs.commons.beans.generator.template." + pBean.getType() + ";" + CR);
		GenerateJavaAccessers.writeExtraImports(pBean.getJavaPackage(), extraImports, classContent);
		classContent.append(CR);
		classContent.append(str);
		this.numberOfBeans++;
		return classContent.toString();
	}

	private static Object generateBeanConstructor(final JavaBean pBean) {
		StringBuilder str = new StringBuilder();
		str.append(TAB[1] + "/**" + CR);
		str.append(TAB[1] + "* Default constructor" + CR);
		str.append(TAB[1] + "*/" + CR);
		str.append(TAB[1] + "public " + pBean.getJavaClassName() + "(){" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private static void writeExtraImports(final String pCurrentPackage, final Set<String> pExtraImports,
			final StringBuilder pStringBuilder) {
		for (String extraImport : pExtraImports) {
			if (!pCurrentPackage.equals(extraImport.substring(0, extraImport.lastIndexOf(".")))) {
				pStringBuilder.append("import " + extraImport + ";" + CR);
			}
		}
	}

	/**
	 * Generates accessers for all fields below a certain position indicated by the
	 * schema node in parameter. It identifies on the fly packages to be imported in
	 * case extra treatments are necessary such as for foreign key fields.
	 *
	 * @param pNode         The node from which to generate accessers for other
	 *                      elements.
	 * @param pExtraImports Set collecting extra packages to be imported.
	 * @return The getter as String
	 * @throws OperationException
	 */
	private String generateBeanAccessersForFieldsBelow(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();

		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.isAssociationNode()) {
				str.append(this.generateBeanGetterForField(node, pBean, pExtraImports));
				str.append(CR);
			} else if (node.isTerminalValue() && !node.isTableNode() || SchemaUtils.isTerminalUnderTerminal(node)) {
				str.append(this.generateBeanGetterForField(node, pBean, pExtraImports));
				str.append(CR);
				str.append(this.generateBeanSetterForField(node, pExtraImports));
				str.append(CR);
			} else {
				str.append(this.generateBeanAccessersForFieldsBelow(node, pBean, pExtraImports));
			}
		}

		return str.toString();
	}

	/**
	 * Generates a Java Bean for the complex type of the schema node in parameter.
	 * It reports on the Wizard second step pane the generation of Java Beans for
	 * complex types.
	 *
	 * @param pNode The node of complex type for which to generate a Java Bean.
	 * @throws EBXCommonsException
	 *
	 */
	private void generateBeanForComplexType(final SchemaNode pNode) throws OperationException, EBXCommonsException {
		JavaBean bean = this.getOrRegisterBean(pNode);
		if (bean.getGenerationActive().booleanValue()) {
			String classContent = this.generateBean(pNode, bean);
			GenerateJavaAccessers.writeJavaFile(this.DATA_MODELS.get(pNode.getSchemaLocation()).getBeansSourceFolder(),
					bean.getJavaPackage(), bean.getJavaClassName(), classContent);
		}
		Class<?> javaBean = pNode.getJavaBeanClass();
		if (javaBean == null
				|| !javaBean.getCanonicalName().equals(GenerateJavaAccessers.getClassSignatureForBean(bean))) {
			this.terminalComplexElements.add(pNode);
		}
	}

	/**
	 * Generates a Java Bean for every table in the dataset in parameter. It reports
	 * on the Wizard second step pane the generation of Java Beans for tables and
	 * complex types.
	 *
	 * @return
	 * @throws EBXCommonsException
	 * @throws UnavailableContentError
	 *
	 * @pWriter Writer allowing to write in generation report.
	 *
	 */
	private JavaBean generateBeanForSchema(final SchemaLocation pSchemaLocation)
			throws OperationException, UnavailableContentError, EBXCommonsException {
		JavaBean bean = this.getOrRegisterBean(this.DATASETS.get(pSchemaLocation).getSchemaNode());
		if (Boolean.TRUE.equals(bean.getGenerationActive()) || bean.getGenerationActive() == null) {
			String classContent = this.generateBean(this.DATASETS.get(pSchemaLocation).getSchemaNode(), bean);
			GenerateJavaAccessers.writeJavaFile(this.DATA_MODELS.get(pSchemaLocation).getBeansSourceFolder(),
					bean.getJavaPackage(), bean.getJavaClassName(), classContent);
		}
		return bean;
	}

	/**
	 * Generates a Java Bean for the table node in parameter. It reports on the
	 * Wizard second step pane the generation of Java Beans for the table and
	 * complex types.
	 *
	 * @param pTable.getTableNode() The table node for which to generate a Java
	 *                              Bean.
	 * @return
	 *
	 */
	private JavaBean generateBeanForTable(final AdaptationTable pTable) throws OperationException, EBXCommonsException {
		JavaBean bean = this.getOrRegisterBean(pTable.getTableNode());
		if (bean.getGenerationActive().booleanValue()) {
			String classContent = this.generateBean(pTable.getTableOccurrenceRootNode(), bean);
			GenerateJavaAccessers.writeJavaFile(
					this.DATA_MODELS.get(pTable.getTableNode().getSchemaLocation()).getBeansSourceFolder(),
					bean.getJavaPackage(), bean.getJavaClassName(), classContent);
		}
		return bean;
	}

	/**
	 * Generates the getter of a field. It identifies on the fly packages to be
	 * imported in case extra treatments are necessary such as for foreign key
	 * fields.
	 *
	 * @param pNode         The terminal node for which to generate a getter method.
	 * @param pExtraImports Set collecting extra packages to be imported.
	 *
	 * @return A getter method as String.
	 * @throws OperationException
	 *
	 */
	private String generateBeanGetterForField(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports) throws EBXCommonsException {
		StringBuilder str = new StringBuilder();
		String attributeName = GenerateJavaAccessers.getAttributeNameForNode(pNode);
		String functionName = GenerateJavaAccessers.getGetterNameForNode(pNode);
		str.append(TAB[1] + "public " + this.getJavaTypeForNodeInBean(pNode, false, pExtraImports) + " " + functionName
				+ "()");
		if (pNode.getFacetOnTableReference() != null) {
			str.append(" throws EBXCommonsException ");
			pExtraImports.add("com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException");
		}
		str.append("{" + CR);

		DataAccessObject localDAO = null;
		if (pNode.getTableNode() != null) {
			localDAO = this.getOrRegisterDAO(pNode.getTableNode());
		} else {
			localDAO = this.getOrRegisterDAO(pNode.getNode(SchemaUtils.getPathToRecordRoot(pNode)));
		}
		if (pNode.getFacetOnTableReference() != null) {
			DataAccessObject distantDAO = this.getOrRegisterDAO(pNode.getFacetOnTableReference().getTableNode());
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(localDAO));
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(distantDAO));
			str.append(TAB[2] + "if(this." + attributeName + " == null"
					+ (pNode.getMaxOccurs() > 1 ? "" : " && isBoundToEBX()") + "){" + CR);
			str.append(TAB[3] + "this." + attributeName + " = " + distantDAO.getJavaClassName() + ".getInstance().");
			if (pNode.getMaxOccurs() > 1) {
				str.append("readAll(this, " + localDAO.getJavaClassName() + "."
						+ GenerateJavaAccessers.getConstantNameForPathToField(pNode) + ");" + CR);
			} else {
				str.append("read(this, " + localDAO.getJavaClassName() + "."
						+ GenerateJavaAccessers.getConstantNameForPathToField(pNode) + ").orElse(null);" + CR);
			}
			str.append(TAB[2] + "}" + CR);
			str.append(TAB[2] + "return this." + attributeName + ";" + CR);
			str.append(TAB[1] + "}" + CR);
		} else if (pNode.isAssociationNode()) {
			pExtraImports.add("java.util.Optional");
			DataAccessObject distantDAO = this.getOrRegisterDAO(pNode.getAssociationLink().getTableNode());
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(localDAO));
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(distantDAO));
			str.append(TAB[2] + "return " + distantDAO.getJavaClassName() + ".getInstance().readAll(this, "
					+ localDAO.getJavaClassName() + "." + GenerateJavaAccessers.getConstantNameForPathToField(pNode)
					+ ");" + CR);
			str.append(TAB[1] + "}" + CR);
			str.append(CR);
			str.append(TAB[1] + "public " + this.getJavaTypeForNodeInBean(pNode, false, pExtraImports) + " "
					+ functionName + "(final String predicate) {" + CR);
			str.append(TAB[2] + "return " + distantDAO.getJavaClassName() + ".getInstance().readAll(this, "
					+ localDAO.getJavaClassName() + "." + GenerateJavaAccessers.getConstantNameForPathToField(pNode)
					+ ", Optional.of(predicate));" + CR);
			str.append(TAB[1] + "}" + CR);
		} else {
			if (pNode.isValueFunction()) {
				String setValue = GenerateJavaAccessers.getSetterNameForNode(pNode);
				String javaType = this.getJavaTypeForNodeInBean(pNode, false, pExtraImports);
				pExtraImports.add("com.orchestranetworks.schema.Path");
				String relativePath = "Path.parse(\"" + SchemaUtils.getPathToRecordRoot(pNode).format() + "\").add("
						+ localDAO.getJavaClassName() + "." + GenerateJavaAccessers.getConstantNameForPathToField(pNode)
						+ ")";
				str.append(TAB[2] + "if(this.ebxRecord != null){" + CR);
				str.append(
						TAB[3] + "this." + setValue + "((" + javaType + ") ebxRecord.get(" + localDAO.getJavaClassName()
								+ "." + GenerateJavaAccessers.getConstantNameForPathToField(pNode) + "));\n");
				str.append(TAB[2] + "} else if(this.ebxContext != null){" + CR);
				str.append(
						TAB[3] + setValue + "((" + javaType + ") this.ebxContext.getValue(" + relativePath + "));\n");
				str.append(TAB[2] + "}" + CR);
			}
			str.append(TAB[2] + "return this." + attributeName + ";" + CR);
			str.append(TAB[1] + "}" + CR);
		}
		return str.toString();
	}

	/**
	 * Generates the setter of a field. It identifies on the fly packages to be
	 * imported in case extra treatments are necessary such as for foreign key
	 * fields.
	 *
	 * @param pNode         The terminal node for which to generate a setter method.
	 * @param pExtraImports Set collecting extra packages to be imported.
	 *
	 * @return A setter method as String.
	 * @throws EBXCommonsException EBXCommonsException
	 *
	 */
	private String generateBeanSetterForField(final SchemaNode pNode, final Set<String> pExtraImports)
			throws EBXCommonsException {
		StringBuilder str = new StringBuilder();
		String attributeName = GenerateJavaAccessers.getAttributeNameForNode(pNode);
		str.append(TAB[1] + "public void " + GenerateJavaAccessers.getSetterNameForNode(pNode) + "(final "
				+ this.getJavaTypeForNodeInBean(pNode, false, pExtraImports) + " " + attributeName + "){" + CR);
		if (pNode.getInheritanceProperties() != null) {
			str.append(TAB[2] + "this.overwrite(this.getDAO()."
					+ GenerateJavaAccessers.getConstantNameForPathToField(pNode) + ");" + CR);
		}
		str.append(TAB[2] + "this." + attributeName + " = " + attributeName + ";" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private void generateDatasetDAO(final SchemaLocation pSchemaLocation)
			throws OperationException, EBXCommonsException {

		DataModel dataModel = this.DATA_MODELS.get(pSchemaLocation);
		Adaptation dataset = this.DATASETS.get(pSchemaLocation);
		DataAccessObject dao = this.getOrRegisterDAO(dataset.getSchemaNode());
		Set<String> extraImports = new HashSet<>();

		StringBuilder str = new StringBuilder();
		str.append("/**" + CR);
		str.append(
				"* Data access object (DA) automatically generated by service 'Generate Java Accessers' of EBX Commons"
						+ CR);
		str.append("* It aims to access data and table of any dataset relying on data model " + pSchemaLocation.format()
				+ "." + CR);
		str.append("*" + CR);
		str.append("* @author EBX Commons" + CR);
		str.append("*/" + CR);
		str.append("public class " + dao.getJavaClassName() + " extends DatasetDAO<"
				+ dao.getJavaBean().getJavaClassName() + ">{" + CR);
		str.append(CR);
		str.append(GenerateJavaAccessers.generateDatasetDaoConstructor(dao));
		str.append(CR);
		str.append(this.generatePathsAsConstantsForFieldsBelow(dataset.getSchemaNode()));
		str.append(CR);
		str.append(this.generatePathsAsConstantsForTablesBelow(dataset.getSchemaNode()));
		str.append(CR);
		str.append(this.generateSetValuesForUpdate(dataset.getSchemaNode(), dao.getJavaBean(), extraImports));
		str.append(CR);
		str.append(this.generateGetValuesFromAdaptation(dataset.getSchemaNode(), dao.getJavaBean(), extraImports));
		str.append(CR);
		str.append(this.generateGetValuesFromValueContext(dataset.getSchemaNode(), dao.getJavaBean(), extraImports));
		str.append(CR);
		str.append(this.generateGetInstanceOfBean(dataset.getSchemaNode(), dao.getJavaBean(), extraImports));
		str.append(CR);
		for (AdaptationTable table : AdaptationUtils.getAllTables(dataset)) {
			this.getOrRegisterTable(table.getTableNode());
			this.generateBeanForTable(table);
			this.generateTableDAO(table, dao);

			if (!StringUtils.isBlank(dataModel.getDtoSourceFolder())
					&& !StringUtils.isBlank(dataModel.getDtoPackage())) {
				for (DataTransferObject dto : this.getOrRegisterDTO(table.getTableNode())) {
					if (dto.getGenerationActive().booleanValue()) {
						this.generateDTOForTable(table, dto);
					}
				}
				if (!StringUtils.isBlank(dataModel.getServicesSourceFolder())
						&& !StringUtils.isBlank(dataModel.getServicesPackage())) {
					for (Service service : this.getOrRegisterService(table.getTableNode())) {
						if (service.getGenerationActive().booleanValue()) {
							this.generateTableService(table, service);
						}
					}
					if (!StringUtils.isBlank(dataModel.getRsSourceFolder())
							&& !StringUtils.isBlank(dataModel.getRsPackage())) {
						for (RestService rs : this.getOrRegisterRestServices(table.getTableNode())) {
							if (rs.getGenerationActive().booleanValue()) {
								this.generateTableRS(table, rs);
							}
						}
					}
				}
			}

			str.append(GenerateJavaAccessers.generateTableGetter(table));
			str.append(CR);
		}
		str.append("}");

		StringBuilder classContent = new StringBuilder();
		classContent.append("package " + dao.getJavaPackage() + ";" + CR);
		classContent.append(CR);
		classContent.append("import com.onwbp.adaptation.Adaptation;" + CR);
		classContent.append("import com.onwbp.adaptation.AdaptationTable;" + CR);
		classContent.append("import com.orchestranetworks.schema.Path;" + CR);
		classContent.append("import com.tibco.ebx.cs.commons.beans.generator.template.DatasetDAO;" + CR);
		classContent.append("import com.orchestranetworks.instance.ValueContext;" + CR);
		classContent.append("import com.orchestranetworks.service.ValueContextForUpdate;" + CR);
		classContent.append("import " + GenerateJavaAccessers.getClassSignatureForBean(dao.getJavaBean()) + ";" + CR);
		GenerateJavaAccessers.writeExtraImports(dao.getJavaPackage(), extraImports, classContent);
		classContent.append(CR);
		classContent.append(str);
		this.numberOfDAOs++;
		GenerateJavaAccessers.writeJavaFile(dataModel.getDaoSourceFolder(), dao.getJavaPackage(),
				dao.getJavaClassName(), classContent.toString());
	}

	private static Object generateDatasetDaoConstructor(final DataAccessObject pDao) {
		StringBuilder str = new StringBuilder();
		str.append(TAB[1] + "public " + pDao.getJavaClassName() + "(final Adaptation pDataset) {" + CR);
		str.append(TAB[2] + "super(pDataset);" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private String generateDTO(final SchemaNode pNode, final DataTransferObject pDTO)
			throws OperationException, EBXCommonsException {
		Set<String> extraImports = new HashSet<>();

		JavaBean bean = this.getOrRegisterBean(pNode);
		StringBuilder str = new StringBuilder();
		str.append("/**" + CR);
		str.append(
				"* Data transfer object (DTO) automatically generated by service 'Generate Java Accessers' of EBX Commons"
						+ CR);
		str.append("* It represents node at path " + pNode.getPathInSchema().format() + " in data model "
				+ pNode.getSchemaLocation().format() + "." + CR);
		str.append("*" + CR);
		str.append("* It can be turned into or build from the Java Bean {@link " + bean.getJavaClassName()
				+ "} with Mapper {@link " + GenerateJavaAccessers.getClassSignatureForMapper(pDTO) + "} " + CR);
		str.append("*" + CR);
		str.append("* @author EBX Commons" + CR);
		str.append("*/" + CR);

		str.append("@Schema(name = \"" + pDTO.getJavaClassName() + "\", description = \"Object representing "
				+ bean.getJavaClassName() + "\")" + CR);
		str.append("public class " + pDTO.getJavaClassName() + " implements ");

		if (bean.getSpecific().booleanValue()) {
			str.append("SpecificComplexTypeDTO");
			extraImports.add("com.tibco.ebx.cs.commons.beans.generator.template.SpecificComplexTypeDTO");
		} else if (pNode.getPathInAdaptation().equals(Path.ROOT)) {
			str.append("TableDTO");
			extraImports.add("com.tibco.ebx.cs.commons.beans.generator.template.TableDTO");
		} else if (pNode.isTableOccurrenceNode()) {
			str.append("ComplexTypeDTO");
			extraImports.add("com.tibco.ebx.cs.commons.beans.generator.template.ComplexTypeDTO");
		} else {
			str.append("DatasetDTO");
			extraImports.add("com.tibco.ebx.cs.commons.beans.generator.template.DatasetDTO");
		}
		str.append("<" + bean.getJavaClassName() + "> {" + CR);
		extraImports.add(GenerateJavaAccessers.getClassSignatureForBean(bean));
		str.append(CR);
		SchemaNode startNode = pNode;
		if (startNode.isTableNode()) {
			startNode = startNode.getTableOccurrenceRootNode();
		}
		str.append(this.generateAttributesForFieldsBelowForDTO(startNode, pDTO, extraImports));
		str.append(CR);
		str.append(GenerateJavaAccessers.generateDTOConstructors(pNode, pDTO, extraImports));
		str.append(CR);
		str.append(this.generateDTOAccessersForFieldsBelow(startNode, pDTO, extraImports));
		if (pNode.getPathInAdaptation().equals(Path.ROOT)) {
			str.append(this.generateGetDAO(pNode, extraImports));
			str.append(CR);
		}
		str.append(this.generateHashCodeAndEquals(pNode, pDTO.getJavaClassName(), extraImports));

		str.append("}");

		StringBuilder classContent = new StringBuilder();
		classContent.append("package " + pDTO.getJavaPackage() + ";" + CR);
		classContent.append(CR);
		classContent.append("import io.swagger.v3.oas.annotations.media.Schema;" + CR);
		GenerateJavaAccessers.writeExtraImports(pDTO.getJavaPackage(), extraImports, classContent);
		classContent.append(CR);
		classContent.append(str);
		this.numberOfDTOs++;
		this.generateMapper(pNode, pDTO);
		return classContent.toString();
	}

	private void generateMapper(final SchemaNode pNode, final DataTransferObject pDTO)
			throws OperationException, EBXCommonsException {
		Set<String> extraImports = new HashSet<>();

		JavaBean bean = this.getOrRegisterBean(pNode);
		StringBuilder str = new StringBuilder();
		str.append("/**" + CR);
		str.append("* Mapper between a {@link " + bean.getJavaClassName() + "} and a {@link " + pDTO.getJavaClassName()
				+ "} automatically generated by service 'Generate Java Accessers' of EBX Commons" + CR);
		str.append("*" + CR);
		str.append("* @author EBX Commons" + CR);
		str.append("*/" + CR);
		str.append("public class " + pDTO.getMapper().getJavaClassName() + " extends BeanToDTOMapper<"
				+ bean.getJavaClassName() + "," + pDTO.getJavaClassName() + ">{" + CR);
		extraImports.add(GenerateJavaAccessers.getClassSignatureForBean(bean));
		extraImports.add(GenerateJavaAccessers.getClassSignatureForDTO(pDTO));
		str.append(CR);
		str.append(TAB[1] + "private static final " + pDTO.getMapper().getJavaClassName() + " instance = new "
				+ pDTO.getMapper().getJavaClassName() + "();" + CR);
		str.append(CR);
		str.append(GenerateJavaAccessers.generateGetInstance(pDTO.getMapper().getJavaClassName()));
		str.append(CR);
		str.append(this.generateGetDTO(pNode, pDTO, extraImports));
		str.append(CR);
		str.append(this.generateGetBean(pNode, pDTO, extraImports));

		str.append("}");

		StringBuilder classContent = new StringBuilder();
		classContent.append("package " + pDTO.getMapper().getJavaPackage() + ";" + CR);
		classContent.append(CR);
		classContent.append("import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;" + CR);
		classContent.append("import com.tibco.ebx.cs.commons.beans.generator.template.BeanToDTOMapper;" + CR);
		GenerateJavaAccessers.writeExtraImports(pDTO.getMapper().getJavaPackage(), extraImports, classContent);
		classContent.append(CR);
		classContent.append(str);
		GenerateJavaAccessers.writeJavaFile(this.DATA_MODELS.get(pNode.getSchemaLocation()).getMappersSourceFolder(),
				pDTO.getMapper().getJavaPackage(), pDTO.getMapper().getJavaClassName(), classContent.toString());
		this.numberOfMappers++;
	}

	private String generateDTOAccessersForFieldsBelow(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();

		for (SchemaNode node : pNode.getNodeChildren()) {
			if (!node.isAssociationNode() && (node.isTerminalValue() && !node.isTableNode()
					|| node.isTerminalValueDescendant() && (node.isComplex() && node.getMaxOccurs() > 1
							|| node.getNodeChildren().length == 0 || DamaUtils.isNodeDAC(node)))) {
				str.append(this.generateDTOGetterForField(node, pDTO, pExtraImports));
				str.append(CR);
				str.append(this.generateDTOSetterForField(node, pDTO, pExtraImports));
				str.append(CR);
			} else {
				str.append(this.generateDTOAccessersForFieldsBelow(node, pDTO, pExtraImports));
			}
		}

		return str.toString();
	}

	private static String generateDTOConstructors(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) {
		StringBuilder str = new StringBuilder();
		str.append("/**" + CR);
		str.append("* Default Constructor" + CR);
		str.append("*/" + CR);
		str.append(TAB[1] + "public " + pDTO.getJavaClassName() + "(){" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private String generateGetDTO(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		JavaBean bean = this.getOrRegisterBean(pNode);
		String parameterName = "pBean";
		str.append(TAB[1] + "public " + pDTO.getJavaClassName() + " getDTO(final " + bean.getJavaClassName() + " "
				+ parameterName + ") throws EBXCommonsException {" + CR);
		String variableName = StringUtils.uncapitalize(pDTO.getJavaClassName());
		str.append(TAB[2] + pDTO.getJavaClassName() + " " + variableName + " = new " + pDTO.getJavaClassName() + "();"
				+ CR);
		if (pNode.isTableNode()) {
			str.append(this.setValuesFromBeanForDTOForFieldsBelow(pNode.getTableOccurrenceRootNode(), pDTO,
					pExtraImports));
		} else {
			str.append(this.setValuesFromBeanForDTOForFieldsBelow(pNode, pDTO, pExtraImports));
		}
		str.append(TAB[2] + "return " + variableName + ";" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	/**
	 * Generates a Java Bean for the complex type of the schema node in parameter.
	 * It reports on the Wizard second step pane the generation of Java Beans for
	 * complex types.
	 *
	 * @param pNode The node of complex type for which to generate a Java Bean.
	 * @return
	 *
	 */
	private void generateDTOForComplexType(final SchemaNode pNode) throws OperationException, EBXCommonsException {
		for (DataTransferObject dto : this.getOrRegisterDTO(pNode)) {
			String dtoLocation = this.DATA_MODELS.get(pNode.getSchemaLocation()).getDtoSourceFolder() + "/"
					+ dto.getJavaPackage().replaceAll("\\.", "/") + "/" + dto.getJavaClassName();
			if (!this.GENERATED_DTOS.contains(dtoLocation)) {
				String classContent = this.generateDTO(pNode, dto);
				GenerateJavaAccessers.writeJavaFile(
						this.DATA_MODELS.get(pNode.getSchemaLocation()).getDtoSourceFolder(), dto.getJavaPackage(),
						dto.getJavaClassName(), classContent);
				this.GENERATED_DTOS.add(dtoLocation);
			}
		}
	}

	private Object generateDTOGetterForField(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws EBXCommonsException {
		StringBuilder str = new StringBuilder();
		String attributeName = GenerateJavaAccessers.getAttributeNameForNode(pNode);
		String functionName = GenerateJavaAccessers.getGetterNameForNode(pNode);
		DataTransferObject includedDTO = null;
		if (pNode.isAssociationNode() || pNode.getFacetOnTableReference() != null) {
			includedDTO = this.getDTOInclusion(pNode, pDTO);
		}
		str.append(TAB[1] + "public " + this.getJavaTypeForNodeInDTO(pNode, false, includedDTO, pExtraImports) + " "
				+ functionName + "(){" + CR);
		if (!pNode.isAssociationNode()) {
			str.append(TAB[2] + "return this." + attributeName + ";" + CR);
		}
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private Object generateDTOSetterForField(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws EBXCommonsException {
		StringBuilder str = new StringBuilder();
		String attributeName = GenerateJavaAccessers.getAttributeNameForNode(pNode);
		String functionName = "set" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		DataTransferObject includedDTO = null;
		if (pNode.isAssociationNode() || pNode.getFacetOnTableReference() != null) {
			includedDTO = this.getDTOInclusion(pNode, pDTO);
		}
		str.append(TAB[1] + "public void " + functionName + "(final "
				+ this.getJavaTypeForNodeInDTO(pNode, false, includedDTO, pExtraImports) + " " + attributeName + "){"
				+ CR);
		str.append(TAB[2] + "this." + attributeName + " = " + attributeName + ";" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	/**
	 * Generates part of the "equals" method for a given attribute name.
	 *
	 * @param attributeName - name of the attribute
	 * @return
	 */
	private static String generateEqualsForAttribute(final String attributeName) {
		return "Objects.equals(" + attributeName + ", other." + attributeName + ")";
	}

	private static Object generateGetAdaptationTable(final SchemaNode pTableNode, final JavaBean pBean,
			final Set<String> pExtraImports) {
		StringBuilder str = new StringBuilder();
		pExtraImports.add("com.onwbp.adaptation.AdaptationTable");
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "protected AdaptationTable getAdaptationTable(final Adaptation pDataset) {" + CR);
		str.append(TAB[2] + "return this.getDatasetDAO(pDataset)."
				+ GenerateJavaAccessers.getTableGetterName(pTableNode) + "();" + CR);
		str.append(TAB[1] + "}" + CR);
		return str;
	}

	private Object generateGetBean(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		JavaBean bean = this.getOrRegisterBean(pNode);
		String variableName = StringUtils.uncapitalize(bean.getJavaClassName());
		pExtraImports.add("com.onwbp.adaptation.Adaptation");
		pExtraImports.add("com.orchestranetworks.service.SessionPermissions");
		pExtraImports.add("java.util.Optional");
		str.append(TAB[1] + "public " + bean.getJavaClassName() + " getBean(final Adaptation pDataset, final "
				+ pDTO.getJavaClassName()
				+ " pDTO, final Optional<SessionPermissions> pPermissions)  throws EBXCommonsException {" + CR);
		if (!pNode.isTableOccurrenceNode()) {
			str.append(TAB[2] + bean.getJavaClassName() + " " + variableName
					+ " = getDAO().getInstanceOfBean(pDataset);" + CR);
			str.append(this.setValuesFromDTOForBeanForFieldsBelow(pNode.getTableOccurrenceRootNode(), pDTO,
					pExtraImports));
		} else {
			str.append(TAB[2] + bean.getJavaClassName() + " " + variableName + " = new " + bean.getJavaClassName()
					+ "();" + CR);
			str.append(this.setValuesFromDTOForBeanForFieldsBelow(pNode, pDTO, pExtraImports));
		}
		str.append(TAB[2] + "return " + variableName + ";" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private String generateGetDAO(final SchemaNode pNode, final Set<String> pExtraImports) throws EBXCommonsException {
		StringBuilder str = new StringBuilder();
		DataAccessObject dao = this.getOrRegisterDAO(pNode);
		pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(dao));
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "protected " + dao.getJavaClassName() + " getDAO() {" + CR);
		str.append(TAB[2] + "return " + dao.getJavaClassName() + ".getInstance();" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private static Object generateGetDatasetDAO(final DataAccessObject pDatasetDAO) {
		StringBuilder str = new StringBuilder();
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "protected " + pDatasetDAO.getJavaClassName()
				+ " getDatasetDAO(final Adaptation pDataset) {" + CR);
		str.append(TAB[2] + "return new " + pDatasetDAO.getJavaClassName() + "(pDataset);" + CR);
		str.append(TAB[1] + "}" + CR);
		return str;
	}

	private String generateGetDefaultValuesForFieldsBelow(final SchemaNode pNode, final Set<String> pExtraImports)
			throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.getMaxOccurs() < 2 && !node.isAssociationNode()
					&& (node.isTerminalValue() || SchemaUtils.isTerminalUnderTerminal(pNode))) {
				if (node.getDefaultValue() != null) {

					if (node.getFacetOnTableReference() != null) {
						str.append(TAB[2] + "if(pDataset != null){" + CR);
						DataAccessObject distantDAO = this
								.getOrRegisterDAO(node.getFacetOnTableReference().getTableNode());
						pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(distantDAO));
						if (node.getFacetOnTableReference().getContainerReference() != null) {
							pExtraImports.add("com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils");
							String datasetVariableName = GenerateJavaAccessers.getAttributeNameForNode(node)
									+ "Dataset";
							if (node.getFacetOnTableReference().getContainerHome() != null) {
								str.append(TAB[3] + "Adaptation " + datasetVariableName
										+ " = AdaptationUtils.getDataset(pDataset.getHome().getRepository(), \""
										+ node.getFacetOnTableReference().getContainerHome().getName() + "\", \""
										+ node.getFacetOnTableReference().getContainerReference().getStringName()
										+ "\");" + CR);
							} else {
								str.append(TAB[3] + "Adaptation " + datasetVariableName
										+ " = AdaptationUtils.getDataset(pDataset.getHome(), \""
										+ node.getFacetOnTableReference().getContainerReference().getStringName()
										+ "\");" + CR);
							}
							str.append(TAB[3] + "instance." + GenerateJavaAccessers.getSetterNameForNode(node) + "("
									+ distantDAO.getJavaClassName() + ".getInstance().read(\"" + node.getDefaultValue()
									+ "\", " + datasetVariableName + ").orElse(null));" + CR);
						} else {
							str.append(TAB[3] + "instance." + GenerateJavaAccessers.getSetterNameForNode(node) + "("
									+ distantDAO.getJavaClassName() + ".getInstance().read(\"" + node.getDefaultValue()
									+ "\", pDataset).orElse(null));" + CR);
						}
						str.append(TAB[2] + "}" + CR);
					} else {
						str.append(TAB[2] + "instance." + GenerateJavaAccessers.getSetterNameForNode(node) + "(");

						if (INTEGER_TYPE.equals(node.getXsTypeName().toString())) {
							str.append(node.getDefaultValue() + ");" + CR);
						} else if (INT_TYPE.equals(node.getXsTypeName().toString())) {
							str.append(node.getDefaultValue() + ");" + CR);
						} else if (URI_TYPE.equals(node.getXsTypeName().toString())) {
							str.append("URI.create(" + node.getDefaultValue() + "));" + CR);
						} else if (LOCALE_TYPE.equals(node.getXsTypeName().toString())) {
							str.append("Locale.forLanguageTag(" + node.getDefaultValue() + "));" + CR);
						} else if (DATE_TYPE.equals(node.getXsTypeName().toString())) {
							pExtraImports.add("java.text.*");
							str.append("DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.US).parse(\""
									+ DATE_FORMAT.format((Date) node.getDefaultValue()) + "\"));" + CR);
						} else if (DATETIME_TYPE.equals(node.getXsTypeName().toString())) {
							pExtraImports.add("java.text.*");
							str.append(
									"DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US).parse(\""
											+ DATETIME_FORMAT.format((Date) node.getDefaultValue()) + "\"));" + CR);
						} else if (BOOLEAN_TYPE.equals(node.getXsTypeName().toString())) {
							if (((Boolean) node.getDefaultValue()).booleanValue()) {
								str.append("Boolean.TRUE);" + CR);
							} else {
								str.append("Boolean.FALSE);" + CR);
							}
						} else if (BIG_DECIMAL_TYPE.equals(node.getXsTypeName().toString())) {
							str.append("new BigDecimal(\"" + node.getDefaultValue() + "\"));" + CR);
						} else {
							str.append("\"" + node.getDefaultValue() + "\");" + CR);
						}
					}
				} else if (node.getInheritanceProperties() != null) {
					str.append(TAB[2] + "instance.inherit(" + GenerateJavaAccessers.getConstantNameForPathToField(node)
							+ ");" + CR);
				}
			} else {
				str.append(this.generateGetDefaultValuesForFieldsBelow(node, pExtraImports));
			}
		}

		return str.toString();
	}

	private static Object generateGetInstance(final String pJavaClassName) {
		StringBuilder str = new StringBuilder();
		str.append(TAB[1] + "public static " + pJavaClassName + " getInstance() {" + CR);
		str.append(TAB[2] + "return instance;" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private String generateGetInstanceOfBean(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "public " + pBean.getJavaClassName() + " getInstanceOfBean(){" + CR);
		str.append(TAB[2] + pBean.getJavaClassName() + " instance = new " + pBean.getJavaClassName() + "();" + CR);
		str.append(TAB[2] + "return instance;" + CR);
		str.append(TAB[1] + "}" + CR + CR);
		str.append(TAB[1] + "@Override" + CR);
		str.append(
				TAB[1] + "public " + pBean.getJavaClassName() + " getInstanceOfBean(final Adaptation pDataset){" + CR);
		str.append(TAB[2] + pBean.getJavaClassName() + " instance = new " + pBean.getJavaClassName() + "();" + CR);
		str.append(this.generateGetDefaultValuesForFieldsBelow(pNode, pExtraImports));
		str.append(TAB[2] + "return instance;" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private static Object generateGetPrimaryKeysGetters(final AdaptationTable pTable) {
		StringBuilder str = new StringBuilder();
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "protected String[] getPrimaryKeysGetters() {" + CR);
		str.append(
				TAB[2] + "return new String[] { " + GenerateJavaAccessers.getPrimaryKeysGetters(pTable) + " };" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private Object generateGetPrimaryKeysSetters(final AdaptationTable pTable, final Set<String> pExtraImports)
			throws EBXCommonsException {
		pExtraImports.add("java.util.List");
		pExtraImports.add("java.util.ArrayList");
		pExtraImports.add("com.tibco.ebx.cs.commons.beans.generator.exception.BeansTechnicalException");
		StringBuilder str = new StringBuilder();
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "protected List<PrimaryKeySetter> getPrimaryKeysSetters() throws BeansTechnicalException {"
				+ CR);
		str.append(TAB[2] + "List<PrimaryKeySetter> setters = new ArrayList<>();" + CR);
		str.append(TAB[2] + "try {" + CR);
		for (Path path : pTable.getPrimaryKeySpec()) {
			SchemaNode pkNode = pTable.getTableOccurrenceRootNode().getNode(Path.SELF.add(path));
			String beanClassName = GenerateJavaAccessers.getJavaClassName(pTable.getTableNode());
			String setterName = GenerateJavaAccessers.getSetterNameForNode(pkNode);
			String parameterClassName = this.getJavaTypeForNodeInBean(pkNode, false, pExtraImports);
			str.append(TAB[3] + "setters.add(new PrimaryKeySetter(" + beanClassName + ".class.getMethod(\"" + setterName
					+ "\", " + parameterClassName + ".class), Optional.");
			if (pkNode.getFacetOnTableReference() == null) {
				str.append("empty()));" + CR);
			} else {
				str.append("of(" + parameterClassName + ".class)));" + CR);
			}
		}
		str.append(TAB[2] + "} catch (NoSuchMethodException ex) {" + CR);
		str.append(TAB[3] + "throw new BeansTechnicalException(ex);" + CR);
		str.append(TAB[2] + "}" + CR);
		str.append(TAB[2] + "return setters;" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();

	}

	private Object generateGetValuesFromAdaptation(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		String className = pBean.getJavaClassName();
		pExtraImports.add("com.orchestranetworks.service.SessionPermissions");
		pExtraImports.add("java.util.Optional");
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "protected void getValuesFromAdaptation(final " + className + " p" + className
				+ ", final Adaptation pRecord, final Optional<SessionPermissions> pPermissions) {" + CR);
		str.append(this.getValuesFromAdaptationForFieldsBelow(pNode, pBean, pExtraImports));
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private Object generateGetValuesFromValueContext(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		String className = pBean.getJavaClassName();
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "protected void getValuesFromValueContext(final " + className + " p" + className
				+ ", final ValueContext pContext, Optional<SessionPermissions> pPermissions) {" + CR);
		String getValues = this.getValuesFromValueContextForFieldsBelow(pNode, pBean, pExtraImports);
		if (!getValues.isEmpty()) {
			str.append(TAB[2] + "Path pathToRecordRoot = SchemaUtils.getPathToRecordRoot(pContext.getNode());" + CR);
			str.append(
					TAB[2] + "Optional<Adaptation> record = AdaptationUtils.getRecordForValueContext(pContext);" + CR);
			pExtraImports.add("com.tibco.ebx.cs.commons.lib.utils.SchemaUtils");
			pExtraImports.add("com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils");
		}
		str.append(getValues);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	/**
	 * Generates overwritten equals and hashCode methods for class.
	 *
	 * @param node           The table for which to create the hashCode and equals
	 *                       methods.
	 * @param pJavaClassName The Java class name of the object to hash and test for
	 *                       equality with a counterpart.
	 * @param pExtraImports
	 * @return String with code for hashCode and equals methods
	 */
	private String generateHashCodeAndEquals(final SchemaNode pNode, final String pJavaClassName,
			final Set<String> pExtraImports) {
		StringBuilder str = new StringBuilder();
		pExtraImports.add("java.util.Objects");
		List<String> attributesList = this.getListOfAttributeNamesForNode(pNode);
		// hash code
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "public int hashCode() {" + CR);
		str.append(TAB[2] + "return Objects.hash(" + StringUtils.join(attributesList, ",") + ");" + CR);
		str.append(TAB[1] + "}" + CR);
		str.append(CR);
		// equals
		str.append(TAB[1] + "@Override" + CR);
		str.append(TAB[1] + "public boolean equals(final Object obj) {" + CR);
		str.append(TAB[2] + "if (this == obj) {" + CR);
		str.append(TAB[3] + "return true;" + CR);
		str.append(TAB[2] + "}" + CR);

		str.append(TAB[2] + "if (obj == null) {" + CR);
		str.append(TAB[3] + "return false;" + CR);
		str.append(TAB[2] + "}" + CR);

		str.append(TAB[2] + "if (!(obj instanceof " + pJavaClassName + ")) {" + CR);
		str.append(TAB[3] + "return false;" + CR);
		str.append(TAB[2] + "}" + CR);

		if (attributesList.isEmpty()) {
			str.append(TAB[2] + "return true;" + CR);
		} else {
			str.append(TAB[2] + pJavaClassName + " other = ( " + pJavaClassName + ") obj;" + CR);
			List<String> equalsForAttributes = new ArrayList<>();
			for (String attributeName : attributesList) {
				equalsForAttributes.add(GenerateJavaAccessers.generateEqualsForAttribute(attributeName));
			}
			str.append(TAB[2] + "return " + StringUtils.join(equalsForAttributes, CR + TAB[3] + " && ") + ";" + CR);
		}
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private String generatePathsAsConstantsForFieldsBelow(final SchemaNode pNode) {
		StringBuilder str = new StringBuilder();

		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.isTableNode()) {
				continue;
			}
			if (node.isTerminalValue()
					|| node.isTerminalValueDescendant()
							&& (node.getAccessMode().isReadWrite() || node.getMaxOccurs() > 1)
					|| node.isAssociationNode()) {
				str.append(
						TAB[1] + "public static final Path " + GenerateJavaAccessers.getConstantNameForPathToField(node)
								+ " = Path.parse(\"." + node.getPathInAdaptation().format() + "\");" + CR);
			}
			str.append(this.generatePathsAsConstantsForFieldsBelow(node));
		}
		return str.toString();
	}

	private String generatePathsAsConstantsForTablesBelow(final SchemaNode schemaNode) {
		StringBuilder str = new StringBuilder();
		for (AdaptationTable table : AdaptationUtils.getAllTables(this.DATASETS.get(schemaNode.getSchemaLocation()))) {
			str.append(TAB[1] + "public static final Path "
					+ GenerateJavaAccessers.getConstantNameForPathToTable(table.getTableNode()) + " = Path.parse(\""
					+ table.getTablePath().format() + "\");" + CR);
		}
		return str.toString();
	}

	private static String generateRestService(final AdaptationTable pTable, final RestService pRestService)
			throws EBXCommonsException {
		StringBuilder classContent = new StringBuilder();
		String tableLabel = pTable.getTableNode().getLabel(Locale.US);
		classContent.append("package " + pRestService.getJavaPackage() + ";" + CR);
		classContent.append(CR);

		classContent.append("import java.util.List;" + CR);

		classContent.append("import javax.ws.rs.Consumes;" + CR);
		classContent.append("import javax.ws.rs.DELETE;" + CR);
		classContent.append("import javax.ws.rs.GET;" + CR);
		classContent.append("import javax.ws.rs.NotFoundException;" + CR);
		classContent.append("import javax.ws.rs.POST;" + CR);
		classContent.append("import javax.ws.rs.PUT;" + CR);
		classContent.append("import javax.ws.rs.Path;" + CR);
		classContent.append("import javax.ws.rs.PathParam;" + CR);
		classContent.append("import javax.ws.rs.Produces;" + CR);
		classContent.append("import javax.ws.rs.QueryParam;" + CR);
		classContent.append("import javax.ws.rs.container.ResourceInfo;" + CR);
		classContent.append("import javax.ws.rs.core.MediaType;" + CR);
		classContent.append("import javax.ws.rs.core.Context;" + CR);
		classContent.append("import javax.ws.rs.core.Response;" + CR);

		classContent.append("import com.onwbp.adaptation.Adaptation;" + CR);
		classContent.append("import com.orchestranetworks.instance.Repository;" + CR);
		classContent.append("import com.orchestranetworks.rest.annotation.Documentation;" + CR);
		classContent.append("import com.orchestranetworks.rest.inject.SessionContext;" + CR);
		classContent.append("import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;" + CR);
		classContent
				.append("import " + GenerateJavaAccessers.getClassSignatureForDTO(pRestService.getDto()) + ";" + CR);
		classContent.append(
				"import " + GenerateJavaAccessers.getClassSignatureForService(pRestService.getService()) + ";" + CR);
		classContent.append("import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;" + CR);

		classContent.append("import io.swagger.v3.oas.annotations.Operation;" + CR);
		classContent.append("import io.swagger.v3.oas.annotations.Parameter;" + CR);
		classContent.append("import io.swagger.v3.oas.annotations.media.ArraySchema;" + CR);
		classContent.append("import io.swagger.v3.oas.annotations.media.Content;" + CR);
		classContent.append("import io.swagger.v3.oas.annotations.media.Schema;" + CR);
		classContent.append("import io.swagger.v3.oas.annotations.parameters.RequestBody;" + CR);
		classContent.append("import io.swagger.v3.oas.annotations.responses.ApiResponse;" + CR);
		classContent.append("import io.swagger.v3.oas.annotations.security.SecurityRequirement;" + CR);
		classContent.append("import io.swagger.v3.oas.annotations.tags.Tag;" + CR);

		classContent.append(CR);
		classContent.append("/**" + CR);
		classContent.append(
				"* REST Service automatically generated by service 'Generate Java Accessers' of EBX Commons" + CR);
		classContent.append("* It aims to integrate records of table at path " + pTable.getTablePath().format()
				+ " in data model " + pTable.getTableNode().getSchemaLocation().format() + "." + CR);
		if (!StringUtils.isBlank(pRestService.getDataspace())) {
			classContent.append("* The identifier of a dataspace being specified before generation to be '"
					+ pRestService.getDataspace() + "', operation are applied to it." + CR);
		}
		if (!StringUtils.isBlank(pRestService.getDataset())) {
			classContent.append("* The identifier of a dataset being specified before generation to be '"
					+ pRestService.getDataset() + "', operation are applied to it." + CR);
		}
		classContent.append("*" + CR);
		classContent.append("* @author EBX Commons" + CR);
		classContent.append("*/" + CR);
		classContent.append("@Produces(MediaType.APPLICATION_JSON)" + CR);
		classContent.append("@Consumes(MediaType.APPLICATION_JSON)" + CR);
		classContent.append("@Path(\"/");
		if (StringUtils.isBlank(pRestService.getDataspace())) {
			classContent.append("{dataspace}/");
		}
		if (StringUtils.isBlank(pRestService.getDataset())) {
			classContent.append("{dataset}/");
		}
		classContent.append(pRestService.getResourceName() + "\")" + CR);
		classContent.append("@Documentation(\"Service to access " + pRestService.getResourceName() + "\")" + CR);
		classContent.append("@SecurityRequirement(name = \"basicAuth\", scopes = \"write: read\")" + CR);
		if (!StringUtils.isBlank(pRestService.getTag())) {
			classContent.append("@Tag(name = \"" + pRestService.getTag() + "\")" + CR);
		}
		classContent.append("public class " + pRestService.getJavaClassName() + " {" + CR);
		classContent.append(CR);
		classContent.append(TAB[1] + "@Context" + CR);
		classContent.append(TAB[1] + "protected ResourceInfo resourceInfo;" + CR);
		classContent.append(CR);
		classContent.append(TAB[1] + "@Context" + CR);
		classContent.append(TAB[1] + "protected SessionContext sessionContext;" + CR);
		classContent.append(CR);
		if (!StringUtils.isBlank(pRestService.getDataspace())) {
			classContent.append(
					TAB[1] + "private static final String DATASPACE = \"" + pRestService.getDataspace() + "\";");
			classContent.append(CR);
		}
		if (!StringUtils.isBlank(pRestService.getDataset())) {
			classContent
					.append(TAB[1] + "private static final String DATASET = \"" + pRestService.getDataset() + "\";");
			classContent.append(CR);
		}
		classContent.append(CR);
		classContent
				.append(TAB[1] + "protected " + pRestService.getService().getJavaClassName() + " getService() {" + CR);
		classContent.append(TAB[2] + "return new " + pRestService.getService().getJavaClassName() + "();" + CR);
		classContent.append(TAB[1] + "}" + CR);

		String dataspaceDatasetParameters = (StringUtils.isBlank(pRestService.getDataspace()) ? "pDataspace"
				: "DATASPACE") + ", " + (StringUtils.isBlank(pRestService.getDataspace()) ? "pDataset" : "DATASET");

		if (pRestService.getPost().booleanValue()) {
			classContent.append(CR);
			classContent.append(TAB[1] + "/**" + CR);
			classContent
					.append(TAB[1] + "* POST Operation to create a record in table " + pTable.getTablePath().format()
							+ " in data model " + pTable.getTableNode().getSchemaLocation().format() + "." + CR);
			classContent.append(TAB[1] + "*" + CR);
			classContent.append(
					TAB[1] + "* @param pDTO A data transfer object representing the record to be created {@link "
							+ pRestService.getDto().getJavaClassName() + "}" + CR);
			classContent.append(TAB[1] + "*" + CR);
			classContent.append(TAB[1]
					+ "* @return A response with the code 201 along with a DTO representing the created record." + CR);
			classContent.append(TAB[1] + "*/" + CR);
			classContent.append(TAB[1] + "@POST" + CR);
			classContent.append(TAB[1] + "@Path(\"/\")" + CR);
			classContent.append(TAB[1] + "@Operation(summary = \"Create a record in table '" + tableLabel
					+ "'\", description = \"Create a record in table '" + tableLabel + "'\")" + CR);
			classContent.append(TAB[1]
					+ "@ApiResponse(description = \"Created record\", content = @Content(schema = @Schema(implementation = "
					+ pRestService.getDto().getJavaClassName() + ".class)))" + CR);
			classContent.append(TAB[1] + "@ApiResponse(responseCode = \"201\", description = \"Record created\")" + CR);
			classContent.append(TAB[1] + "public Response post(@RequestBody(description = \""
					+ pRestService.getJavaClassName() + "\", required = true, ");
			classContent.append("content = @Content(schema = @Schema(implementation = "
					+ pRestService.getDto().getJavaClassName() + ".class)))" + CR);
			classContent.append(TAB[1] + "final " + pRestService.getDto().getJavaClassName() + " pDTO");
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append(", @PathParam(\"dataspace\")" + CR);
				classContent.append(TAB[1] + "final String pDataspace");
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append(", @PathParam(\"dataset\")" + CR);
				classContent.append(TAB[1] + "final String pDataset");
			}
			classContent.append(") throws EBXCommonsException {" + CR);
			classContent.append(TAB[2] + "Adaptation dataset = AdaptationUtils.getDataset(Repository.getDefault(), "
					+ dataspaceDatasetParameters + ");" + CR);
			classContent.append(TAB[2] + pRestService.getDto().getJavaClassName()
					+ " created = this.getService().create(pDTO, this.sessionContext.getSession(), dataset);" + CR);
			classContent
					.append(TAB[2] + "return Response.status(Response.Status.CREATED).entity(created).build();" + CR);
			classContent.append(TAB[1] + "}" + CR);
		}

		if (pRestService.getGet().booleanValue()) {
			classContent.append(CR);
			classContent.append(TAB[1] + "/**" + CR);
			classContent.append(TAB[1] + "* GET Operation to get a record from table " + pTable.getTablePath().format()
					+ " in data model " + pTable.getTableNode().getSchemaLocation().format() + "." + CR);
			classContent.append(TAB[1] + "*" + CR);
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append(
						TAB[1] + "* @param pDataspace Identifier of the dataspace from which to get the record." + CR);
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append(
						TAB[1] + "* @param pDataset Identifier of the dataset from which to get the record." + CR);
			}
			classContent.append(TAB[1] + "* @param pPK The primary key of the record to get." + CR);
			classContent.append(TAB[1] + "*" + CR);
			classContent.append(TAB[1]
					+ "* @return A response with the code 20O along with a DTO representing the record. A response with the code 404 if the record has not been found."
					+ CR);
			classContent.append(TAB[1] + "*/" + CR);
			classContent.append(TAB[1] + "@GET" + CR);
			classContent.append(TAB[1] + "@Path(\"/{pk}\")" + CR);
			classContent.append(TAB[1] + "@Operation(summary = \"Get record from table '" + tableLabel
					+ "'\", description = \"Get record from table '" + tableLabel + "'\")" + CR);
			classContent.append(TAB[1] + "@ApiResponse(description = \"Record from table '" + tableLabel
					+ "'\", content = @Content(schema = @Schema(implementation = "
					+ pRestService.getDto().getJavaClassName() + ".class)))" + CR);
			classContent.append(
					TAB[1] + "@ApiResponse(responseCode = \"200\", description = \"Record found and returned\")" + CR);
			classContent
					.append(TAB[1] + "@ApiResponse(responseCode = \"404\", description = \"Record not found\")" + CR);
			classContent.append(TAB[1] + "public " + pRestService.getDto().getJavaClassName() + " get(");
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append("@PathParam(\"dataspace\")" + CR);
				classContent.append(TAB[1] + "final String pDataspace, ");
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append("@PathParam(\"dataset\")" + CR);
				classContent.append(TAB[1] + "final String pDataset, ");
			}
			classContent.append("@Parameter(description = \"" + GenerateJavaAccessers.getPrimaryDescription(pTable)
					+ "\") @PathParam(\"pk\")" + CR);
			classContent.append(TAB[1] + "final String pPK) throws EBXCommonsException {" + CR);
			classContent.append(TAB[2] + "Adaptation dataset = AdaptationUtils.getDataset(Repository.getDefault(), "
					+ dataspaceDatasetParameters + ");" + CR);
			classContent.append(TAB[2] + pRestService.getDto().getJavaClassName()
					+ " dto =  this.getService().read(pPK, this.sessionContext.getSession(), dataset).orElse(null);"
					+ CR);
			classContent.append(TAB[2] + "if (dto == null) {" + CR);
			classContent.append(TAB[3] + "throw new NotFoundException();" + CR);
			classContent.append(TAB[2] + "}" + CR);
			classContent.append(TAB[2] + "return dto;" + CR);
			classContent.append(TAB[1] + "}" + CR);
		}

		if (pRestService.getGetList().booleanValue()) {
			classContent.append(CR);
			classContent.append(TAB[1] + "/**" + CR);
			classContent.append(
					TAB[1] + "* GET Operation to get a list of records from table " + pTable.getTablePath().format()
							+ " in data model " + pTable.getTableNode().getSchemaLocation().format() + "." + CR);
			classContent.append(TAB[1] + "*" + CR);
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append(TAB[1]
						+ "* @param pDataspace Identifier of the dataspace from which to search for records." + CR);
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append(
						TAB[1] + "* @param pDataset Identifier of the dataset from which to search for records." + CR);
			}
			classContent.append(TAB[1]
					+ "* @param pPredicate An XPath predicate to be applied to the table to filter the records to be returned."
					+ CR);
			classContent.append(TAB[1] + "* @param pLimit Maximum number of record to be returned." + CR);
			classContent.append(TAB[1]
					+ "* @param pOffset Number of record to be ignored in the results. This is to be used to get paginated results in association with pLimit."
					+ CR);
			classContent.append(TAB[1] + "*" + CR);
			classContent.append(TAB[1]
					+ "* @return A response with the code 20O along with a list of DTO representing the records found."
					+ CR);
			classContent.append(TAB[1] + "*/" + CR);
			classContent.append(TAB[1] + "@GET" + CR);
			classContent.append(TAB[1] + "@Path(\"/\")" + CR);
			classContent.append(TAB[1] + "@Operation(summary = \"Search for records from table '" + tableLabel
					+ "' by predicate\", description = \"Search for records from table '" + tableLabel
					+ "' by predicate\")" + CR);
			classContent.append(TAB[1]
					+ "@ApiResponse(description = \"List of records matching predicate\", content = @Content(array = @ArraySchema(schema = @Schema(implementation = "
					+ pRestService.getDto().getJavaClassName() + ".class))))" + CR);
			classContent.append(
					TAB[1] + "@ApiResponse(responseCode = \"200\", description = \"Request executed properly\")" + CR);
			classContent.append(TAB[1] + "public List<" + pRestService.getDto().getJavaClassName() + "> getList(");
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append("@PathParam(\"dataspace\")" + CR);
				classContent.append(TAB[1] + "final String pDataspace, ");
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append("@PathParam(\"dataset\")" + CR);
				classContent.append(TAB[1] + "final String pDataset, ");
			}
			classContent.append(TAB[1] + "@QueryParam(\"predicate\")" + CR);
			classContent.append(TAB[1] + "final String pPredicate, @QueryParam(\"limit\")" + CR);
			classContent.append(TAB[1] + "final Integer pLimit, @QueryParam(\"offset\")" + CR);
			classContent.append(TAB[1] + "final Integer pOffset) throws EBXCommonsException {" + CR);
			classContent.append(TAB[2] + "Adaptation dataset = AdaptationUtils.getDataset(Repository.getDefault(), "
					+ dataspaceDatasetParameters + ");" + CR);
			classContent.append(TAB[2]
					+ "return this.getService().read(this.sessionContext.getSession(), dataset, pPredicate, pLimit, pOffset);"
					+ CR);
			classContent.append(TAB[1] + "}" + CR);
		}

		if (pRestService.getPut().booleanValue()) {
			classContent.append(CR);
			classContent.append(TAB[1] + "/**" + CR);
			classContent.append(TAB[1] + "* PUT Operation to update a record in table " + pTable.getTablePath().format()
					+ " in data model " + pTable.getTableNode().getSchemaLocation().format() + "." + CR);
			classContent.append(TAB[1] + "*" + CR);
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append(TAB[1]
						+ "* @param pDataspace Identifier of the dataspace from which to update the record." + CR);
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append(
						TAB[1] + "* @param pDataset Identifier of the dataset from which to update the record." + CR);
			}
			classContent.append(TAB[1] + "* @param pPK The primary key of the record to update." + CR);
			classContent.append(
					TAB[1] + "* @param pDTO A data transfer object representing the record to be created {@link "
							+ pRestService.getDto().getJavaClassName() + "}" + CR);
			classContent.append(TAB[1] + "*" + CR);
			classContent.append(TAB[1]
					+ "* @return A response with the code 200 along with a DTO representing the updated record. A response with code 404 if the record has not been found."
					+ CR);
			classContent.append(TAB[1] + "*/" + CR);
			classContent.append(TAB[1] + "@PUT" + CR);
			classContent.append(TAB[1] + "@Path(\"/{pk}\")" + CR);
			classContent.append(TAB[1] + "@Operation(summary = \"Update a record in table '" + tableLabel
					+ "'\", description = \"Update a record in table '" + tableLabel + "'\")" + CR);
			classContent.append(TAB[1]
					+ "@ApiResponse(description = \"Updated record\", content = @Content(schema = @Schema(implementation = "
					+ pRestService.getDto().getJavaClassName() + ".class)))" + CR);
			classContent.append(TAB[1] + "@ApiResponse(responseCode = \"200\", description = \"Record updated\")" + CR);
			classContent
					.append(TAB[1] + "@ApiResponse(responseCode = \"404\", description = \"Record not found\")" + CR);
			classContent.append(TAB[1] + "public Response put(@RequestBody(description = \""
					+ pRestService.getJavaClassName() + "\", required = true, ");
			classContent.append("content = @Content(schema = @Schema(implementation = "
					+ pRestService.getDto().getJavaClassName() + ".class)))" + CR);
			classContent.append(TAB[1] + "final " + pRestService.getDto().getJavaClassName() + " pDTO");
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append(", @PathParam(\"dataspace\")" + CR);
				classContent.append(TAB[1] + "final String pDataspace");
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append(", @PathParam(\"dataset\")" + CR);
				classContent.append(TAB[1] + "final String pDataset");
			}
			classContent.append(", @Parameter(description = \"" + GenerateJavaAccessers.getPrimaryDescription(pTable)
					+ "\") @PathParam(\"pk\")" + CR);
			classContent.append(TAB[1] + "final String pPK) throws EBXCommonsException {" + CR);
			classContent.append(TAB[2] + "Adaptation dataset = AdaptationUtils.getDataset(Repository.getDefault(), "
					+ dataspaceDatasetParameters + ");" + CR);
			classContent.append(TAB[2] + pRestService.getDto().getJavaClassName()
					+ " dto = this.getService().update(pPK, pDTO, this.sessionContext.getSession(), dataset);" + CR);
			classContent.append(TAB[2] + "if(dto != null){" + CR);
			classContent.append(TAB[3]
					+ "return Response.status(Response.Status.OK).entity(this.getService().read(pPK, this.sessionContext.getSession(), dataset)).build();"
					+ CR);
			classContent.append(TAB[2] + "}" + CR);
			classContent.append(TAB[2] + "else {" + CR);
			classContent.append(TAB[3] + "throw new NotFoundException();" + CR);
			classContent.append(TAB[2] + "}" + CR);
			classContent.append(TAB[1] + "}" + CR);
		}

		if (pRestService.getPutList().booleanValue()) {
			classContent.append(CR);
			classContent.append(TAB[1] + "/**" + CR);
			classContent.append(TAB[1] + "* PUT Operation to create or update a list of records in table "
					+ pTable.getTablePath().format() + " in data model "
					+ pTable.getTableNode().getSchemaLocation().format() + "." + CR);
			classContent.append(TAB[1] + "*" + CR);
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append(TAB[1]
						+ "* @param pDataspace Identifier of the dataspace from which to update the records." + CR);
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append(
						TAB[1] + "* @param pDataset Identifier of the dataset from which to update the records." + CR);
			}
			classContent.append(TAB[1]
					+ "* @param pDTOs A list of data transfer object representing the records to be created or updated {@link "
					+ pRestService.getDto().getJavaClassName() + "}" + CR);
			classContent.append(TAB[1] + "*" + CR);
			classContent.append(TAB[1]
					+ "* @return A response with the code 200 along with a list of DTO representing the created and/or updated records."
					+ CR);
			classContent.append(TAB[1] + "*/" + CR);
			classContent.append(TAB[1] + "@PUT" + CR);
			classContent.append(TAB[1] + "@Path(\"/\")" + CR);
			classContent.append(TAB[1] + "@Operation(summary = \"Update or create a list of records in table '"
					+ tableLabel + "'\", description = \"Update or create a list of records in table '" + tableLabel
					+ "'\")" + CR);
			classContent.append(TAB[1]
					+ "@ApiResponse(description = \"Created and/or Updated records\", content = @Content(array = @ArraySchema(schema = @Schema(implementation = "
					+ pRestService.getDto().getJavaClassName() + ".class))))" + CR);
			classContent.append(TAB[1]
					+ "@ApiResponse(responseCode = \"200\", description = \"All occurrences updated or created\")"
					+ CR);
			classContent.append(TAB[1] + "public Response putList(@RequestBody(description = \""
					+ pRestService.getJavaClassName() + "\", required = true, ");
			classContent.append("content = @Content(array = @ArraySchema(schema = @Schema(implementation = "
					+ pRestService.getDto().getJavaClassName() + ".class))))" + CR);
			classContent.append(TAB[1] + "final List<" + pRestService.getDto().getJavaClassName() + "> pDTOs");
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append(", @PathParam(\"dataspace\")" + CR);
				classContent.append(TAB[1] + "final String pDataspace");
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append(", @PathParam(\"dataset\")" + CR);
				classContent.append(TAB[1] + "final String pDataset");
			}
			classContent.append(") throws EBXCommonsException {" + CR);
			classContent.append(TAB[2] + "Adaptation dataset = AdaptationUtils.getDataset(Repository.getDefault(), "
					+ dataspaceDatasetParameters + ");" + CR);
			classContent.append(TAB[2] + "List<" + pRestService.getDto().getJavaClassName()
					+ "> dtos = this.getService().createOrUpdate(pDTOs, this.sessionContext.getSession(), dataset);"
					+ CR);
			classContent.append(TAB[2] + "return Response.status(Response.Status.OK).entity(dtos).build();" + CR);
			classContent.append(TAB[1] + "}" + CR);
		}

		if (pRestService.getDelete().booleanValue()) {
			classContent.append(CR);
			classContent.append(CR);
			classContent.append(TAB[1] + "/**" + CR);
			classContent.append(
					TAB[1] + "* DELETE Operation to remove a record from table " + pTable.getTablePath().format()
							+ " in data model " + pTable.getTableNode().getSchemaLocation().format() + "." + CR);
			classContent.append(TAB[1] + "*" + CR);
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append(TAB[1]
						+ "* @param pDataspace Identifier of the dataspace from which to remove the record." + CR);
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append(
						TAB[1] + "* @param pDataset Identifier of the dataset from which to remove the record." + CR);
			}
			classContent.append(TAB[1] + "* @param pPK The primary key of the record to delete." + CR);
			classContent.append(TAB[1] + "*" + CR);
			classContent.append(TAB[1]
					+ "* @return A response with the code 204. A response with the code 404 if the record has not been found."
					+ CR);
			classContent.append(TAB[1] + "*/" + CR);
			classContent.append(TAB[1] + "@DELETE" + CR);
			classContent.append(TAB[1] + "@Path(\"/{pk}\")" + CR);
			classContent.append(TAB[1] + "@Operation(summary = \"Delete a record from table '" + tableLabel
					+ "'\", description = \"Delete a record from table '" + tableLabel + "'\")" + CR);
			classContent.append(TAB[1] + "@ApiResponse(responseCode = \"204\", description = \"Record deleted\")" + CR);
			classContent
					.append(TAB[1] + "@ApiResponse(responseCode = \"404\", description = \"Record not found\")" + CR);
			classContent.append(TAB[1] + "public Response delete(");
			if (StringUtils.isBlank(pRestService.getDataspace())) {
				classContent.append("@PathParam(\"dataspace\")" + CR);
				classContent.append(TAB[1] + "final String pDataspace, ");
			}
			if (StringUtils.isBlank(pRestService.getDataset())) {
				classContent.append("@PathParam(\"dataset\")" + CR);
				classContent.append(TAB[1] + "final String pDataset, ");
			}
			classContent.append("@Parameter(description = \"" + GenerateJavaAccessers.getPrimaryDescription(pTable)
					+ "\") @PathParam(\"pk\")" + CR);
			classContent.append(TAB[1] + "final String pPk) throws EBXCommonsException {" + CR);
			classContent.append(TAB[2] + "Adaptation dataset = AdaptationUtils.getDataset(Repository.getDefault(), "
					+ dataspaceDatasetParameters + ");" + CR);
			classContent
					.append(TAB[2] + "this.getService().delete(pPk, this.sessionContext.getSession(), dataset);" + CR);
			classContent.append(TAB[3] + "return Response.noContent().build();" + CR);
			classContent.append(TAB[1] + "}" + CR);
		}

		classContent.append("}" + CR);

		return classContent.toString();
	}

	private static String getPrimaryDescription(final AdaptationTable pTable) {
		Path[] primaryKeySpec = pTable.getPrimaryKeySpec();
		StringBuilder description = new StringBuilder("");
		if (primaryKeySpec.length > 1) {
			description.append("The primary key is made of the fields '");

			for (int i = 0; i < primaryKeySpec.length; i++) {
				description.append(GenerateJavaAccessers.getAttributeNameForNode(
						pTable.getTableOccurrenceRootNode().getNode(Path.SELF.add(primaryKeySpec[i]))));
				if (i == primaryKeySpec.length - 2) {
					description.append("' and '");
				} else if (i == primaryKeySpec.length - 1) {
					break;
				} else {
					description.append("', ");
				}
			}
			description.append("' concatenated in this order and separated with a pipe.");
		} else {
			description.append("The primary key is the field '");
			description.append(GenerateJavaAccessers.getAttributeNameForNode(
					pTable.getTableOccurrenceRootNode().getNode(Path.SELF.add(primaryKeySpec[0]))));
			description.append("'");
		}
		description.append(".");
		return description.toString();
	}

	private String generateService(final AdaptationTable pTable, final Service pService) throws EBXCommonsException {
		DataAccessObject dao = this.DAOS.get(pTable.getTableNode().getSchemaLocation())
				.get(pTable.getTableNode().getPathInSchema().format());
		StringBuilder classContent = new StringBuilder();
		classContent.append("package " + pService.getJavaPackage() + ";" + CR);
		classContent.append(CR);
		classContent.append("import " + GenerateJavaAccessers.getClassSignatureForBean(dao.getJavaBean()) + ";" + CR);
		classContent.append("import " + GenerateJavaAccessers.getClassSignatureForDAO(dao) + ";" + CR);
		classContent.append(
				"import " + GenerateJavaAccessers.getClassSignatureForDTO(pService.getDataTransferObject()) + ";" + CR);
		classContent.append("import "
				+ GenerateJavaAccessers.getClassSignatureForMapper(pService.getDataTransferObject()) + ";" + CR);
		classContent.append("import com.tibco.ebx.cs.commons.beans.generator.template.Service;" + CR);
		classContent.append(CR);
		classContent.append("/**" + CR);
		classContent
				.append("* Service automatically generated by service 'Generate Java Accessers' of EBX Commons" + CR);
		classContent.append("* It aims to manipulate records of table at path " + pTable.getTablePath().format()
				+ " in data model " + pTable.getTableNode().getSchemaLocation().format() + "." + CR);
		classContent.append("*" + CR);
		classContent.append("* @author EBX Commons" + CR);
		classContent.append("*/" + CR);
		classContent.append("public class " + pService.getJavaClassName() + " extends ");
		classContent.append("Service<" + dao.getJavaBean().getJavaClassName() + ", " + dao.getJavaClassName() + ", "
				+ pService.getDataTransferObject().getJavaClassName() + "> {" + CR);
		classContent.append(CR);
		classContent.append(TAB[1] + "@Override" + CR);
		classContent
				.append(TAB[1] + "protected Class<" + dao.getJavaBean().getJavaClassName() + "> getBeanClass() {" + CR);
		classContent.append(TAB[2] + "return " + dao.getJavaBean().getJavaClassName() + ".class;" + CR);
		classContent.append(TAB[1] + "}" + CR);
		classContent.append(CR);
		classContent.append(TAB[1] + "@Override" + CR);
		classContent.append(TAB[1] + "protected Class<" + pService.getDataTransferObject().getJavaClassName()
				+ "> getDTOClass() {" + CR);
		classContent.append(TAB[2] + "return " + pService.getDataTransferObject().getJavaClassName() + ".class;" + CR);
		classContent.append(TAB[1] + "}" + CR);
		classContent.append(CR);
		classContent.append(TAB[1] + "@Override" + CR);
		classContent.append(TAB[1] + "protected " + dao.getJavaClassName() + " getDAO() {" + CR);
		classContent.append(TAB[2] + "return " + dao.getJavaClassName() + ".getInstance();" + CR);
		classContent.append(TAB[1] + "}" + CR);
		classContent.append(CR);
		classContent.append(TAB[1] + "@Override" + CR);
		classContent.append(TAB[1] + "protected " + pService.getDataTransferObject().getMapper().getJavaClassName()
				+ " getMapper() {" + CR);
		classContent.append(
				TAB[2] + "return new " + pService.getDataTransferObject().getMapper().getJavaClassName() + "();" + CR);
		classContent.append(TAB[1] + "}" + CR);
		classContent.append("}" + CR);
		this.numberOfServices++;
		return classContent.toString();
	}

	private Object generateSetValuesForUpdate(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports)
			throws ConstraintViolationException, EBXCommonsException, OperationException {
		StringBuilder str = new StringBuilder();
		String className = pBean.getJavaClassName();
		str.append(TAB[1] + "@Override" + CR);
		pExtraImports.add("com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException");
		str.append(TAB[1] + "protected void setValuesForUpdate(final ValueContextForUpdate pContext, final " + className
				+ " p" + className + ") throws EBXCommonsException {" + CR);
		str.append(this.setValuesForUpdateForFieldsBelow(pNode, pBean, pExtraImports));
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private DataAccessObject generateTableDAO(final AdaptationTable pTable, final DataAccessObject pDAO)
			throws EBXCommonsException, OperationException {
		DataAccessObject dao = this.getOrRegisterDAO(pTable.getTableNode());
		if (dao.getGenerationActive().booleanValue()) {
			Set<String> extraImports = new HashSet<>();
			StringBuilder str = new StringBuilder();
			str.append("/**" + CR);
			str.append(
					"* Data access object (DAO) automatically generated by service 'Generate Java Accessers' of EBX Commons"
							+ CR);
			str.append("* It aims to access records of table at path " + pTable.getTablePath().format()
					+ " in data model " + pTable.getTableNode().getSchemaLocation().format() + "." + CR);
			str.append("*" + CR);
			str.append("* @author EBX Commons" + CR);
			str.append("*/" + CR);
			str.append("public class " + dao.getJavaClassName() + " extends TableDAO<"
					+ dao.getJavaBean().getJavaClassName() + "> {" + CR);
			str.append(CR);
			str.append(TAB[1] + "private static final " + dao.getJavaClassName() + " instance = new "
					+ dao.getJavaClassName() + "();" + CR);
			str.append(CR);
			str.append(this.generatePathsAsConstantsForFieldsBelow(pTable.getTableOccurrenceRootNode()));
			str.append(CR);
			str.append(this.generateSetValuesForUpdate(pTable.getTableOccurrenceRootNode(), dao.getJavaBean(),
					extraImports));
			str.append(CR);
			str.append(this.generateGetValuesFromAdaptation(pTable.getTableOccurrenceRootNode(), dao.getJavaBean(),
					extraImports));
			str.append(CR);
			str.append(this.generateGetValuesFromValueContext(pTable.getTableOccurrenceRootNode(), dao.getJavaBean(),
					extraImports));
			str.append(CR);
			str.append(GenerateJavaAccessers.generateGetInstance(dao.getJavaClassName()));
			str.append(CR);
			str.append(this.generateGetInstanceOfBean(pTable.getTableOccurrenceRootNode(), dao.getJavaBean(),
					extraImports));
			str.append(CR);
			str.append(GenerateJavaAccessers.generateGetPrimaryKeysGetters(pTable));
			str.append(CR);
			str.append(this.generateGetPrimaryKeysSetters(pTable, extraImports));
			str.append(CR);
			str.append(GenerateJavaAccessers.generateGetDatasetDAO(pDAO));
			str.append(CR);
			str.append(GenerateJavaAccessers.generateGetAdaptationTable(pTable.getTableNode(), dao.getJavaBean(),
					extraImports));

			str.append("}");

			StringBuilder classContent = new StringBuilder();
			classContent.append("package " + dao.getJavaPackage() + ";" + CR);
			classContent.append(CR);
			classContent.append("import com.onwbp.adaptation.Adaptation;" + CR);
			classContent.append("import com.orchestranetworks.schema.Path;" + CR);
			classContent.append("import com.orchestranetworks.service.ValueContextForUpdate;" + CR);
			classContent.append("import com.orchestranetworks.instance.ValueContext;" + CR);
			classContent.append("import com.tibco.ebx.cs.commons.beans.generator.template.TableDAO;" + CR);
			classContent.append("import com.tibco.ebx.cs.commons.beans.generator.template.PrimaryKeySetter;" + CR);

			extraImports.add(GenerateJavaAccessers.getClassSignatureForBean(dao.getJavaBean()));
			GenerateJavaAccessers.writeExtraImports(dao.getJavaPackage(), extraImports, classContent);
			classContent.append(CR);
			classContent.append(str);
			this.numberOfDAOs++;
			GenerateJavaAccessers.writeJavaFile(
					this.DATA_MODELS.get(pTable.getTableNode().getSchemaLocation()).getDaoSourceFolder(),
					dao.getJavaPackage(), dao.getJavaClassName(), classContent.toString());
		}
		return dao;
	}

	private void generateDTOForTable(final AdaptationTable pTable, final DataTransferObject pDTO)
			throws OperationException, EBXCommonsException {
		String classContent = this.generateDTO(pTable.getTableOccurrenceRootNode(), pDTO);
		GenerateJavaAccessers.writeJavaFile(
				this.DATA_MODELS.get(pTable.getTableNode().getSchemaLocation()).getDtoSourceFolder(),
				pDTO.getJavaPackage(), pDTO.getJavaClassName(), classContent);
	}

	private static Object generateTableGetter(final AdaptationTable table) {
		StringBuilder str = new StringBuilder();
		String tableGetterName = GenerateJavaAccessers.getTableGetterName(table.getTableNode());
		str.append(TAB[1] + "public AdaptationTable " + tableGetterName + "(){" + CR);
		str.append(TAB[2] + "return ebxDataset.getTable("
				+ GenerateJavaAccessers.getConstantNameForPathToTable(table.getTableNode()) + ");" + CR);
		str.append(TAB[1] + "}" + CR);
		return str.toString();
	}

	private void generateTableRS(final AdaptationTable pTable, final RestService pRS)
			throws OperationException, EBXCommonsException {
		Service service = pRS.getService();
		if (service == null) {
			service = this.getOrRegisterService(pTable.getTableNode()).get(0);
			pRS.setService(service);
		}
		DataTransferObject dto = pRS.getDto();
		if (dto == null) {
			dto = service.getDataTransferObject();
			if (dto == null) {
				dto = pRS.getTable().getPreferredDTO();
			}
			pRS.setDto(dto);
		}
		String classContent = GenerateJavaAccessers.generateRestService(pTable, pRS);
		this.numberOfRS++;
		GenerateJavaAccessers.writeJavaFile(
				this.DATA_MODELS.get(pTable.getTableNode().getSchemaLocation()).getRsSourceFolder(),
				pRS.getJavaPackage(), pRS.getJavaClassName(), classContent);
	}

	private void generateTableService(final AdaptationTable pTable, final Service pService)
			throws OperationException, EBXCommonsException {
		String classContent = this.generateService(pTable, pService);
		GenerateJavaAccessers.writeJavaFile(
				this.DATA_MODELS.get(pTable.getTableNode().getSchemaLocation()).getServicesSourceFolder(),
				pService.getJavaPackage(), pService.getJavaClassName(), classContent);
	}

	/**
	 * Generates instance variable declaration for a fields. It identifies on the
	 * fly packages to be imported in case extra treatments are necessary such as
	 * for foreign key fields.
	 *
	 * @param pNode         The node for which to generate an instance variable
	 *                      declaration.
	 * @param pExtraImports Set collecting extra packages to be imported.
	 * @param pAnnotate
	 *
	 * @return The instance variable declaration as String
	 * @throws OperationException
	 */
	private String getAttributeDeclarationForNodeInBean(final SchemaNode pNode, final Set<String> pExtraImports)
			throws EBXCommonsException {
		StringBuilder str = new StringBuilder();
		str.append(TAB[1] + "private " + this.getJavaTypeForNodeInBean(pNode, false, pExtraImports) + " "
				+ GenerateJavaAccessers.getAttributeNameForNode(pNode) + ";" + CR);
		return str.toString();
	}

	/**
	 * Generates instance variable declaration for a fields. It identifies on the
	 * fly packages to be imported in case extra treatments are necessary such as
	 * for foreign key fields.
	 *
	 * @param pNode         The node for which to generate an instance variable
	 *                      declaration.
	 * @param pDTO
	 * @param pExtraImports Set collecting extra packages to be imported.
	 * @param pAnnotate
	 *
	 * @return The instance variable declaration as String
	 * @throws OperationException
	 */
	private String getAttributeDeclarationForNodeInDTO(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws EBXCommonsException {
		StringBuilder str = new StringBuilder();
		StringBuilder doc = new StringBuilder("");
		if (pNode.getDescription(Locale.US) != null) {
			doc.append(pNode.getDescription(Locale.US));
		}
		SchemaFacetTableRef tableRef = pNode.getFacetOnTableReference();
		if (tableRef != null) {
			doc.append(" Being a reference, its value is made of the primary key of the referenced object. Here ");
			if (tableRef.getTablePrimaryKeyNodes().length > 1) {
				doc.append("the fields '");
				for (int i = 0; i < tableRef.getTablePrimaryKeyNodes().length; i++) {
					doc.append(GenerateJavaAccessers.getAttributeNameForNode(tableRef.getTablePrimaryKeyNodes()[i]));
					if (i == tableRef.getTablePrimaryKeyNodes().length - 2) {
						doc.append(" and ");
					} else if (i == tableRef.getTablePrimaryKeyNodes().length - 1) {
						break;
					} else {
						doc.append(", ");
					}
				}

				doc.append("' of the referenced object concatenated in this order and separated with a pipe.");
			} else {
				doc.append("the field '");
				doc.append(GenerateJavaAccessers.getAttributeNameForNode(tableRef.getTablePrimaryKeyNodes()[0]));
				doc.append("' of the referenced object");
			}
			doc.append(".");
		}
		DataTransferObject inDTO = null;
		if (pNode.isAssociationNode()) {
			inDTO = this.getDTOInclusion(pNode, pDTO);
			if (inDTO != null) {
				pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDTO(inDTO));
				pExtraImports.add(GenerateJavaAccessers.getClassSignatureForBean(inDTO.getTable().getJavaBean()));
				pExtraImports.add("io.swagger.v3.oas.annotations.media.ArraySchema");
				str.append(TAB[1] + "@ArraySchema(arraySchema = @Schema(description = \""
						+ (doc == null ? "" : doc.toString().replace("\n", "").replace("\r", "").replace("\"", "\\\""))
						+ "\"");
				str.append(", implementation = " + inDTO.getJavaClassName() + ".class))" + CR);
				str.append(TAB[1] + "private " + this.getJavaTypeForNodeInDTO(pNode, false, inDTO, pExtraImports) + " "
						+ GenerateJavaAccessers.getAttributeNameForNode(pNode) + ";" + CR);
			}
		} else {
			if (pNode.getMaxOccurs() > 1) {
				pExtraImports.add("io.swagger.v3.oas.annotations.media.ArraySchema");
				str.append(TAB[1] + "@ArraySchema(arraySchema = ");
			} else {
				str.append(TAB[1]);
			}
			if (tableRef != null) {
				inDTO = this.getDTOInclusion(pNode, pDTO);
				str.append("@Schema(description = \""
						+ (doc == null ? "" : doc.toString().replace("\n", "").replace("\r", "").replace("\"", "\\\""))
						+ "\"");
				if (inDTO != null) {
					pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDTO(inDTO));
					pExtraImports.add(GenerateJavaAccessers.getClassSignatureForBean(inDTO.getTable().getJavaBean()));
					str.append(", implementation = " + inDTO.getJavaClassName() + ".class)");
				} else {
					str.append(")");
				}
			} else {
				str.append("@Schema(description = \""
						+ (doc == null ? "" : doc.toString().replace("\n", "").replace("\r", "").replace("\"", "\\\""))
						+ "\"");
				if (SchemaTypeName.XS_DATETIME.equals(pNode.getXsTypeName())) {
					str.append(", format = \"date-time\"");
				}
				str.append(")");
			}
			if (pNode.getMaxOccurs() > 1) {
				str.append(")");
			}
			str.append(CR);
			str.append(TAB[1] + "private " + this.getJavaTypeForNodeInDTO(pNode, false, inDTO, pExtraImports) + " "
					+ GenerateJavaAccessers.getAttributeNameForNode(pNode) + ";" + CR);
		}

		return str.toString();
	}

	private DataTransferObject getDTOInclusion(final SchemaNode pNode, final DataTransferObject pDTO)
			throws EBXCommonsException {
		for (IncludedRelation includedRelation : pDTO.getIncludedRelations()) {
			if (includedRelation.getField().getPath().equals(pNode.getPathInAdaptation().format())) {
				if (includedRelation.getRelatedDTO() == null) {
					if (pNode.isAssociationNode()) {
						// TODO modify by prefferedDTO
						includedRelation.setRelatedDTO(
								this.getOrRegisterTable(pNode.getAssociationLink().getTableNode()).getPreferredDTO());
					} else if (pNode.getFacetOnTableReference() != null) {
						includedRelation.setRelatedDTO(this
								.getOrRegisterTable(pNode.getFacetOnTableReference().getTableNode()).getPreferredDTO());
					}
				}
				return includedRelation.getRelatedDTO();
			}
		}
		return null;
	}

	/**
	 * Format a name of instance variable for a given field.
	 *
	 * @param pNode The node for which to format the corresponding instance variable
	 *              name.
	 *
	 * @return The instance variable name as String
	 */
	private static String getAttributeNameForNode(final SchemaNode pNode) {
		return getAttributeNameForNode(pNode, true);
	}

	/**
	 * Format a name of instance variable for a given field.
	 *
	 * @param pNode            The node for which to format the corresponding
	 *                         instance variable name.
	 * @param upToTerminalNode if true, stop name composition at terminal node.
	 *
	 * @return The instance variable name as String
	 */
	private static String getAttributeNameForNode(final SchemaNode pNode, final boolean upToTerminalNode) {
		String name = StringUtils.capitalize(pNode.getPathInSchema().getLastStep().format());
		SchemaNode node = pNode.getParent();
		while (node != null && !node.getPathInSchema().isRoot()) {
			if (node.getPathInAdaptation().isRoot() || node.isTableNode()
					|| upToTerminalNode && (node.isTerminalValue() || SchemaUtils.isTerminalUnderTerminal(node))) {
				break;
			}
			name = StringUtils.capitalize(node.getPathInSchema().getLastStep().format()) + name;
			node = node.getParent();
		}

		return StringUtils.uncapitalize(name);
	}

	private static String getClassSignatureForBean(final JavaBean pBean) {
		return pBean.getJavaPackage() + "." + pBean.getJavaClassName();
	}

	private static String getClassSignatureForDAO(final DataAccessObject pDao) {
		return pDao.getJavaPackage() + "." + pDao.getJavaClassName();
	}

	private static String getClassSignatureForDTO(final DataTransferObject pDTO) {
		return pDTO.getJavaPackage() + "." + pDTO.getJavaClassName();
	}

	private static String getClassSignatureForMapper(final DataTransferObject pDTO) throws EBXCommonsException {
		return pDTO.getMapper().getJavaPackage() + "." + pDTO.getMapper().getJavaClassName();
	}

	private static String getClassSignatureForService(final Service pService) {
		return pService.getJavaPackage() + "." + pService.getJavaClassName();
	}

	private static String getConstantNameForPathToField(final SchemaNode pNode) {
		return "path_to_field_" + GenerateJavaAccessers.getAttributeNameForNode(pNode, false);
	}

	private static String getConstantNameForPathToTable(final SchemaNode pNode) {
		return "path_to_table_" + GenerateJavaAccessers.getAttributeNameForNode(pNode);
	}

	private static String getGetterNameForNode(final SchemaNode pNode) {
		String functionName = "get";
		String attributeName = StringUtils.capitalize(GenerateJavaAccessers.getAttributeNameForNode(pNode));
		return functionName + attributeName;
	}

	/**
	 * Format a name of Java class name for a given table.
	 *
	 * @param pNode The node for which to format the corresponding Java class name.
	 *
	 * @return The Java class name.
	 */
	private static String getJavaClassName(final SchemaNode pNode) {

		if (pNode.getParent() == null) {
			Optional<String> modelFileName = SchemaUtils.getModelFileName(pNode.getSchemaLocation());
			if (modelFileName.isPresent()) {
				return StringUtils.capitalize(modelFileName.get().replaceAll("[^a-zA-Z]", ""));
			} else {
				return StringUtils.capitalize(pNode.getSchemaLocation().format().replaceAll("[^a-zA-Z]", ""));
			}
		}
		String pathLastStep = pNode.getPathInSchema().getLastStep().format();
		return StringUtils.capitalize(pathLastStep);
	}

	/**
	 * Retrieve the right Java type corresponding to the type of the field. It can
	 * be a generated Java Bean if the field is a terminal complex type.
	 *
	 * @param pNode The node for which to retrieve the Java type.
	 *
	 * @return The Java Type.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	private String getJavaTypeForNodeInBean(final SchemaNode pNode, final boolean pForceMono,
			final Set<String> pExtraImports) throws EBXCommonsException {
		String type = "";
		if ((pNode.isAssociationNode() || pNode.getMaxOccurs() > 1) && !pForceMono) {
			type = "List<";
			pExtraImports.add("java.util.List");
		}
		if (DamaUtils.isNodeDAC(pNode)) {
			type += MediaType.class.getSimpleName();
			pExtraImports.add(MediaType.class.getCanonicalName());
		} else if (pNode.isComplex()
				&& (pNode.isTerminalValue() || !pNode.isTerminalValue() && pNode.getJavaBeanClass() != null)) {
			JavaBean bean = this.getOrRegisterBean(pNode);
			type += bean.getJavaClassName();
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForBean(bean));
		} else if (pNode.isAssociationNode()) {
			SchemaNode tableNode = pNode.getAssociationLink().getTableNode();
			JavaBean bean = this.getOrRegisterBean(tableNode);
			DataAccessObject dao = this.getOrRegisterDAO(tableNode);
			type += bean.getJavaClassName();
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForBean(bean));
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(dao));
		} else if (pNode.getFacetOnTableReference() != null) {
			SchemaNode tableNode = pNode.getFacetOnTableReference().getTableNode();
			JavaBean bean = this.getOrRegisterBean(tableNode);
			DataAccessObject dao = this.getOrRegisterDAO(tableNode);
			type += bean.getJavaClassName();
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForBean(bean));
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(dao));
		} else if (TYPES_MAP.get(pNode.getXsTypeName().toString()) == null) {

			type += "String";
		} else {
			type += TYPES_MAP.get(pNode.getXsTypeName().toString());
			if (IMPORT_MAP.get(pNode.getXsTypeName().toString()) != null) {
				pExtraImports.add(IMPORT_MAP.get(pNode.getXsTypeName().toString()));
			}
		}

		if ((pNode.isAssociationNode() || pNode.getMaxOccurs() > 1) && !pForceMono) {
			type += ">";
		}
		return type;
	}

	private Table getOrRegisterTable(final SchemaNode pNode) throws EBXCommonsException {
		Table table = this.TABLES.get(pNode.getSchemaLocation()).get(pNode.getPathInSchema().format());
		if (table == null) {
			table = ModelTableDAO.getInstance().getInstanceOfBean(this.DATASETS.get(pNode.getSchemaLocation()));
			DataAccessObject dao = this.getOrRegisterDAO(pNode);
			table.setDaobject(dao);
			table.setJavaBean(dao.getJavaBean());
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				table.inherit(ModelTableDAO.path_to_field_javaBean);
			}
			table.inherit(ModelTableDAO.path_to_field_javaBean);
			table.setPathInSchema(pNode.getPathInSchema().format());
			table.setDataModel(this.DATA_MODELS.get(pNode.getSchemaLocation()));
			table = ModelTableDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, table);
			table.setJavaBean(dao.getJavaBean());
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				table.inherit(ModelTableDAO.path_to_field_javaBean);
			}
			this.TABLES.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), table);
			dao.setTable(table);
			DataAccessObjectDAO.getInstance().update(this.procedureContext, dao.getEbxRecord(), dao);
			JavaBean bean = dao.getJavaBean();
			bean.setTable(table);
			JavaBeanDAO.getInstance().update(this.procedureContext, bean.getEbxRecord(), bean);
		}
		return table;
	}

	private Field getOrRegisterField(final SchemaNode pNode) throws EBXCommonsException {
		Field field = this.FIELDS.get(pNode.getSchemaLocation()).get(pNode.getPathInSchema().format());
		if (field == null && (pNode.isTerminalValue() || SchemaUtils.isTerminalUnderTerminal(pNode))
				&& !pNode.isTableNode()) {
			field = FieldDAO.getInstance().getInstanceOfBean(this.DATASETS.get(pNode.getSchemaLocation()));
			field.setDataModel(this.DATA_MODELS.get(pNode.getSchemaLocation()));
			if (pNode.getTableNode() != null) {
				field.setTable(this.getOrRegisterTable(pNode.getTableNode()));
			}
			field.setPath(pNode.getPathInAdaptation().format());
			if (pNode.isComplex()) {
				field = FieldDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, field);
				field.setJavaBean(this.getOrRegisterBean(pNode, field));
				field.setType(COMPLEX_FIELD);
				field = FieldDAO.getInstance().update(this.procedureContext, this.DATA_MODELS_DATASET, field);
			} else {
				if (pNode.isAssociationNode()) {
					field.setType(ASSOCIATION_FIELD);
				} else if (pNode.getFacetOnTableReference() != null) {
					field.setType(FK_FIELD);
				} else if (SchemaUtils.isPrimaryKey(pNode)) {
					field.setType(PK_FIELD);
				} else {
					field.setType(SIMPLE_FIELD);
				}
				field = FieldDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, field);
			}
			this.FIELDS.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), field);
		}
		return field;
	}

	private JavaBean getOrRegisterBean(final SchemaNode pNode) throws EBXCommonsException {
		return this.getOrRegisterBean(pNode, null);
	}

	private JavaBean getOrRegisterBean(final SchemaNode pNode, final Field pField) throws EBXCommonsException {
		JavaBean bean = this.BEANS.get(pNode.getSchemaLocation()).get(pNode.getPathInSchema().format());

		if (bean == null) {
			bean = JavaBeanDAO.getInstance().getInstanceOfBean(this.DATASETS.get(pNode.getSchemaLocation()));
			DataModel dataModel = this.DATA_MODELS.get(pNode.getSchemaLocation());
			bean.setDataModel(dataModel);
			if (pNode.getJavaBeanClass() != null) {
				bean.setJavaClassName(pNode.getJavaBeanClass().getSimpleName());
				bean.setJavaPackage(pNode.getJavaBeanClass().getPackage().getName());
				bean.setSpecific(Boolean.TRUE);
				bean.setGenerationActive(Boolean.FALSE);
			} else {
				bean.setJavaClassName(GenerateJavaAccessers.getJavaClassName(pNode));
				bean.setJavaPackage(dataModel.getBeansPackage());
				if (!pNode.getPathInSchema().equals(Path.ROOT)) {
					bean.inherit(JavaBeanDAO.path_to_field_javaPackage);
				}
			}
			Table table = this.TABLES.get(pNode.getSchemaLocation()).get(pNode.getPathInSchema().format());
			bean.setTable(table);
			if (pNode.getParent() == null) {
				bean.setType(DATASET_BEAN_TYPE);
			} else if (pNode.isTableNode()) {
				bean.setType(TABLE_BEAN_TYPE);
			} else {
				bean.setType(COMPLEX_TYPE_BEAN_TYPE);
				table = this.TABLES.get(pNode.getSchemaLocation())
						.get(SchemaUtils.getTableNode(pNode).getPathInSchema().format());
				bean.setTable(table);
				bean.setField(pField);
			}
			bean = JavaBeanDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, bean);
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				if (pNode.getJavaBeanClass() != null) {
					bean.setJavaPackage(pNode.getJavaBeanClass().getPackage().getName());
				} else {
					bean.setJavaPackage(dataModel.getBeansPackage());
					if (!pNode.getPathInSchema().equals(Path.ROOT)) {
						bean.inherit(JavaBeanDAO.path_to_field_javaPackage);
					}
				}
			}
			this.BEANS.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), bean);
		}
		return bean;
	}

	private DataAccessObject getOrRegisterDAO(final SchemaNode pNode) throws EBXCommonsException {
		DataAccessObject dao = this.DAOS.get(pNode.getSchemaLocation()).get(pNode.getPathInSchema().format());
		if (dao == null) {
			dao = DataAccessObjectDAO.getInstance().getInstanceOfBean(this.DATASETS.get(pNode.getSchemaLocation()));
			dao.setJavaClassName(getJavaClassName(pNode) + SUFFIX_DAO);
			DataModel dataModel = this.DATA_MODELS.get(pNode.getSchemaLocation());
			Table table = this.TABLES.get(pNode.getSchemaLocation()).get(pNode.getPathInSchema().format());
			dao.setTable(table);
			dao.setJavaPackage(dataModel.getDaoPackage());
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				dao.inherit(DataAccessObjectDAO.path_to_field_javaPackage);
			}
			JavaBean bean = this.getOrRegisterBean(pNode);
			dao.setJavaBean(bean);
			dao = DataAccessObjectDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, dao);
			dao.setJavaPackage(dataModel.getDaoPackage());
			dao.setJavaBean(bean);
			dao.inherit(DataAccessObjectDAO.path_to_field_javaBean);
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				dao.inherit(DataAccessObjectDAO.path_to_field_javaPackage);
			}
			this.DAOS.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), dao);
			if (table != null && table.getDaobject() == null) {
				table.setDaobject(dao);
				ModelTableDAO.getInstance().update(this.procedureContext, this.DATA_MODELS_DATASET, table);
			}
			if (pNode.getParent() == null) {
				dataModel.setDaobject(dao);
				DataModelDAO.getInstance().update(this.procedureContext, this.DATA_MODELS_DATASET, dataModel);
			}
		} else {
			if (dao.getJavaBean() == null) {
				JavaBean bean = this.getOrRegisterBean(pNode);
				dao.setJavaBean(bean);
				dao = DataAccessObjectDAO.getInstance().update(this.procedureContext, this.DATA_MODELS_DATASET, dao);
				if (dao.getJavaPackage() == null) {
					dao.setJavaPackage(bean.getJavaPackage());
				}
				this.DAOS.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), dao);
			}
		}
		return dao;
	}

	private List<DataTransferObject> getOrRegisterDTO(final SchemaNode pNode) throws EBXCommonsException {
		List<DataTransferObject> dtos = this.DTOS.get(pNode.getSchemaLocation()).get(pNode.getPathInSchema().format());
		if (dtos == null) {
			dtos = new ArrayList<>();
		}
		if (dtos.isEmpty()) {
			DataTransferObject dto = DataTransferObjectDAO.getInstance()
					.getInstanceOfBean(this.DATASETS.get(pNode.getSchemaLocation()));
			DataModel dataModel = this.DATA_MODELS.get(pNode.getSchemaLocation());
			Table table = null;
			if (pNode.isTableNode()) {
				table = this.getOrRegisterTable(pNode);
			} else {
				dto.setField(this.getOrRegisterField(pNode));
				table = this.getOrRegisterTable(SchemaUtils.getTableNode(pNode));
			}
			dto.setTable(table);
			dto.setJavaPackage(dataModel.getDtoPackage());
			JavaBean bean = this.getOrRegisterBean(pNode);
			dto.setJavaClassName(bean.getJavaClassName() + SUFFIX_DTO);
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				dto.inherit(DataTransferObjectDAO.path_to_field_javaPackage);
			}
			dto = DataTransferObjectDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, dto);
			Mapper mapper = MapperDAO.getInstance().getInstanceOfBean(this.DATASETS.get(pNode.getSchemaLocation()));
			mapper.setJavaPackage(dataModel.getMappersPackage());
			mapper.setJavaClassName(bean.getJavaClassName() + TO + dto.getJavaClassName() + SUFFIX_MAPPER);
			mapper.setDto(dto);
			mapper.inherit(MapperDAO.path_to_field_javaPackage);
			mapper = MapperDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, mapper);
			mapper.setJavaPackage(dataModel.getMappersPackage());
			dto.setMapper(mapper);
			dto = DataTransferObjectDAO.getInstance().update(this.procedureContext, this.DATA_MODELS_DATASET, dto);
			dto.setJavaClassName(GenerateJavaAccessers.getJavaClassName(pNode) + SUFFIX_DTO);
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				dto.inherit(DataTransferObjectDAO.path_to_field_javaPackage);
			}
			this.DTOS.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), Arrays.asList(dto));
			dtos.add(dto);
			if (pNode.isTableNode() && table.getPreferredDTO() == null) {
				table.setPreferredDTO(dto);
				table = ModelTableDAO.getInstance().update(this.procedureContext, this.DATA_MODELS_DATASET, table);
				this.TABLES.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), table);
			}
		}
		return dtos;
	}

	private List<Service> getOrRegisterService(final SchemaNode pNode) throws EBXCommonsException {
		List<Service> services = this.SERVICES.get(pNode.getSchemaLocation()).get(pNode.getPathInSchema().format());
		if (services == null) {
			services = new ArrayList<>();
		}
		if (services.isEmpty()) {
			Service service = ServiceDAO.getInstance().getInstanceOfBean(this.DATASETS.get(pNode.getSchemaLocation()));
			service.setDataTransferObject(this.getOrRegisterTable(pNode).getPreferredDTO());
			DataModel dataModel = this.DATA_MODELS.get(pNode.getSchemaLocation());
			service.setJavaPackage(dataModel.getServicesPackage());
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				service.inherit(ServiceDAO.path_to_field_javaPackage);
			}
			service.setJavaClassName(GenerateJavaAccessers.getJavaClassName(pNode) + SUFFIX_SERVICE);
			service = ServiceDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, service);
			service.setJavaPackage(dataModel.getServicesPackage());
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				service.inherit(ServiceDAO.path_to_field_javaPackage);
			}
			this.SERVICES.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), Arrays.asList(service));
			services.add(service);
		}
		return services;
	}

	private List<RestService> getOrRegisterRestServices(final SchemaNode pNode) throws EBXCommonsException {
		List<RestService> restServices = this.REST_SERVICES.get(pNode.getSchemaLocation())
				.get(pNode.getPathInSchema().format());
		if (restServices == null) {
			restServices = new ArrayList<>();
		}
		if (restServices.isEmpty()) {
			RestService rs = RestServiceDAO.getInstance()
					.getInstanceOfBean(this.DATASETS.get(pNode.getSchemaLocation()));
			rs.setService(this.getOrRegisterService(pNode).get(0));
			Table table = this.getOrRegisterTable(pNode);
			rs.setTable(table);
			rs.setDto(table.getPreferredDTO());
			DataModel dataModel = this.DATA_MODELS.get(pNode.getSchemaLocation());
			rs.setJavaPackage(dataModel.getRsPackage());
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				rs.inherit(RestServiceDAO.path_to_field_javaPackage);
			}
			rs.setJavaClassName(GenerateJavaAccessers.getJavaClassName(pNode) + SUFFIX_REST_SERVICE);
			rs.setResourceName(this.getRestServiceEndPointName(pNode));
			rs = RestServiceDAO.getInstance().create(this.procedureContext, this.DATA_MODELS_DATASET, rs);
			rs.setJavaPackage(dataModel.getRsPackage());
			if (!pNode.getPathInSchema().equals(Path.ROOT)) {
				rs.inherit(RestServiceDAO.path_to_field_javaPackage);
			}
			this.REST_SERVICES.get(pNode.getSchemaLocation()).put(pNode.getPathInSchema().format(), Arrays.asList(rs));
			restServices.add(rs);
		}
		return restServices;
	}

	private String getJavaTypeForNodeInDTO(final SchemaNode pNode, final boolean pForceMono,
			final DataTransferObject pIncludedDTO, final Set<String> pExtraImports) throws EBXCommonsException {
		String type = "";
		if ((pNode.isAssociationNode() || pNode.getMaxOccurs() > 1) && !pForceMono) {
			type = "List<";
			pExtraImports.add("java.util.List");
		}
		if (DamaUtils.isNodeDAC(pNode)) {
			pExtraImports.add("com.orchestranetworks.addon.dama.models.MediaType");
			type += "MediaType";
		} else if ((pNode.isAssociationNode() || pNode.getFacetOnTableReference() != null) && pIncludedDTO != null) {
			type += pIncludedDTO.getJavaClassName();
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDTO(pIncludedDTO));
		} else if (pNode.isComplex() && pNode.isTerminalValue()) {
			type += this.getOrRegisterDTO(pNode).get(0).getJavaClassName();
		} else if (pNode.isComplex() && (pNode.getMaxOccurs() > 1 || pNode.getAccessMode().isReadWrite())) {
			JavaBean bean = this.getOrRegisterBean(pNode);
			type += bean.getJavaClassName();
			pExtraImports.add(GenerateJavaAccessers.getClassSignatureForBean(bean));
		} else if (TYPES_MAP.get(pNode.getXsTypeName().toString()) == null) {
			type += "String";
		} else {
			type += TYPES_MAP.get(pNode.getXsTypeName().toString());
			if (IMPORT_MAP.get(pNode.getXsTypeName().toString()) != null) {
				pExtraImports.add(IMPORT_MAP.get(pNode.getXsTypeName().toString()));
			}
		}
		if ((pNode.isAssociationNode() || pNode.getMaxOccurs() > 1) && !pForceMono) {
			type += ">";
		}
		return type;
	}

	/**
	 * Gets attribute names of a node as list.
	 *
	 * @param pNode - table occurrence node.
	 * @return array list of attribute names.
	 */
	private List<String> getListOfAttributeNamesForNode(final SchemaNode pNode) {
		List<String> attributes = new ArrayList<>();
		for (SchemaNode node : pNode.getNodeChildren()) {
			if ((node.isTerminalValue() && !node.isTableNode() || node.isTerminalValueDescendant())
					&& !node.isAssociationNode()) {
				attributes.add(GenerateJavaAccessers.getAttributeNameForNode(node));
			} else if (!node.isAssociationNode()) {
				attributes.addAll(this.getListOfAttributeNamesForNode(node));
			}
		}
		return attributes;
	}

	private static String getPrimaryKeysGetters(final AdaptationTable pTable) {
		StringBuilder str = new StringBuilder();
		for (Path path : pTable.getPrimaryKeySpec()) {
			str.append("\"" + GenerateJavaAccessers
					.getGetterNameForNode(pTable.getTableOccurrenceRootNode().getNode(Path.SELF.add(path))) + "\",");
		}
		str.deleteCharAt(str.length() - 1);
		return str.toString();
	}

	private String getRestServiceEndPointName(final SchemaNode pNode) throws EBXCommonsException {
		return this.getOrRegisterBean(pNode).getJavaClassName().toLowerCase();
	}

	private static String getSetterNameForNode(final SchemaNode pNode) {
		String functionName = "set";
		String attributeName = GenerateJavaAccessers.getAttributeNameForNode(pNode);
		functionName += attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		return functionName;
	}

	private static String getTableGetterName(final SchemaNode pTableNode) {
		String tableName = pTableNode.getPathInAdaptation().getLastStep().format();
		tableName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1, tableName.length());
		return "get" + tableName + "Table";
	}

	/**
	 * Gets java instructions to set values of a record to the Java Beans instance
	 * variables of all fields below the one in parameter. It identifies on the fly
	 * packages to be imported in case extra treatments are necessary such as for
	 * foreign key fields.
	 *
	 * @param pNode         The node from which to put values in instance variables.
	 * @param pExtraImports Set collecting extra packages to be imported.
	 *
	 * @return Java instructions as String.
	 * @throws OperationException
	 */
	private String getValuesFromAdaptationForFieldsBelow(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		String className = pBean.getJavaClassName();
		for (SchemaNode node : pNode.getNodeChildren()) {

			String beanParameter = "p" + className;
			String setValue = beanParameter + "." + GenerateJavaAccessers.getSetterNameForNode(node);
			if (!node.isTableNode() && !node.isValueFunction() && !node.isAssociationNode() && (node.isTerminalValue()
					|| node.isTerminalValueDescendant() && (!node.isComplex() || node.getMaxOccurs() > 1))) {
				if (node.getFacetOnTableReference() == null) {
					String type = this.getJavaTypeForNodeInBean(node, false, pExtraImports);
					str.append(TAB[2]
							+ "if(!pPermissions.isPresent() || !pPermissions.get().getNodeAccessPermission(pRecord.getSchemaNode().getNode("
							+ GenerateJavaAccessers.getConstantNameForPathToField(node) + "), pRecord).isHidden()){"
							+ CR);
					str.append(TAB[3] + setValue + "(" + ("Object".equals(type) ? "" : "(" + type + ") ")
							+ "pRecord.get(" + GenerateJavaAccessers.getConstantNameForPathToField(node) + "));\n");
					str.append(TAB[2] + "}" + CR);
				}
				if (node.getInheritanceProperties() != null) {
					pExtraImports.add("com.onwbp.adaptation.AdaptationValue");
					str.append(TAB[2] + "if(AdaptationValue.INHERIT_VALUE.equals(pRecord.getValueWithoutResolution("
							+ GenerateJavaAccessers.getConstantNameForPathToField(node) + "))){" + CR);
					str.append(TAB[3] + beanParameter + ".inherit("
							+ GenerateJavaAccessers.getConstantNameForPathToField(node) + ");" + CR);
					str.append(TAB[2] + "}else{" + CR);
					str.append(TAB[3] + beanParameter + ".overwrite("
							+ GenerateJavaAccessers.getConstantNameForPathToField(node) + ");" + CR);
					str.append(TAB[2] + "}" + CR);
				}
			} else {
				str.append(this.getValuesFromAdaptationForFieldsBelow(node, pBean, pExtraImports));
			}
		}

		return str.toString();
	}

	/**
	 * Gets java instructions to set values of a value context to the Java Beans
	 * instance variables of all fields below the one in parameter. It identifies on
	 * the fly packages to be imported in case extra treatments are necessary such
	 * as for foreign key fields.
	 *
	 * @param pNode         The node from which to put values in instance variables.
	 * @param pExtraImports Set collecting extra packages to be imported.
	 *
	 * @return Java instructions as String.
	 * @throws OperationException
	 */
	private String getValuesFromValueContextForFieldsBelow(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		String className = pBean.getJavaClassName();
		for (SchemaNode node : pNode.getNodeChildren()) {
			String beanParameter = "p" + className;
			String setValue = beanParameter + "." + GenerateJavaAccessers.getSetterNameForNode(node);
			if (!node.isTableNode() && !node.isValueFunction() && !node.isAssociationNode() && (node.isTerminalValue()
					|| node.isTerminalValueDescendant() && (!node.isComplex() || node.getMaxOccurs() > 1))) {
				String path = "pathToRecordRoot.add(" + GenerateJavaAccessers.getConstantNameForPathToField(node) + ")";
				if (node.getFacetOnTableReference() == null) {
					str.append(TAB[2] + "if(!pPermissions.isPresent() || ");
					str.append(
							"record.isPresent() && !pPermissions.get().getNodeAccessPermission(record.get().getSchemaNode().getNode("
									+ GenerateJavaAccessers.getConstantNameForPathToField(node)
									+ "), record.get()).isHidden() || ");
					str.append("!pPermissions.get().getNodeAccessPermission(pContext.getNode(" + path
							+ "), pContext.getAdaptationInstance()).isHidden()){" + CR);
					String type = this.getJavaTypeForNodeInBean(node, false, pExtraImports);
					str.append(TAB[3] + setValue + "(" + ("Object".equals(type) ? "" : "(" + type + ") ")
							+ "pContext.getValue(" + path + "));\n");
					str.append(TAB[2] + "}" + CR);
				}
				if (node.getInheritanceProperties() != null) {
					pExtraImports.add("com.onwbp.adaptation.AdaptationValue");
					str.append(TAB[2] + "if(record.isPresent()){" + CR);
					str.append(
							TAB[3] + "if(AdaptationValue.INHERIT_VALUE.equals(record.get().getValueWithoutResolution("
									+ GenerateJavaAccessers.getConstantNameForPathToField(node) + "))){" + CR);
					str.append(TAB[4] + beanParameter + ".inherit("
							+ GenerateJavaAccessers.getConstantNameForPathToField(node) + ");" + CR);
					str.append(TAB[3] + "}else{" + CR);
					str.append(TAB[4] + beanParameter + ".overwrite("
							+ GenerateJavaAccessers.getConstantNameForPathToField(node) + ");" + CR);
					str.append(TAB[3] + "}" + CR);
					str.append(TAB[2] + "}" + CR);
				}
			} else {
				str.append(this.getValuesFromValueContextForFieldsBelow(node, pBean, pExtraImports));
			}
		}
		return str.toString();
	}

	private void initConfiguration(final Repository pRepository) throws EBXCommonsException {
		this.TABLES.clear();
		this.FIELDS.clear();
		this.BEANS.clear();
		this.DAOS.clear();
		this.DTOS.clear();
		this.SERVICES.clear();
		this.REST_SERVICES.clear();
		this.GENERATED_DTOS.clear();
		for (SchemaLocation schema : this.schemas) {
			DataModel dataModel = this.DATA_MODELS.get(schema);
			Map<String, Table> tables = new HashMap<>();
			Map<String, Field> fields = new HashMap<>();
			Map<String, DataAccessObject> daos = new HashMap<>();
			Map<String, JavaBean> beans = new HashMap<>();
			Map<String, List<DataTransferObject>> dtos = new HashMap<>();
			Map<String, List<Service>> services = new HashMap<>();
			Map<String, List<RestService>> rss = new HashMap<>();
			DataAccessObject dao = dataModel.getDaobject();
			if (dao != null) {
				beans.put(Path.ROOT.format(), dao.getJavaBean());
				daos.put(Path.ROOT.format(), dao);
			}
			for (Field field : dataModel.getFields()) {
				fields.put(field.getPath(), field);
			}
			for (Table table : dataModel.getTables()) {
				for (Field field : table.getFields()) {
					fields.put(table.getPathInSchema() + field.getPath(), field);
					if (field.getJavaBean() != null) {
						beans.put(table.getPathInSchema() + field.getPath(), field.getJavaBean());
					}
					if (!field.getDtos().isEmpty()) {
						dtos.put(table.getPathInSchema() + field.getPath(), field.getDtos());
					}
				}
				dao = table.getDaobject();
				if (dao != null) {
					beans.put(table.getPathInSchema(), dao.getJavaBean());
					daos.put(table.getPathInSchema(), dao);
				}
				tables.put(table.getPathInSchema(), table);
				List<Service> tableServices = new ArrayList<>();
				dtos.put(table.getPathInSchema(), table.getDtos());
				for (DataTransferObject dto : table.getDtos()) {
					tableServices.addAll(dto.getServices());
				}
				rss.put(table.getPathInSchema(), table.getRsServices());
				services.put(table.getPathInSchema(), tableServices);
			}
			this.TABLES.put(schema, tables);
			this.FIELDS.put(schema, fields);
			this.BEANS.put(schema, beans);
			this.DAOS.put(schema, daos);
			this.DTOS.put(schema, dtos);
			this.SERVICES.put(schema, services);
			this.REST_SERVICES.put(schema, rss);
			this.numberOfBeans = 0;
			this.numberOfDAOs = 0;
			this.numberOfDTOs = 0;
			this.numberOfMappers = 0;
			this.numberOfServices = 0;
			this.numberOfRS = 0;
		}
	}

	private void initServiceVariables(final UserServiceSetupObjectContext<DatasetEntitySelection> pContext) {
		this.schemas = new HashSet<>();
		this.DATASETS = new HashMap<>();
		this.objectKeys = new HashMap<>();
		this.DATA_MODELS_DATASET = BeansRepositoryUtils.getDataModelsDataset(pContext.getRepository());

		Adaptation dataset = pContext.getEntitySelection().getDataset();
		this.schema = dataset.getSchemaLocation();
		this.schemas.add(dataset.getSchemaLocation());
		this.DATASETS.put(dataset.getSchemaLocation(), dataset);
		for (Adaptation linkedDataset : SchemaUtils.getLinkedDataModels(dataset, true)) {
			if (this.schemas.add(linkedDataset.getSchemaLocation())) {
				this.DATASETS.put(linkedDataset.getSchemaLocation(), linkedDataset);
			}
		}
	}

	@Override
	public UserServiceEventOutcome processEventOutcome(
			final UserServiceProcessEventOutcomeContext<DatasetEntitySelection> pContext,
			final UserServiceEventOutcome pOutcome) {
		if (pOutcome instanceof CustomOutcome) {
			CustomOutcome action = (CustomOutcome) pOutcome;
			switch (action) {
			case CONFIGURATION_STEP:
				this.step = new ConfigurationStep();
				break;

			case GENERATION_STEP:
				this.step = new GenerateStep();
				break;
			}
			return null;
		}
		return pOutcome;
	}

	@Override
	public void setupDisplay(final UserServiceSetupDisplayContext<DatasetEntitySelection> pContext,
			final UserServiceDisplayConfigurator pConfigurator) {
		if (ConfigurationStep.class.isInstance(this.step)) {
			for (SchemaLocation schema : GenerateJavaAccessers.this.schemas) {
				ObjectKey key = GenerateJavaAccessers.this.objectKeys.get(schema);
				pContext.getValueContext(key, DataModelDAO.path_to_field_moduleName)
						.setNewValue(schema.getModuleName());
				pContext.getValueContext(key, DataModelDAO.path_to_field_schemaLocation)
						.setNewValue(SchemaUtils.getSchemaPathInModule(schema));
			}
		}

		try {
			this.step.setupDisplay(pContext, pConfigurator);
		} catch (OperationException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void setupObjectContext(final UserServiceSetupObjectContext<DatasetEntitySelection> pContext,
			final UserServiceObjectContextBuilder pBuilder) {
		if (pContext.isInitialDisplay()) {
			this.initServiceVariables(pContext);
			for (SchemaLocation schemaLocation : this.schemas) {
				try {
					this.setupObjectForSchema(pContext, pBuilder, schemaLocation);
				} catch (BeansTechnicalException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	private void setupObjectForSchema(final UserServiceSetupObjectContext<DatasetEntitySelection> pContext,
			final UserServiceObjectContextBuilder pBuilder, final SchemaLocation schemaLocation)
			throws BeansTechnicalException {
		ObjectKey key = ObjectKey.forName("S" + UUID.randomUUID().toString().replace("-", ""));
		this.objectKeys.put(schemaLocation, key);
		DataModel model = BeansRepositoryUtils.getDataModel(pContext.getRepository(), schemaLocation);
		if (model == null) {
			pBuilder.registerNewRecord(key, BeansRepositoryUtils.getDataModelTable(pContext.getRepository()));
		} else {
			pBuilder.registerRecordOrDataSet(key, model.getEbxRecord());
		}
	}

	/**
	 * Gets java instructions to set the values of the Java Beans instance variables
	 * to a ValueContextForUpdate.
	 *
	 * @param pNode The node from which to put values in value context.
	 *
	 * @return Java instructions as String.
	 * @throws EBXCommonsException
	 * @throws ConstraintViolationException
	 * @throws OperationException
	 */
	private String setValuesForUpdateForFieldsBelow(final SchemaNode pNode, final JavaBean pBean,
			final Set<String> pExtraImports)
			throws EBXCommonsException, ConstraintViolationException, OperationException {
		StringBuilder str = new StringBuilder();
		String className = pBean.getJavaClassName();
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.isTableNode() || node.isValueFunction()) {
				continue;
			}
			String beanParameter = "p" + className;
			String getValue = beanParameter + "." + GenerateJavaAccessers.getGetterNameForNode(node) + "()";
			if (!node.isAssociationNode() && (node.isTerminalValue()
					|| node.isTerminalValueDescendant() && !(node.isComplex() && node.getMaxOccurs() > 1))) {
				String constantToPath = GenerateJavaAccessers.getConstantNameForPathToField(node);
				int baseTab = 0;
				if (node.getInheritanceProperties() != null) {
					pExtraImports.add("com.onwbp.adaptation.AdaptationValue");
					str.append(TAB[2] + "if(" + beanParameter + ".doesInherit(" + constantToPath + ")){" + CR);
					str.append(
							TAB[3] + "pContext.setValue(AdaptationValue.INHERIT_VALUE," + constantToPath + ");" + CR);
					str.append(TAB[2] + "}else{" + CR);
					baseTab = 1;
				}
				if (node.getFacetOnTableReference() != null) {
					JavaBean distantBean = this.getOrRegisterBean(node.getFacetOnTableReference().getTableNode());
					DataAccessObject distantDAO = this.getOrRegisterDAO(node.getFacetOnTableReference().getTableNode());
					pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(distantDAO));
					if (node.getMaxOccurs() > 1) {
						pExtraImports.add(GenerateJavaAccessers.getClassSignatureForBean(distantBean));
						String serialListVariableName = GenerateJavaAccessers.getAttributeNameForNode(node)
								+ "_serialized_list";
						str.append(TAB[baseTab + 2] + "List<String> " + serialListVariableName
								+ " = new ArrayList<String>();" + CR);
						pExtraImports.add("java.util.List");
						pExtraImports.add("java.util.ArrayList");
						str.append(TAB[baseTab + 2] + "for(" + distantBean.getJavaClassName() + " record : " + getValue
								+ "){" + CR);
						str.append(TAB[baseTab + 3] + serialListVariableName + ".add(" + distantDAO.getJavaClassName()
								+ ".getInstance().getRecordPrimaryKey(record));" + CR);
						str.append(TAB[baseTab + 2] + "}" + CR);
						str.append(TAB[baseTab + 2] + "pContext.setValue(" + serialListVariableName + ","
								+ constantToPath + ");" + CR);
					} else {
						str.append(TAB[baseTab + 2] + "if(" + getValue + " != null){" + CR);
						str.append(TAB[baseTab + 3] + "pContext.setValue(" + distantDAO.getJavaClassName()
								+ ".getInstance().getRecordPrimaryKey(" + getValue + ")," + constantToPath + ");" + CR);
						str.append(TAB[baseTab + 2] + "}" + CR);
					}
				} else {
					str.append(TAB[baseTab + 2] + "pContext.setValue(" + getValue + "," + constantToPath + ");" + CR);
				}
				if (node.getInheritanceProperties() != null) {
					str.append(TAB[2] + "}" + CR);
				}
			} else {
				str.append(this.setValuesForUpdateForFieldsBelow(node, pBean, pExtraImports));
			}
		}

		return str.toString();
	}

	private Object setValuesFromBeanForDTOForFieldsBelow(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		JavaBean bean = this.getOrRegisterBean(pNode);
		String parameterName = "pBean";
		String variableName = StringUtils.uncapitalize(pDTO.getJavaClassName());
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.isComplex() && node.isTerminalValue() && !DamaUtils.isNodeDAC(node)) {
				if (node.getMaxOccurs() > 1) {
					DataTransferObject dto = this.getOrRegisterDTO(node).get(0);
					pExtraImports.add("java.util.List");
					pExtraImports.add("java.util.ArrayList");
					String variablePKListName = GenerateJavaAccessers.getAttributeNameForNode(node) + "PKList";
					str.append(TAB[2] + "if(" + parameterName + "." + GenerateJavaAccessers.getGetterNameForNode(node)
							+ "() != null){" + CR);
					bean = this.getOrRegisterBean(node);
					str.append(TAB[3] + "List<" + dto.getJavaClassName() + "> " + variablePKListName
							+ " = new ArrayList<>();" + CR);
					str.append(TAB[3] + "for(" + bean.getJavaClassName() + " bean : " + parameterName + "."
							+ GenerateJavaAccessers.getGetterNameForNode(node) + "()){" + CR);
					str.append(TAB[4] + variablePKListName + ".add(" + dto.getMapper().getJavaClassName()
							+ ".getInstance().getDTO(bean));" + CR);
					str.append(TAB[3] + "}" + CR);
					str.append(TAB[3] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "("
							+ variablePKListName + ");" + CR);
					str.append(TAB[2] + "}" + CR);
				} else {
					DataTransferObject dto = this.getOrRegisterDTO(node).get(0);
					str.append(TAB[2] + "if(" + parameterName + "." + GenerateJavaAccessers.getGetterNameForNode(node)
							+ "() != null){" + CR);
					str.append(TAB[3] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "("
							+ dto.getMapper().getJavaClassName() + ".getInstance().getDTO(" + parameterName + "."
							+ GenerateJavaAccessers.getGetterNameForNode(node) + "()));" + CR);
					str.append(TAB[2] + "}" + CR);
				}
			} else if (node.isAssociationNode()) {
				DataTransferObject includedDTO = this.getDTOInclusion(node, pDTO);
				if (includedDTO != null) {
					pExtraImports.add("java.util.List");
					pExtraImports.add("java.util.ArrayList");
					String variableListName = GenerateJavaAccessers.getAttributeNameForNode(node) + "List";
					String variableAssoName = GenerateJavaAccessers.getAttributeNameForNode(node) + "Asso";
					str.append(TAB[2] + "List<" + bean.getJavaClassName() + "> " + variableAssoName + " = "
							+ parameterName + "." + GenerateJavaAccessers.getGetterNameForNode(node) + "();" + CR);
					str.append(TAB[2] + "if(!" + variableAssoName + ".isEmpty()){" + CR);
					str.append(TAB[3] + "List<" + includedDTO.getJavaClassName() + "> " + variableListName
							+ " = new ArrayList<>();" + CR);
					str.append(TAB[3] + "for(" + bean.getJavaClassName() + " bean : " + variableAssoName + "){" + CR);
					str.append(TAB[4] + variableListName + ".add(" + includedDTO.getMapper().getJavaClassName()
							+ ".getInstance().getDTO(bean));" + CR);
					str.append(TAB[3] + "}" + CR);
					str.append(TAB[3] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "("
							+ variableListName + ");" + CR);
					str.append(TAB[2] + "}" + CR);
				}
			} else if (node.isTerminalValue()
					|| node.isTerminalValueDescendant() && (!node.isComplex() || node.getMaxOccurs() > 1)) {
				if (node.getFacetOnTableReference() != null) {
					DataTransferObject includedDTO = this.getDTOInclusion(node, pDTO);
					DataAccessObject distantDAO = this.getOrRegisterDAO(node.getFacetOnTableReference().getTableNode());
					if (node.getMaxOccurs() > 1) {
						pExtraImports.add("java.util.List");
						pExtraImports.add("java.util.ArrayList");
						String variableListName = GenerateJavaAccessers.getAttributeNameForNode(node) + "List";
						str.append(TAB[2] + "if(" + parameterName + "."
								+ GenerateJavaAccessers.getGetterNameForNode(node) + "() != null){" + CR);
						if (includedDTO != null) {
							pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDTO(includedDTO));
							str.append(TAB[3] + "List<" + includedDTO.getJavaClassName() + "> " + variableListName
									+ " = new ArrayList<>();" + CR);
							str.append(TAB[3] + "for(" + bean.getJavaClassName() + " bean : " + parameterName + "."
									+ GenerateJavaAccessers.getGetterNameForNode(node) + "()){" + CR);
							str.append(TAB[4] + variableListName + ".add(" + includedDTO.getMapper().getJavaClassName()
									+ ".getInstance().getDTO(bean));" + CR);
						} else {
							str.append(TAB[3] + "List<String> " + variableListName + " = new ArrayList<>();" + CR);
							pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(distantDAO));
							str.append(TAB[3] + "for(String primaryKey : " + distantDAO.getJavaClassName()
									+ ".getInstance()");
							str.append(".getRecordPrimaryKeys(" + parameterName + "."
									+ GenerateJavaAccessers.getGetterNameForNode(node) + "())){" + CR);
							str.append(TAB[4] + variableListName + ".add(primaryKey);" + CR);
						}
						str.append(TAB[3] + "}" + CR);
						str.append(TAB[3] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "("
								+ variableListName + ");" + CR);
						str.append(TAB[2] + "}" + CR);
					} else {
						if (includedDTO != null) {
							pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDTO(includedDTO));
							str.append(TAB[2] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node)
									+ "(" + includedDTO.getMapper().getJavaClassName() + ".getInstance().getDTO("
									+ parameterName + "." + GenerateJavaAccessers.getGetterNameForNode(node) + "()));"
									+ CR);
						} else {
							str.append(TAB[2] + "if(" + parameterName + "."
									+ GenerateJavaAccessers.getGetterNameForNode(node) + "() != null){" + CR);
							pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDAO(distantDAO));
							str.append(TAB[3] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node)
									+ "(" + distantDAO.getJavaClassName() + ".getInstance()");
							str.append(".getRecordPrimaryKey(" + parameterName + "."
									+ GenerateJavaAccessers.getGetterNameForNode(node) + "()));" + CR);
							str.append(TAB[2] + "}" + CR);
						}
					}
				} else {
					str.append(TAB[2] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "(");
					if (DamaUtils.isNodeDAC(node)) {
						pExtraImports
								.add("com.tibco.ebx.cs.commons.beans.generator.template.MediaTypeToMediaTypeDTOMapper");
						str.append("MediaTypeToMediaTypeDTOMapper.getInstance().getDTO(");
						str.append(
								parameterName + "." + GenerateJavaAccessers.getGetterNameForNode(node) + "()));" + CR);
					} else {
						str.append(
								parameterName + "." + GenerateJavaAccessers.getGetterNameForNode(node) + "());" + CR);
					}
				}
			} else {
				str.append(this.setValuesFromBeanForDTOForFieldsBelow(node, pDTO, pExtraImports));
			}
		}
		return str.toString();
	}

	private Object setValuesFromDTOForBeanForFieldsBelow(final SchemaNode pNode, final DataTransferObject pDTO,
			final Set<String> pExtraImports) throws OperationException, EBXCommonsException {
		StringBuilder str = new StringBuilder();
		JavaBean bean = pDTO.getTable().getJavaBean();
		if (pDTO.getField() != null) {
			bean = pDTO.getField().getJavaBean();
		}
		String variableName = StringUtils.uncapitalize(bean.getJavaClassName());
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.isComplex() && node.isTerminalValue() && !DamaUtils.isNodeDAC(node)) {
				DataTransferObject dto = this.getOrRegisterDTO(node).get(0);
				pExtraImports.add(GenerateJavaAccessers.getClassSignatureForMapper(dto));
				if (node.getMaxOccurs() > 1) {
					pExtraImports.add("java.util.List");
					pExtraImports.add("java.util.ArrayList");
					String variablePKListName = GenerateJavaAccessers.getAttributeNameForNode(node) + "PKList";
					str.append(TAB[2] + "if(pDTO." + GenerateJavaAccessers.getGetterNameForNode(node) + "() != null){"
							+ CR);
					// TODO do not take first one but allow to configure it in model.
					bean = this.getOrRegisterBean(node);
					pExtraImports.add(GenerateJavaAccessers.getClassSignatureForBean(bean));
					pExtraImports.add(GenerateJavaAccessers.getClassSignatureForDTO(dto));
					str.append(TAB[3] + "List<" + bean.getJavaClassName() + "> " + variablePKListName
							+ " = new ArrayList<>();" + CR);
					str.append(TAB[3] + "for(" + dto.getJavaClassName() + " dto :  pDTO."
							+ GenerateJavaAccessers.getGetterNameForNode(node) + "()){" + CR);
					str.append(TAB[4] + variablePKListName + ".add(" + dto.getMapper().getJavaClassName()
							+ ".getInstance().getBean(pDataset,dto, pPermissions));" + CR);
					str.append(TAB[3] + "}" + CR);
					str.append(TAB[3] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "("
							+ variablePKListName + ");" + CR);
					str.append(TAB[2] + "}" + CR);
				} else {
					str.append(TAB[2] + "if(pDTO." + GenerateJavaAccessers.getGetterNameForNode(node) + "() != null){"
							+ CR);
					str.append(TAB[3] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "("
							+ dto.getMapper().getJavaClassName() + ".getInstance().getBean(pDataset,pDTO."
							+ GenerateJavaAccessers.getGetterNameForNode(node) + "(), pPermissions));" + CR);
					str.append(TAB[2] + "}" + CR);
				}
			} else if (node.isAssociationNode()) {
				DataTransferObject inDTO = this.getDTOInclusion(node, pDTO);
				if (inDTO != null) {
					// pExtraImports.add("java.util.List");
					// pExtraImports.add("java.util.ArrayList");
					// String variableListName = this.getAttributeNameForNode(node) + "List";
					// String variableAssoName = this.getAttributeNameForNode(node) + "Asso";
					// str.append(TAB[2] + variableAssoName + " = " + parameterName + "." +
					// this.getGetterNameForNode(node) + "();" + CR);
					// str.append(TAB[2] + "if(!" + variableAssoName + ".isEmpty()){" + CR);
					// str.append(TAB[3] + "List<" + includedDTO.getJavaClassName() + "> " +
					// variableListName + " = new ArrayList<>();" + CR);
					// str.append(TAB[3] + "for(" + includedDTO.getTable().getJavaBean() + " bean :
					// " + variableAssoName + "){" + CR);
					// str.append(TAB[4] + variableListName + ".add(new " +
					// includedDTO.getJavaClassName() + "(bean));" + CR);
					// str.append(TAB[3] + "}" + CR);
					// str.append(TAB[3] + "this." + this.getAttributeNameForNode(node) + " = " +
					// variableListName + ";" + CR);
					// str.append(TAB[2] + "}" + CR);
				}
			} else if (node.isTerminalValue()
					|| node.isTerminalValueDescendant() && (!node.isComplex() || node.getMaxOccurs() > 1)) {
				SchemaFacetTableRef facetOnTableReference = node.getFacetOnTableReference();
				if (facetOnTableReference != null) {
					DataTransferObject inDTO = this.getDTOInclusion(node, pDTO);
					if (inDTO != null) {

					} else {
						DataAccessObject distantDAO = this.getOrRegisterDAO(facetOnTableReference.getTableNode());

						pExtraImports.add("com.onwbp.adaptation.PrimaryKey");
						str.append(TAB[2] + "if(pDTO." + GenerateJavaAccessers.getGetterNameForNode(node)
								+ "() != null) {" + CR);
						String datasetVariableName = "pDataset";
						if (facetOnTableReference.getContainerReference() != null) {
							datasetVariableName = "dataset" + GenerateJavaAccessers.getJavaClassName(node);
							str.append(TAB[3] + "Adaptation " + datasetVariableName + " = ");
							String dataspace = facetOnTableReference.getContainerHome() == null ? "Optional.empty()"
									: "Optional.ofNullable(\"" + facetOnTableReference.getContainerHome().getName()
											+ "\")";
							str.append("AdaptationUtils.getDataset(pDataset.createValueContext()," + dataspace
									+ ",Optional.of(\"" + facetOnTableReference.getContainerReference().getStringName()
									+ "\"));" + CR);
							pExtraImports.add("com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils");
						}
						str.append(
								TAB[3] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "(");
						str.append(distantDAO.getJavaClassName() + ".getInstance().read"
								+ (node.getMaxOccurs() > 1 ? "All" : "") + "(pDTO."
								+ GenerateJavaAccessers.getGetterNameForNode(node) + "()," + datasetVariableName
								+ ", pPermissions)"
								+ (node.getMaxOccurs() > 1 ? ""
										: ".orElse(" + distantDAO.getJavaClassName()
												+ ".getInstance().getInstanceOfBeanFromPK(" + datasetVariableName
												+ ",PrimaryKey.parseString(pDTO."
												+ GenerateJavaAccessers.getGetterNameForNode(node) + "())))")
								+ ");" + CR);
						str.append(TAB[2] + "}" + CR);
					}
				} else {
					str.append(TAB[2] + variableName + "." + GenerateJavaAccessers.getSetterNameForNode(node) + "(");
					if (DamaUtils.isNodeDAC(node)) {
						pExtraImports
								.add("com.tibco.ebx.cs.commons.beans.generator.template.MediaTypeToMediaTypeDTOMapper");
						str.append("MediaTypeToMediaTypeDTOMapper.getInstance().getBean(pDTO.");
						str.append(GenerateJavaAccessers.getGetterNameForNode(node) + "()));" + CR);
					} else {
						str.append("pDTO." + GenerateJavaAccessers.getGetterNameForNode(node) + "());" + CR);
					}
				}
			} else {
				str.append(this.setValuesFromDTOForBeanForFieldsBelow(node, pDTO, pExtraImports));
			}
		}
		return str.toString();
	}

	@Override
	public void validate(final UserServiceValidateContext<DatasetEntitySelection> pContext) {
		for (ObjectKey key : this.objectKeys.values()) {
			ValueContextForInputValidation vcfip = pContext.getValueContext(key);
			String beanFolder = (String) vcfip.getValue(DataModelDAO.path_to_field_beansSourceFolder);
			String daoFolder = (String) vcfip.getValue(DataModelDAO.path_to_field_daoSourceFolder);
			String beanPackage = (String) vcfip.getValue(DataModelDAO.path_to_field_beansPackage);
			String daoPackage = (String) vcfip.getValue(DataModelDAO.path_to_field_daoPackage);

			if (StringUtils.isBlank(beanFolder) || StringUtils.isBlank(daoFolder) || StringUtils.isBlank(beanPackage)
					|| StringUtils.isBlank(daoPackage)) {
				pContext.addError("Source folders and packages for Bean and DAO must be specified for all data models");
			}

			GenerateJavaAccessers.checkFolderExist(pContext, vcfip, beanFolder);
			GenerateJavaAccessers.checkFolderExist(pContext, vcfip, daoFolder);
			String dtoFolder = (String) vcfip.getValue(DataModelDAO.path_to_field_dtoSourceFolder);
			GenerateJavaAccessers.checkFolderExist(pContext, vcfip, dtoFolder);
			String servicesFolder = (String) vcfip.getValue(DataModelDAO.path_to_field_servicesSourceFolder);
			GenerateJavaAccessers.checkFolderExist(pContext, vcfip, servicesFolder);
			String rsFolder = (String) vcfip.getValue(DataModelDAO.path_to_field_rsSourceFolder);
			GenerateJavaAccessers.checkFolderExist(pContext, vcfip, rsFolder);
		}
	}

	/**
	 * Write the generated Java class in the corresponding file.
	 *
	 * @param pJavaClassName The name of the Java Class used as file name.
	 * @return True if a file has been replaced.
	 *
	 */
	private static boolean writeJavaFile(final String pSourceFolder, final String pJavaPackage,
			final String pJavaClassName, final String pJavaClassContent) throws OperationException {
		File directory = new File(pSourceFolder);
		if (!directory.exists()) {
			throw OperationException.createError("No folder at " + pSourceFolder + ".");
		}
		if (!directory.isDirectory()) {
			throw OperationException.createError(pSourceFolder + " is not a directory.");
		}
		if (!directory.canWrite()) {
			throw OperationException.createError("Cannot write in " + pSourceFolder + ".");
		}

		File packageDirectory = new File(pSourceFolder + "/" + pJavaPackage.replaceAll("\\.", "/"));
		packageDirectory.mkdirs();

		if (pJavaClassName.equals("DynamicAttributeDTO")) {
			// TODO ???
		}

		File file = new File(packageDirectory.getAbsolutePath() + "/" + pJavaClassName + ".java");
		boolean overwritting = file.exists();
		try (FileWriter writer = new FileWriter(file)) {
			Optional<String> version = CommonsProperties.getVersion();
			writer.write("/**" + CR);
			writer.write("* This class has been automatically generated by ebx-cs-commons "
					+ (version.isPresent() ? version.get() : "Unknown verison") + CR);
			writer.write("*/" + CR);
			writer.write(pJavaClassContent);
			CommonsLogger.getLogger().info("Java class " + pJavaClassName + " generated on " + file.getCanonicalPath());
		} catch (IOException ex) {
			CommonsLogger.getLogger().error("Failed to write Java Class " + pJavaClassName + " : " + ex.getMessage(),
					ex);
			throw OperationException
					.createError("Failed to write Java Class " + pJavaClassName + " : " + ex.getMessage(), ex);
		}
		return overwritting;
	}
}
