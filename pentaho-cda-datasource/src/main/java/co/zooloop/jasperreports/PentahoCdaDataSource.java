package co.zooloop.jasperreports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import co.zooloop.jasperreports.query.PentahoCdaQueryWrapper;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;

public class PentahoCdaDataSource implements JRDataSource {

	private PentahoCdaQueryWrapper wrapper;

	public static final String QUERY_LANGUAGE = "PentahoCdaQuery";

	private static final Log logger = LogFactory.getLog(PentahoCdaDataSource.class);

	ConvertUtilsBean convertUtilsBean;

	TypedTableModel model;
	Map<String, Integer> namePosMap = new HashMap();

	int currentPos = -1;

	public PentahoCdaDataSource(PentahoCdaQueryWrapper wrapper) {
		logger.info("New Pentaho CDA Data Source");
		this.wrapper = wrapper;
		this.model = wrapper.model;
		this.initConverter();
		for (int i = 0; i < model.getColumnCount(); i++) {
			namePosMap.put(model.getColumnName(i), i);
		}
	}

	public void initConverter() {
		this.convertUtilsBean = new ConvertUtilsBean();
		DoubleConverter doubleConverter = new DoubleConverter();
		FloatConverter floatConverter = new FloatConverter();
		IntegerConverter integerConverter = new IntegerConverter();
		LongConverter longConverter = new LongConverter();
		ShortConverter shortConverter = new ShortConverter();
		DateConverter dateConverter = new DateConverter();
		dateConverter.setLocale(Locale.getDefault());
		DateFormat formatter = DateFormat.getDateTimeInstance(3, 3, Locale.getDefault());
		String pattern = ((SimpleDateFormat) formatter).toPattern();
		dateConverter.setPattern(pattern);
		this.convertUtilsBean.register(doubleConverter, Double.TYPE);
		this.convertUtilsBean.register(doubleConverter, Double.class);
		this.convertUtilsBean.register(floatConverter, Float.TYPE);
		this.convertUtilsBean.register(floatConverter, Float.class);
		this.convertUtilsBean.register(integerConverter, Integer.TYPE);
		this.convertUtilsBean.register(integerConverter, Integer.class);
		this.convertUtilsBean.register(longConverter, Long.TYPE);
		this.convertUtilsBean.register(longConverter, Long.class);
		this.convertUtilsBean.register(shortConverter, Short.TYPE);
		this.convertUtilsBean.register(shortConverter, Short.class);
		this.convertUtilsBean.register(dateConverter, Date.class);
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		try {
			String name = field.getDescription();
			if (name == null || name.isEmpty()) {
				name = field.getName();
			}

			if (name == null) {
				return null;
			} else {
				String[] ids = name.split("\\.");
				Object e = this.getCurrentResult(ids);
				return this.converter(field, e, ids[ids.length - 1]);
			}
		} catch (Exception var5) {
			logger.error(var5);
			throw new JRException(var5.getMessage());
		}
	}

	private Object getCurrentResult(String[] ids) {
		Map currentMap = null;
		for (int index = 0; index < ids.length; ++index) {
			boolean isLast = index == ids.length - 1;
			String id = ids[index];
			
			Object currentFieldObject = null;
			if ( currentMap == null) {
				Integer currentFieldPos = namePosMap.get(id);
				if (currentFieldPos == null) {
					return null;
				}

				currentFieldObject = model.getValueAt(currentPos, currentFieldPos);
			} else {
				 currentFieldObject = currentMap.get(id);
				 if (currentFieldObject == null) {
					 return null;
				 }
			}
			
			if (!(currentFieldObject instanceof Map)) {
				if (isLast) {
					return currentFieldObject;
				}

				return null;
			}
			currentMap = (Map)currentFieldObject;
		}

		return null;
	}

	public Object converter(JRField field, Object value, String fieldName) {
		if (value == null) {
			return null;
		} else {
			Class requiredClass = field.getValueClass();
			if (requiredClass.equals(value.getClass())) {
				return value;
			} else if (requiredClass == Object.class) {
				return value;
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Converting value " + value.toString() + " with type " + value.getClass().getName()
							+ " to " + requiredClass.getName() + " type");
				}

				try {
					return requiredClass == String.class ? value.toString()
							: this.convertUtilsBean.convert(value, requiredClass);
				} catch (Exception var7) {
					String message = "Conversion error, field name: \"" + field.getName() + "\" requested type: \""
							+ field.getValueClassName() + "\" received type: \"" + value.getClass().getName()
							+ "\" value: \"" + value.toString() + "\"";
					logger.error(message);
					message.concat("\n");
					message.concat(var7.getMessage());
					throw new ClassCastException(message);
				}
			}
		}
	}

	@Override
	public boolean next() throws JRException {
		if (currentPos < (model.getRowCount() - 1)) {
			currentPos++;
			return true;
		}
		return false;
	}

}
