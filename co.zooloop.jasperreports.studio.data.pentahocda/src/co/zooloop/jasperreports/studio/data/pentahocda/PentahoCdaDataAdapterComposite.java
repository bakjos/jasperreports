/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package co.zooloop.jasperreports.studio.data.pentahocda;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.engine.JasperReportsContext;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
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


import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.data.ADataAdapterComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.secret.DataAdaptersSecretsProvider;
import com.jaspersoft.studio.swt.widgets.WSecretText;

import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapter;
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
				PentahoFileBrowserDialog dialog = new PentahoFileBrowserDialog(Display.getDefault().getActiveShell());
				dialog.open();
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
		bindingContext.bindValue(SWTObservables.observeSelection(sugarMode), PojoObservables.observeValue(dataAdapter, "sugarMode"));
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
