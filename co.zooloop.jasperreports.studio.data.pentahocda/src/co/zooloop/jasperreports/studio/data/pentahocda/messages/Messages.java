/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package co.zooloop.jasperreports.studio.data.pentahocda.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "co.zooloop.jasperreports.studio.data.pentahocda.messages.messages"; //$NON-NLS-1$
	public static String PentahoCdaDataAdapterComposite_labelPassword;
	public static String PentahoCdaDataAdapterComposite_labelURL;
	public static String PentahoCdaDataAdapterComposite_labelUsername;
	public static String PentahoCdaDataAdapterComposite_labelSugarMode;
	
	public static String PentahoCdaDataAdapterComposite_labelSolution;
	public static String PentahoCdaDataAdapterComposite_labelPath;
	public static String PentahoCdaDataAdapterComposite_labelFile;
	public static String PentahoCdaDataAdapterComposite_labelQuery;
	
	public static String PentahoCdaDataAdapterFactory_description;
	public static String PentahoCdaDataAdapterFactory_label;
	public static String RDDatasourcePentahoCdaPage_labelurl;
	public static String RDDatasourcePentahoCdaPage_pass;
	public static String RDDatasourcePentahoCdaPage_title;
	public static String RDDatasourcePentahoCdaPage_username;
	public static String RDDatasourcePentahoCdaPage_solution;
	public static String RDDatasourcePentahoCdaPage_path;
	public static String RDDatasourcePentahoCdaPage_file;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
