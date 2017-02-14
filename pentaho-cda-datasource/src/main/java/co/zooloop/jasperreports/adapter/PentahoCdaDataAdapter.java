package co.zooloop.jasperreports.adapter;

import net.sf.jasperreports.data.DataAdapter;

public interface PentahoCdaDataAdapter extends DataAdapter {

	String getUsername();

	void setUsername(String username);

	String getPassword();

	void setPassword(String password);
	
	boolean isPentaho5();

	void setPentaho5(boolean pentaho5);

	String getBaseUrl();
		
	void setBaseUrl(String baseUrl);
	
	

}
