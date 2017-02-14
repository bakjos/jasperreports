package co.zooloop.jasperreports.adapter;

import net.sf.jasperreports.data.AbstractDataAdapter;

public class PentahoCdaDataAdapterImpl  extends AbstractDataAdapter implements  PentahoCdaDataAdapter {
	private String username;
	private String password;
	private boolean pentaho5;

	private String baseUrl;
	
	

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

	public boolean isPentaho5() {
		return pentaho5;
	}

	public void setPentaho5(boolean pentaho5) {
		this.pentaho5 = pentaho5;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	
	
}
