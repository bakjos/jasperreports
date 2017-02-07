package co.zooloop.jasperreports.adapter;

import net.sf.jasperreports.data.DataAdapter;

public interface PentahoCdaDataAdapter extends DataAdapter {

	String getUsername();

	void setUsername(String username);

	String getPassword();

	void setPassword(String password);

	String getSolution();

	void setSolution(String solution);

	String getPath();

	void setPath(String path);

	String getFile();

	void setFile(String file);

	boolean isPentaho5();

	void setPentaho5(boolean pentaho5);

	String getBaseUrl();
		
	void setBaseUrl(String baseUrl);
	
	String getDataAccessId();

	void setDataAccessId(String dataAccessId);

}
