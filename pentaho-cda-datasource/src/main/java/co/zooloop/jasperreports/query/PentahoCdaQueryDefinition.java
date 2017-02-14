package co.zooloop.jasperreports.query;

import java.io.Serializable;
import java.util.List;

public class PentahoCdaQueryDefinition implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	private String solution;
	private String path;
	private String file;
	private String dataAccessId;
	
	List<PentahoCdaParameter> parameters;


	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getDataAccessId() {
		return dataAccessId;
	}

	public void setDataAccessId(String dataAccessId) {
		this.dataAccessId = dataAccessId;
	}

	public List<PentahoCdaParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<PentahoCdaParameter> parameters) {
		this.parameters = parameters;
	}
	
	
	
	
	
}
