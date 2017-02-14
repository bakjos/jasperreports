/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package co.zooloop.jasperreports.studio.data.pentahocda;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.data.ADataAdapterComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.secret.DataAdaptersSecretsProvider;
import com.jaspersoft.studio.swt.widgets.WSecretText;
import com.jaspersoft.studio.utils.jobs.CheckedRunnableWithProgress;

import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapter;
import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import co.zooloop.jasperreports.studio.data.pentahocda.messages.Messages;
import co.zooloop.jasperreports.studio.data.pentahocda.tree.RepositoryFileTreeDto;
import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.DataAdapterServiceUtil;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ParameterContributorContext;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class PentahoCdaDataAdapterComposite extends ADataAdapterComposite {
	private Text serverUrlField;
	private Text usernameField;
	private WSecretText passwordField;
	private Button pentaho5Check;
	

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
		
		pentaho5Check = new Button(this, SWT.CHECK);
		pentaho5Check.setText(Messages.PentahoCdaDataAdapterComposite_labelSugarMode);
		pentaho5Check.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
	}
	
	private void createLabel(String text) {
		createLabel(text, 6);
	}

	private void createLabel(String text, int cells) {
		Label label = new Label(this, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, cells, 1));
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
		
		/*if ( dataAdapter.isPentaho5() ) {
			solutionField.setEnabled(false);
			fileField.setEnabled(false);
		}
		
		if ( !StringUtils.isBlank(dataAdapter.getBaseUrl()) &&  !StringUtils.isBlank(dataAdapter.getPath())) {
			fetchQueries();
		}*/
	}

	@Override
	protected void bindWidgets(DataAdapter dataAdapter) {
		bindingContext.bindValue(SWTObservables.observeText(serverUrlField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "baseUrl")); 
		bindingContext.bindValue(SWTObservables.observeText(usernameField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "username")); 
		//bindingContext.bindValue(SWTObservables.observeText(fileField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "file")); 
		//bindingContext.bindValue(SWTObservables.observeText(solutionField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "solution")); 
		//bindingContext.bindValue(SWTObservables.observeText(pathField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "path")); 
		bindingContext.bindValue(SWTObservables.observeText(passwordField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "password"));
		bindingContext.bindValue(SWTObservables.observeSelection(pentaho5Check), PojoObservables.observeValue(dataAdapter, "pentaho5"));
		
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
	
	/**/
}
