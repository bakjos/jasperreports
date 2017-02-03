/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package co.zooloop.jasperreports.studio.data.pentahocda;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.DataAdapterServiceUtil;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ParameterContributorContext;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.icu.util.StringTokenizer;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.data.ADataAdapterComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.secret.DataAdaptersSecretsProvider;
import com.jaspersoft.studio.swt.widgets.WSecretText;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.utils.jobs.CheckedRunnableWithProgress;

import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapter;
import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import co.zooloop.jasperreports.studio.data.pentahocda.messages.Messages;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class PentahoCdaDataAdapterComposite extends ADataAdapterComposite {
	private Text serverUrlField;
	private Text usernameField;
	private Text solutionField;
	private Text pathField;
	private Text fileField;
	private Text queryField;
	private WSecretText passwordField;
	private Button sugarMode;
	private Button browsePath;
	private int nextRowSelection = -1;

	private PentahoCdaDataAdapterDescriptor dataAdapterDescriptor;

	public PentahoCdaDataAdapterComposite(Composite parent, int style, JasperReportsContext jrContext) {
		super(parent, style, jrContext);
		initComponents();
	}
	
	

	private void initComponents() {
		setLayout(new GridLayout(6, false));

		createLabel(Messages.PentahoCdaDataAdapterComposite_labelURL);
		serverUrlField = new Text(this, SWT.BORDER);
		serverUrlField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		createLabel(Messages.PentahoCdaDataAdapterComposite_labelUsername);
		usernameField = new Text(this, SWT.BORDER);
		usernameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		createLabel(Messages.PentahoCdaDataAdapterComposite_labelPassword);
		passwordField = new WSecretText(this, SWT.BORDER | SWT.PASSWORD);
		passwordField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		sugarMode = new Button(this, SWT.CHECK);
		sugarMode.setText(Messages.PentahoCdaDataAdapterComposite_labelSugarMode);
		sugarMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		createLabel(Messages.PentahoCdaDataAdapterComposite_labelSolution);
		solutionField = new Text(this, SWT.BORDER);
		solutionField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		createLabel(Messages.PentahoCdaDataAdapterComposite_labelPath);
		pathField = new Text(this, SWT.BORDER);
		pathField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		browsePath = new Button(this, SWT.NONE);
		browsePath.setText("Browse");
		browsePath.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 1));
		
		
		
		browsePath.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				final PentahoCdaDataAdapter dataAdapter = dataAdapterDescriptor.getDataAdapter(); 
						
				if ( StringUtils.isBlank(dataAdapter.getBaseUrl()) ) {
					MessageDialog.open(MessageDialog.ERROR, getShell(), "Error", "The server url cannot be empty ", SWT.SHEET);
					serverUrlField.setFocus();
					return;
				}
				try {
					DataAdapterService daService = DataAdapterServiceUtil.getInstance(new ParameterContributorContext( getJrContext(), null, null)).getService(dataAdapter);
					Map<String, Object> parameters = new HashMap<String, Object>();
					daService.contributeParameters(parameters);
					PentahoCdaConnection connection =  (PentahoCdaConnection) parameters.get(JRParameter.REPORT_CONNECTION);
					
					PentahoFileBrowserDialog dialog = new PentahoFileBrowserDialog(Display.getDefault().getActiveShell(), connection);
					String path = "/";
					String fileName = null;
					if ( !StringUtils.isBlank(dataAdapter.getPath())  ) {
						StringTokenizer st = new StringTokenizer(dataAdapter.getPath(), "/");
						String _path = "";
						while (st.hasMoreTokens()) {
							String currentToken = st.nextToken();
							if ( st.hasMoreTokens()) {
								_path += "/" + currentToken; 
							} else {
								fileName = currentToken;
							}
						}
						
						if ( !_path.isEmpty()) {
							path = _path;
						}
					}
					
					if ( dialog.loadPath(path, fileName) ) {
						if ( dialog.open() == Window.OK ) {
							pathField.setText(dialog.getSelectedPath());
						}
					}
					
					connection.close();
					daService.dispose();
				} catch(Exception ex) {
					UIUtils.showErrorDialog("Error loading the connection", ex);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
		
		createLabel(Messages.PentahoCdaDataAdapterComposite_labelFile);
		fileField = new Text(this, SWT.BORDER);
		fileField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		createLabel(Messages.PentahoCdaDataAdapterComposite_labelQuery);
		queryField = new Text(this, SWT.BORDER);
		queryField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
	}

	private void createLabel(String text) {
		Label label = new Label(this, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 6, 1));
	}

	public DataAdapterDescriptor getDataAdapter() {
		if (dataAdapterDescriptor == null) {
			dataAdapterDescriptor = new PentahoCdaDataAdapterDescriptor();
		}
		return dataAdapterDescriptor;
	}

	@Override
	public void setDataAdapter(DataAdapterDescriptor dataAdapterDescriptor) {
		super.setDataAdapter(dataAdapterDescriptor);
		this.dataAdapterDescriptor = (PentahoCdaDataAdapterDescriptor) dataAdapterDescriptor;
		PentahoCdaDataAdapter dataAdapter = (PentahoCdaDataAdapter) dataAdapterDescriptor.getDataAdapter();
		if (!passwordField.isWidgetConfigured()) {
			passwordField.loadSecret(DataAdaptersSecretsProvider.SECRET_NODE_ID, passwordField.getText());
		}
		bindWidgets(dataAdapter);
	}

	@Override
	protected void bindWidgets(DataAdapter dataAdapter) {
		bindingContext.bindValue(SWTObservables.observeText(serverUrlField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "baseUrl")); //$NON-NLS-1$
		bindingContext.bindValue(SWTObservables.observeText(serverUrlField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "baseUrl")); //$NON-NLS-1$
		bindingContext.bindValue(SWTObservables.observeText(usernameField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "username")); //$NON-NLS-1$
		bindingContext.bindValue(SWTObservables.observeText(solutionField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "solution")); //$NON-NLS-1$
		bindingContext.bindValue(SWTObservables.observeText(pathField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "path")); //$NON-NLS-1$
		bindingContext.bindValue(SWTObservables.observeText(passwordField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "password"));
		
	}

	@Override
	public void performAdditionalUpdates() {
		if (JaspersoftStudioPlugin.shouldUseSecureStorage()) {
			passwordField.persistSecret();
			// update the "password" replacing it with the UUID key saved in secure
			// preferences
			PentahoCdaDataAdapter pentahoCdaDataAdapter = (PentahoCdaDataAdapter) dataAdapterDesc.getDataAdapter();
			pentahoCdaDataAdapter.setPassword(passwordField.getUUIDKey());
		}
	}
	
	@Override
	public String getHelpContextId() {
		return PREFIX.concat("adapter_pentahocda"); //$NON-NLS-1$
	}
}
