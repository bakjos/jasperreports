package co.zooloop.jasperreports.query;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.type.ParameterEvaluationTimeEnum;

public class PentahoCdaParameter implements JRValueParameter {

	private String name;
	private Object value;

	public PentahoCdaParameter(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Object clone() {
		return null;
	}

	public boolean hasProperties() {
		return false;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return null;
	}

	public void setDescription(String description) {
	}

	public Class<?> getValueClass() {
		return this.value != null ? this.value.getClass() : null;
	}

	public String getValueClassName() {
		return this.value != null ? this.value.getClass().getName() : null;
	}

	public boolean isSystemDefined() {
		return false;
	}

	public boolean isForPrompting() {
		return false;
	}

	public JRExpression getDefaultValueExpression() {
		return null;
	}

	public Class<?> getNestedType() {
		return null;
	}

	public String getNestedTypeName() {
		return null;
	}

	public JRPropertiesMap getPropertiesMap() {
		return null;
	}

	public JRPropertiesHolder getParentProperties() {
		return null;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
	}

	@Override
	public ParameterEvaluationTimeEnum getEvaluationTime() {
		return ParameterEvaluationTimeEnum.LATE;
	}

}
