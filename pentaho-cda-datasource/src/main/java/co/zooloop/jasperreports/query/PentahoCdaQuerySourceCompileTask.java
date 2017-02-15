package co.zooloop.jasperreports.query;

import java.util.Map;

import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;
import net.sf.jasperreports.engine.design.JasperDesign;

public class PentahoCdaQuerySourceCompileTask extends JRSourceCompileTask {
	
	public PentahoCdaQuerySourceCompileTask(
			JasperDesign jasperDesign, 
			String unitName, 
			JRExpressionCollector expressionCollector, 
			Map<String, ? extends JRParameter> parametersMap, 
			Map<String, JRField> fieldsMap, 
			Map<String, JRVariable> variablesMap, 
			JRVariable[] variables
			) {
		super (jasperDesign, unitName, expressionCollector, parametersMap, fieldsMap, variablesMap, variables, false);
	}
	

}
