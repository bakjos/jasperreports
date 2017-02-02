package co.zooloop.jasperreports.adapter;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import net.sf.jasperreports.data.AbstractDataAdapterService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.util.SecretsUtil;

public class PentahoCdaDataAdapterService extends AbstractDataAdapterService {

	private static final Log log = LogFactory.getLog(AbstractDataAdapterService.class);

	private PentahoCdaDataAdapter dataAdapter;

	private PentahoCdaConnection connection;

	public PentahoCdaDataAdapterService(ParameterContributorContext paramContribContext,
			PentahoCdaDataAdapter dataAdapter) {
		super(paramContribContext, dataAdapter);
		this.dataAdapter = dataAdapter;
	}

	@Override
	public void contributeParameters(Map<String, Object> parameters) throws JRException {
		if (this.connection != null) {
			this.dispose();
		}

		if (this.dataAdapter != null) {
			try {
				this.createConnection();
				parameters.put("REPORT_CONNECTION", this.connection);
			} catch (Exception ex) {
				throw new JRException(ex);
			}
		}
	}

	private void createConnection() throws JRException {
		String password = this.dataAdapter.getPassword();
		SecretsUtil secretService = SecretsUtil.getInstance(this.getJasperReportsContext());
		if (secretService != null) {
			password = secretService.getSecret("net.sf.jasperreports.data.adapter", password);
		}
		this.connection = new PentahoCdaConnection(dataAdapter.getUsername(),password, dataAdapter.getSolution(),
				dataAdapter.getPath(), dataAdapter.getFile(), dataAdapter.isSugarMode(), dataAdapter.getBaseUrl(), dataAdapter.getDataAccessId());
	}

	public void dispose() {
		try {
			if (this.connection != null) {
				this.connection.close();
			}
		} catch (Exception var2) {
			var2.printStackTrace();
			if (log.isErrorEnabled()) {
				log.error("Error while closing the connection.", var2);
			}
		}

	}

	public void test() throws JRException {
		try {
			if (this.connection == null) {
				this.createConnection();
			}

			this.connection.test();
		} finally {
			this.dispose();
		}

	}

}
