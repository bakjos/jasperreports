package co.zooloop.jasperreports.query;

import java.util.HashMap;
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
    public TypedTableModel model;
   

    public PentahoCdaQueryWrapper(String queryString, PentahoCdaConnection connection, Map<String, Object> parameters) throws JRException {
        this.connection = connection;
        this.parameters = parameters;
        this.processQuery(queryString);
    }

    public void processQuery(String queryString) throws JRException {
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
       	model = connection.fetchData(METHOD_DO_QUERY, extraParameters);
        
    }


}
