/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package co.zooloop.jasperreports.studio.data.pentahocda;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;

import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapterImpl;

/**
 * Creator to build a JSS PentahoCda data adapter from the xml definition of an iReport PentahoCda 
 * data adapter
 * 
 * @author Orlandin Marco
 */
public class PentahoCdaDBCreator implements IDataAdapterCreator {

	@Override
	public DataAdapterDescriptor buildFromXML(Document docXML) {
		PentahoCdaDataAdapterImpl result = new PentahoCdaDataAdapterImpl();
		
		NamedNodeMap rootAttributes = docXML.getChildNodes().item(0).getAttributes();
		String connectionName = rootAttributes.getNamedItem("name").getTextContent();
		result.setName(connectionName);
		
		NodeList children = docXML.getChildNodes().item(0).getChildNodes();
		for(int i=0; i<children.getLength(); i++){
			Node node = children.item(i);
			if (node.getNodeName().equals("connectionParameter")){
		
				String paramName = node.getAttributes().getNamedItem("name").getTextContent();
				
				if (paramName.equals("username")) result.setUsername(node.getTextContent()) ;
				if (paramName.equals("baseUrl")) result.setBaseUrl(node.getTextContent());
				if (paramName.equals("password")) result.setPassword(node.getTextContent());
			}
		}

		PentahoCdaDataAdapterDescriptor desc = new PentahoCdaDataAdapterDescriptor();
		desc.setDataAdapter(result);
		return desc;
	}

	@Override
	public String getID() {
		return "com.jaspersoft.ireport.pentaho.connection.PentahoCdaConnection";
	}


}
