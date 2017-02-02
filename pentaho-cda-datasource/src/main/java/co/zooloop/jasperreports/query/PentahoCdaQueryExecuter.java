package co.zooloop.jasperreports.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.zooloop.jasperreports.PentahoCdaDataSource;
import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;

public class PentahoCdaQueryExecuter extends JRAbstractQueryExecuter {

	private static final Log logger = LogFactory.getLog(PentahoCdaQueryExecuter.class);
	private Map<String, ? extends JRValueParameter> reportParameters;
	private Map<String, Object> parameters;
	private PentahoCdaQueryWrapper wrapper;
	private boolean directParameters;

	public PentahoCdaQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters) {
		this(jasperReportsContext, dataset, parameters, false);
	}

	public PentahoCdaQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters, boolean directParameters) {
		super(jasperReportsContext, dataset, parameters);
		this.directParameters = directParameters;
		this.reportParameters = parameters;
		this.parameters = new HashMap();
		this.parseQuery();
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
	        if(valueParameter == null) {
	            throw new JRException("No MongoDB connection");
	        } else {
	            return (PentahoCdaConnection)valueParameter.getValue();
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

		this.wrapper = new PentahoCdaQueryWrapper(this.getQueryString(), connection, this.parameters);
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

		/*return parameterValue != null ? JSONSerializers.getStrict().serialize(parameterValue)
				: "{ " + parameterName + " : null }";*/
		
		return parameterValue.toString();
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

}
