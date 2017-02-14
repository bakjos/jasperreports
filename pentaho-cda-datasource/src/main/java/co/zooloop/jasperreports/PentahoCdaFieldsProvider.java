package co.zooloop.jasperreports;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import co.zooloop.jasperreports.query.PentahoCdaParameter;
import co.zooloop.jasperreports.query.PentahoCdaQueryExecuter;
import co.zooloop.jasperreports.query.PentahoCdaQueryWrapper;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;

public class PentahoCdaFieldsProvider {

	private static PentahoCdaFieldsProvider instance;
	private static final Lock lock = new ReentrantLock();
	private static final Log logger = LogFactory.getLog(PentahoCdaFieldsProvider.class);
	public static final String FIELD_NAME_SEPARATOR = ".";

	private PentahoCdaFieldsProvider() {
	}

	public static PentahoCdaFieldsProvider getInstance() {
		lock.lock();

		PentahoCdaFieldsProvider var0;
		try {
			if (instance == null) {
				instance = new PentahoCdaFieldsProvider();
			}

			var0 = instance;
		} finally {
			lock.unlock();
		}

		return var0;
	}
	
	 public List<JRDesignField> getFields(JasperReportsContext context, JRDataset dataset, Map<String, Object> parameters, PentahoCdaConnection connection) throws JRException {
		  PentahoCdaQueryExecuter queryExecuter = null;
		  PentahoCdaQueryWrapper wrapper = null;
		  try {
			  Map<String, PentahoCdaParameter> newValueParameters = new HashMap<String, PentahoCdaParameter>();
			  
			  Iterator<String> fieldNames = parameters.keySet().iterator();
			  
			  Object field;
	          while(fieldNames.hasNext()) {
	              String fields = (String)fieldNames.next();
	              field = parameters.get(fields);
	              PentahoCdaParameter newParameter = new PentahoCdaParameter(fields, field);
	              newValueParameters.put(fields, newParameter);
	          }
			  
			  queryExecuter = new PentahoCdaQueryExecuter(context, dataset, newValueParameters);
			  
			  wrapper = new PentahoCdaQueryWrapper(queryExecuter.getQueryDefinition(), connection, queryExecuter.getParameters());
			  
			  TypedTableModel  model = wrapper.model;
			  
			  List<JRDesignField> fields1 = new ArrayList<JRDesignField>();
			  for ( int i = 0; i  <model.getColumnCount(); i++ ) {
				  JRDesignField field1 = new JRDesignField();
				  field1.setName(model.getColumnName(i));
				  field1.setValueClass( model.getColumnClass(i));
				  fields1.add(field1);
			  }
			  
			  return fields1;
			  
			  
		  } finally {
            if(connection != null) {
                try {
					connection.close();
				} catch (SQLException e) {
					throw new JRException(e);
				}
            }

        }
	 }
	 
	 

}
