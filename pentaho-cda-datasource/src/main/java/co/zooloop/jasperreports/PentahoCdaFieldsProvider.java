package co.zooloop.jasperreports;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import co.zooloop.jasperreports.query.PentahoCdaQueryExecuter;
import co.zooloop.jasperreports.query.PentahoCdaQueryWrapper;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
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
		  return null;
	 }
	 
	 

}
