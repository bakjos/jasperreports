/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package co.zooloop.jasperreports.studio.data.pentahocda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JRDesignField;

import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.utils.parameter.ParameterUtil;

import co.zooloop.jasperreports.connection.PentahoCdaConnection;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class PentahoCdaFieldsProvider implements IFieldsProvider {
	public boolean supportsGetFieldsOperation(JasperReportsConfiguration jConfig) {
		return true;
	}

	public List<JRDesignField> getFields(DataAdapterService con, JasperReportsConfiguration jConfig, JRDataset dataset) throws JRException, UnsupportedOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		con.contributeParameters(parameters);
		ParameterUtil.setParameters(jConfig, dataset, parameters);
		parameters.put(JRParameter.REPORT_MAX_COUNT, 0);

		return co.zooloop.jasperreports.PentahoCdaFieldsProvider.getInstance().getFields(jConfig, dataset, parameters, (PentahoCdaConnection) parameters.get(JRParameter.REPORT_CONNECTION));
	}
}
