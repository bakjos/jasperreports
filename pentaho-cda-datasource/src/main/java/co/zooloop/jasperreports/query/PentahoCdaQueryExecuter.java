package co.zooloop.jasperreports.query;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import co.zooloop.jasperreports.PentahoCdaDataSource;
import co.zooloop.jasperreports.connection.PentahoCdaConnection;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRAbstractCompiler;
import net.sf.jasperreports.engine.design.JRClassGenerator;
import net.sf.jasperreports.engine.design.JRCompilationSourceCode;
import net.sf.jasperreports.engine.design.JRCompilationUnit;
import net.sf.jasperreports.engine.design.JRCompiler;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRReportCompileData;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRFillDataset;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.fill.JRFillVariable;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;

public class PentahoCdaQueryExecuter extends JRAbstractQueryExecuter {

	private static final Log logger = LogFactory.getLog(PentahoCdaQueryExecuter.class);
	private Map<String, ? extends JRValueParameter> reportParameters;
	private Map<String, Object> parameters;
	private PentahoCdaQueryWrapper wrapper;
	private boolean directParameters;
	private Map<String, PentahoCdaParameter> cdaParameterMap; 

	public PentahoCdaQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters) {
		this(jasperReportsContext, dataset, parameters, false);
	}

	private static final int NAME_SUFFIX_RANDOM_MAX = 1000000;
	private static final Random random = new Random();

	private static String createNameSuffix() {
		return "_" + System.currentTimeMillis() + "_" + random.nextInt(NAME_SUFFIX_RANDOM_MAX);
	}

	protected static String getUnitName(JRReport report, JRDataset dataset, String nameSuffix) {
		String className;
		if (dataset.isMainDataset()) {
			className = report.getName();
		} else {
			className = report.getName() + "_" + dataset.getName();
		}

		className = JRStringUtil.getJavaIdentifier(className) + nameSuffix;

		return className;
	}

	/*
	 * protected String compileUnits(final JRCompilationUnit[] units, String
	 * classpath, File tempDirFile) { final INameEnvironment env =
	 * getNameEnvironment(units);
	 * 
	 * final IProblemFactory problemFactory = new
	 * DefaultProblemFactory(Locale.getDefault());
	 * 
	 * final CompilerRequestor requestor = getCompilerRequestor(units);
	 * 
	 * final Compiler compiler = new Compiler(env, policy, getJdtSettings(),
	 * requestor, problemFactory);
	 * 
	 * do { CompilationUnit[] compilationUnits =
	 * requestor.processCompilationUnits();
	 * 
	 * compiler.compile(compilationUnits); } while
	 * (requestor.hasMissingMethods());
	 * 
	 * requestor.processProblems();
	 * 
	 * return requestor.getFormattedProblems(); }
	 */

	protected <T> Map<String, T> toMap(T[] array) {
		Map<String, T> map = new HashMap<String, T>();

		for (T t : array) {
			try {
				Method m = t.getClass().getMethod("getName");
				String name = (String) m.invoke(t);
				map.put(name, t);
			} catch (Exception e) {

			}
		}

		return map;
	}

	public PentahoCdaQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters, boolean directParameters) {
		super(jasperReportsContext, dataset, parameters);
		this.directParameters = directParameters;
		this.reportParameters = parameters;
		this.parameters = new HashMap();
		this.parseQuery();
		
		String queryString = this.getQueryString();
		if ( !StringUtils.isBlank(queryString) ) {
			
			net.sf.jasperreports.engine.design.JasperDesign jasperDesign  = (net.sf.jasperreports.engine.design.JasperDesign)jasperReportsContext.getValue("JasperDesign");
			JRExpressionCollector expressionCollector = JRExpressionCollector.collector(jasperReportsContext, jasperDesign);
			Map<String, JRExpression> expressionMap = new HashMap<String, JRExpression>();
			GsonBuilder builder = new GsonBuilder();
	        Gson gson = builder.create();
	        cdaParameterMap = new HashMap<String, PentahoCdaParameter>();
			
			List<PentahoCdaParameter> params =  gson.fromJson(queryString, new TypeToken<ArrayList<PentahoCdaParameter>>() {}.getType());
			
			if ( dataset instanceof JRFillDataset) {
			
				for ( PentahoCdaParameter param:  params) {
					
					JRDesignExpression expression = new JRDesignExpression((String)param.getValue());
					expressionCollector.addExpression(expression);
					expression.setId(expressionCollector.getExpressionId(expression));
					expressionMap.put(param.getName(), expression);
					cdaParameterMap.put(param.getName(), param);
					
				}
				
				String nameSuffix = createNameSuffix();
				
				String unitName = getUnitName(jasperDesign, dataset, nameSuffix);
				JREvaluator evaluator = null;
				
				JRSourceCompileTask sourceTask = new JRSourceCompileTask(jasperDesign, jasperDesign.getMainDesignDataset(), expressionCollector, unitName);
				try {
					JRCompilationSourceCode sourceCode = JRClassGenerator.generateClass(sourceTask);
					JRCompilationUnit[] units = new JRCompilationUnit[] {
							new JRCompilationUnit(unitName, sourceCode, null, expressionCollector.getExpressions(dataset), sourceTask)
					};
					String classpath = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(JRCompiler.COMPILER_CLASSPATH);
					PentahoCdaQueryCompiler compiler = new PentahoCdaQueryCompiler(jasperReportsContext);
					compiler.compileUnits(units, classpath);
					evaluator = compiler.loadEvaluatorFinal(units[0].getCompileData(), unitName);
					
					WhenResourceMissingTypeEnum whenResourceMissingType = dataset.getWhenResourceMissingTypeValue();
					boolean ignoreNPE = 
						JRPropertiesUtil.getInstance(jasperReportsContext)
							.getBooleanProperty(
								jasperDesign, 
								JREvaluator.PROPERTY_IGNORE_NPE, 
								true
								);
					
					
					
					evaluator.init(toMap((JRFillParameter[])dataset.getParameters()), toMap((JRFillField[])dataset.getFields()),toMap((JRFillVariable[])dataset.getVariables()), whenResourceMissingType, ignoreNPE);
					
				} catch (JRException e1) {
					
				}
				
		        if ( params != null ) {
		        	for ( PentahoCdaParameter param:  params) {
		        		if ( evaluator != null ) {
			        		if ( expressionMap.containsKey(param.getName()) ) {
			        			JRExpression expression = expressionMap.get(param.getName());
			        			Object value = param.getValue();
			        			try {
			        				value = evaluator.evaluate(expression);
			        				cdaParameterMap.get(param.getName()).setValue(value);
								} catch (JRExpressionEvalException e) {
									
								}
			        		}
		        		} else {
		        			
		        		}
		        	}
		        }
			}
	        
		}
	}

	@Override
	public boolean cancelQuery() throws JRException {
		logger.warn("Cancel not implemented");
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	private PentahoCdaConnection processConnection(JRValueParameter valueParameter) throws JRException {
		if (valueParameter == null) {
			throw new JRException("No PentahoCDA connection");
		} else {
			return (PentahoCdaConnection) valueParameter.getValue();
		}
	}

	@Override
	public JRDataSource createDatasource() throws JRException {
		PentahoCdaConnection connection = (PentahoCdaConnection) ((Map) this.getParameterValue("REPORT_PARAMETERS_MAP"))
				.get("REPORT_CONNECTION");
		if (connection == null) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"REPORT_PARAMETERS_MAP: " + ((Map) this.getParameterValue("REPORT_PARAMETERS_MAP")).keySet());
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Direct parameters: " + this.reportParameters.keySet());
			}

			connection = this.processConnection((JRValueParameter) this.reportParameters.get("REPORT_CONNECTION"));
			if (connection == null) {
				throw new JRException("No MongoDB connection");
			}
		}

		this.wrapper = new PentahoCdaQueryWrapper(cdaParameterMap, connection, this.parameters);
		return new PentahoCdaDataSource(this.wrapper);
	}

	@Override
	protected String getParameterReplacement(String parameterName) {
		Object parameterValue = this.reportParameters.get(parameterName);
		if (parameterValue == null) {
			throw new JRRuntimeException("Parameter \"" + parameterName + "\" does not exist.");
		} else {
			if (parameterValue instanceof JRValueParameter) {
				parameterValue = ((JRValueParameter) parameterValue).getValue();
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Geting parameter replacement, parameterName: " + parameterName + "; replacement:"
						+ parameterValue);
			}

			return this.processParameter(parameterName, parameterValue);
		}
	}

	private String processParameter(String parameterName, Object parameterValue) {
		if (parameterValue instanceof Collection) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");

			for (Iterator var4 = ((Collection) parameterValue).iterator(); var4.hasNext(); builder.append(", ")) {
				Object value = var4.next();
				if (value != null) {
					builder.append(this.generateParameterObject(parameterName, value));
				} else {
					builder.append("null");
				}
			}

			if (builder.length() > 2) {
				builder.delete(builder.length() - 2, builder.length());
			}

			builder.append("]");
			return builder.toString();
		} else {
			this.parameters.put(parameterName, parameterValue);
			return this.generateParameterObject(parameterName, parameterValue);
		}
	}

	private String generateParameterObject(String parameterName, Object parameterValue) {
		if (logger.isDebugEnabled()) {
			if (parameterValue != null) {
				logger.debug("Generating parameter object, parameterName: " + parameterName + "; value:"
						+ parameterValue.toString() + "; class:" + parameterValue.getClass().toString());
			} else {
				logger.debug("Generating parameter object, parameterName: " + parameterName + "; value: null");
			}
		}

		return parameterValue != null ? parameterValue.toString() : null;
	}

	public String getProcessedQueryString() {
		return this.getQueryString();
	}

	protected Object getParameterValue(String parameterName, boolean ignoreMissing) {
		try {
			return super.getParameterValue(parameterName, ignoreMissing);
		} catch (Exception var4) {
			return var4.getMessage().endsWith("cannot be cast to net.sf.jasperreports.engine.JRValueParameter")
					&& this.directParameters ? this.reportParameters.get(parameterName) : null;
		}
	}

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public Map<String, PentahoCdaParameter> getCdaParameterMap() {
		return cdaParameterMap;
	}
	
	

}
