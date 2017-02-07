/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package co.zooloop.jasperreports.studio.data.pentahocda.querydesigner;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.DataAdapterServiceUtil;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.designer.QueryDesigner;
import com.jaspersoft.studio.data.designer.UndoRedoImpl;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.data.designer.QueryDesigner.QueryListener;
import com.jaspersoft.studio.wizards.ContextHelpIDs;

import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapterService;
import co.zooloop.jasperreports.connection.PentahoCdaConnection;


public class PentahoCdaQueryDesigner extends QueryDesigner {
	
	
	protected PentahoCdaParameterEditor control;
	

	public Control createControl(Composite parent) {
		control=new PentahoCdaParameterEditor(parent, SWT.BORDER);
		GridLayout controlGl=new GridLayout(1,true);
		controlGl.marginWidth=0;
		controlGl.marginHeight=0;
		control.setLayout(controlGl);
		control.addModifyListener(new QueryListener());
		return control;
	}

	/*protected void queryTextAreaModified() {
		// keep the query info updated
		((JRDesignQuery) jDataset.getQuery()).setText(control.getText());
	}*/

	@Override
	public String getContextHelpId() {
		return ContextHelpIDs.PREFIX.concat("query_pentaho");
	}
	
	@Override
	protected void updateQueryText(String txt) {
		control.setText(txt);
	}
	
	@Override
	protected String getQueryFromWidget() {
		return control.getText();
	}
	
	

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setDataAdapter(DataAdapterDescriptor da) {
		super.setDataAdapter(da);
		
		DataAdapterService daService = DataAdapterServiceUtil.getInstance(new ParameterContributorContext( jConfig, null, null)).getService(da.getDataAdapter());
		
	
		if ( daService instanceof PentahoCdaDataAdapterService) {
			try {
				Map<String, Object> parameters = new HashMap<String, Object>();
				daService.contributeParameters(parameters);
				PentahoCdaConnection connection =  (PentahoCdaConnection) parameters.get(JRParameter.REPORT_CONNECTION);
				control.setConnection(connection);
			} catch(Exception ex ) {
				
			}
		}
	}
	
	
}
