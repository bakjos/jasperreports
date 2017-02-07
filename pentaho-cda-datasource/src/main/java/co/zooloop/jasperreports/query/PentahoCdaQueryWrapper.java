package co.zooloop.jasperreports.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import net.sf.jasperreports.engine.JRException;

public class PentahoCdaQueryWrapper{
	private static final Log logger = LogFactory.getLog(PentahoCdaQueryWrapper.class);
    
	public static final String DATA_ACCESS_ID = "dataAccessId";
	public static final String METHOD_DO_QUERY = "doQuery";
	  
    
    
    private PentahoCdaConnection connection;
    private Map<String, Object> parameters;
    private Map<String, PentahoCdaParameter> cdaParameterMap;
    public TypedTableModel model;
   

    public PentahoCdaQueryWrapper( Map<String, PentahoCdaParameter> cdaParameterMap, PentahoCdaConnection connection, Map<String, Object> parameters) throws JRException {
        this.connection = connection;
        this.parameters = parameters;
        this.cdaParameterMap = cdaParameterMap;
        this.processQuery();
    }

    public void processQuery() throws JRException {
        logger.info("Processing CDA query");
        
    	Map<String, String> extraParameters = new HashMap<String, String>();
    	extraParameters.put(DATA_ACCESS_ID, connection.getDataAccessId());
    	
    	if ( parameters != null) {
    		for ( Map.Entry<String, Object> entry: parameters.entrySet() ) {
    			if ( entry.getValue() != null) {
    				extraParameters.put(entry.getKey(), entry.getValue().toString());
    			}
    		}
    	}
    	
    	if ( cdaParameterMap != null) {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		for (PentahoCdaParameter parameter: cdaParameterMap.values()) {
    			String paramName = "param" + parameter.getName();
    			if ( parameter.getValue() != null) {
    				if ( Date.class.getName().equals(parameter.getValueClassName()) ) {
    					Object value = parameter.getValue();
    					Date date = null;
    					if ( value instanceof Date) {
    						date = (Date)value;
    					} else if ( value instanceof String) {
    						try {
								date = sdf.parse(value.toString());
							} catch (ParseException e) {
								date = new java.util.Date();
							}
    					}
    					extraParameters.put(paramName, sdf.format(date));
    				} else {
    					extraParameters.put(paramName, parameter.getValue().toString());
    				}
    				
    			}
    		}
    	}
    	
       	model = connection.fetchData(METHOD_DO_QUERY, extraParameters);
        
    }


}
