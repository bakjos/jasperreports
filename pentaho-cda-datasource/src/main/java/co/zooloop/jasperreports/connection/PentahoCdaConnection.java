package co.zooloop.jasperreports.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaQueryBackend;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaResponseParser;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import co.zooloop.jasperreports.query.PentahoCdaQueryDefinition;
import net.sf.jasperreports.engine.JRException;

public class PentahoCdaConnection implements Connection {

	private static final char DOMAIN_SEPARATOR = '\\';
	
	private static final String className = "org.pentaho.reporting.platform.plugin.connection.CdaPluginLocalQueryBackend";

	private String username;
	private String password;

	private boolean sugarMode;

	private String baseUrl;

	CdaQueryBackend localBackend;
	
	private boolean local;

	private transient HttpClient client;

	public PentahoCdaConnection(String username, String password,
			boolean sugarMode, String baseUrl) {
		super();
		this.username = username;
		this.password = password;
		this.sugarMode = sugarMode;
		this.baseUrl = baseUrl;
		
		
		try {
			localBackend = ObjectUtilities.loadAndInstantiate( className, CdaQueryBackend.class, CdaQueryBackend.class );
			localBackend.setBaseUrl(baseUrl);
			localBackend.setPassword(password);
			localBackend.setUsername(username);
			localBackend.setSugarMode(sugarMode);
			local = true;
		} catch(Exception ex) {
			local = false;
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public boolean isSugarMode() {
		return sugarMode;
	}

	public void setSugarMode(boolean sugarMode) {
		this.sugarMode = sugarMode;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	


	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Statement createStatement() throws SQLException {
		return null;
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return null;
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return null;
	}

	public String nativeSQL(String sql) throws SQLException {
		return null;
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
	}

	public boolean getAutoCommit() throws SQLException {
		return false;
	}

	public void commit() throws SQLException {
	}

	public void rollback() throws SQLException {
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return null;
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
	}

	public boolean isReadOnly() throws SQLException {
		return false;
	}

	public void setCatalog(String catalog) throws SQLException {
	}

	public String getCatalog() throws SQLException {
		return null;
	}

	public void setTransactionIsolation(int level) throws SQLException {
	}

	public int getTransactionIsolation() throws SQLException {
		return 0;
	}

	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	public void clearWarnings() throws SQLException {
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return null;
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return null;
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
	}

	public void setHoldability(int holdability) throws SQLException {
	}

	public int getHoldability() throws SQLException {
		return 0;
	}

	public Savepoint setSavepoint() throws SQLException {
		return null;
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return null;
	}

	public void rollback(Savepoint savepoint) throws SQLException {
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return null;
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return null;
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return null;
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return null;
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return null;
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return null;
	}

	public Clob createClob() throws SQLException {
		return null;
	}

	public Blob createBlob() throws SQLException {
		return null;
	}

	public NClob createNClob() throws SQLException {
		return null;
	}

	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	public boolean isValid(int timeout) throws SQLException {
		try {
			return this.test() != null;
		} catch (JRException var3) {
			throw new SQLException("Validation failed; Caused by: " + var3.getMessage(), var3);
		}
	}
	
	protected String encodeParameter( final String value ) {
	    if ( StringUtils.isEmpty( value ) ) {
	      return "";
	    }
	    try {
	      return URLEncoder.encode( value, StandardCharsets.ISO_8859_1.name() );
	    } catch ( UnsupportedEncodingException e ) {
	      throw new IllegalStateException( e );
	    }
	  }

	public String createURL( final PentahoCdaQueryDefinition queryDef, final String method, final Map<String, String> extraParameter) {
		final String baseURL = getBaseUrl();
		
		final String basePath = isSugarMode() ? "/plugin/cda/api/" : "/content/cda/";

		final StringBuilder url = new StringBuilder();
		url.append(baseURL);
		url.append(basePath);
		url.append(method);
		url.append("?");
		url.append("outputType=xml");
		
		if ( queryDef != null ) {
			url.append("&path=");
			url.append(encodeParameter(queryDef.getPath()));
			if (!isSugarMode()) {
				url.append("&solution=");
				url.append(encodeParameter(queryDef.getSolution()));
				url.append("&file=");
				url.append(encodeParameter(queryDef.getFile()));
			}
		}
		for (final Map.Entry<String, String> entry : extraParameter.entrySet()) {
			final String key = encodeParameter(entry.getKey());
			if (StringUtils.isEmpty(key)) {
				continue;
			}
			url.append("&");
			url.append(key);
			url.append("=");
			url.append(encodeParameter(entry.getValue()));
		}
		return url.toString();
	}
	
	public TypedTableModel fetchData (final PentahoCdaQueryDefinition queryDef, final String method,  Map<String, String> extraParameter) throws JRException {
		if ( !local ) {
			
			if ( extraParameter == null) {
				extraParameter = new HashMap<String, String>();
			}
			if ( queryDef != null) {
				extraParameter.put("dataAccessId", queryDef.getDataAccessId());
			}
			
			String url = createURL(queryDef, method, extraParameter);
			
			final GetMethod httpCall = new GetMethod( url.toString() );
			final HttpClient client = getHttpClient();
		    int status;
			try {
				status = client.executeMethod( httpCall );
				if ( status == 200) {
					InputStream is =  httpCall.getResponseBodyAsStream();
					try {
						return CdaResponseParser.performParse(is);
					} catch (ReportDataFactoryException e) {
						throw new JRException("Failed to parse data: " + e.getMessage());	
					}
				} else {
					
					try {
						InputStream is =  httpCall.getResponseBodyAsStream();
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						IOUtils.getInstance().copyStreams(is, bos);	
						throw new JRException("Failed to retrieve data: " + new String(bos.toByteArray()));
					} catch(JRException ex) {
						throw ex;
					} catch (Exception ex) {
						throw new JRException("Failed to retrieve data: " + httpCall.getStatusLine());
					}
				}
			} catch (HttpException e) {
				throw new JRException(e);
			} catch (IOException e) {
				throw new JRException(e);
			} 
		} else {
			try {
				localBackend.setPath(queryDef.getPath());
				localBackend.setFile(queryDef.getFile());
				localBackend.setSolution(queryDef.getSolution());
				return localBackend.fetchData(null, method, extraParameter);
			} catch (ReportDataFactoryException e) {
				if ( e.getCause() != null) {
					throw new JRException("Failed to retrieve data", e.getCause());
				} else {
					throw new JRException("Failed to retrieve data", e);
				}
				
			}
		}
		
	}

	public String test() throws JRException {
		Map<String, String> extraParameters = new HashMap<String, String>();
		fetchData(null, "getCdaList", extraParameters);
		return null;
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
	}

	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		return null;
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	public void setSchema(String schema) throws SQLException {
	}

	public String getSchema() throws SQLException {
		return null;
	}

	public void abort(Executor executor) throws SQLException {
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
	}

	public int getNetworkTimeout() throws SQLException {
		return 0;
	}

	@Override
	public void close() throws SQLException {
		if (client != null) {
			client = null;
		}
	}

	@Override
	public boolean isClosed() throws SQLException {
		return client == null;
	}

	public static Credentials getCredentials(final String user, final String password) {
		if (StringUtils.isEmpty(user)) {
			return null;
		}

		final int domainIdx = user.indexOf(DOMAIN_SEPARATOR);
		if (domainIdx == -1) {
			return new UsernamePasswordCredentials(user, password);
		}
		try {
			final String domain = user.substring(0, domainIdx);
			final String username = user.substring(domainIdx + 1);
			final String host = InetAddress.getLocalHost().getHostName();
			return new NTCredentials(username, password, host, domain);
		} catch (UnknownHostException uhe) {
			return new UsernamePasswordCredentials(user, password);
		}
	}

	public HttpClient getHttpClient() {
		if (client == null) {
			client = new HttpClient();
			client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			client.getParams().setAuthenticationPreemptive(true);
			client.getState().setCredentials(AuthScope.ANY, getCredentials(getUsername(), getPassword()));
		}
		return client;
	}

}
