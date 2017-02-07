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
	private Text solutionField;
	private Text pathField;
	private Text fileField;
	//private Text queryField;
	private WSecretText passwordField;
	private Button pentaho5Check;
	private Button browsePath;
	private int nextRowSelection = -1;
	private List<String[]> rows = new ArrayList<String[]>();
	
	TableViewer tableViewer;
	Table table;
	
	private class ColumnIndexLabelProvider extends ColumnLabelProvider {
		private int columnIndex;

		private ColumnIndexLabelProvider(int columnIndex) {
			this.columnIndex = columnIndex;
		}

		@Override
		public String getText(Object element) {
			String[] strs = (String[])element;
			switch(columnIndex) {
			case 0:
				return strs[1];
			case 1:
				return strs[0];
			default:
				return strs[columnIndex];
			}
		}
	}

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
		
		pentaho5Check.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();
				solutionField.setEnabled(!btn.getSelection());
				fileField.setEnabled(!btn.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			
			}
		});
		
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
		
		createLabel(Messages.PentahoCdaDataAdapterComposite_labelQuery, 5);
		
		Button fetchButton = new Button(this, SWT.NONE);
		fetchButton.setText("Fetch");
		fetchButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 1));
		
		fetchButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				fetchQueries();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
	
		table = new Table(this, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		//GridData gd2 = new GridData(GridData.FILL_BOTH);
		GridData gd2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1);
		gd2.heightHint = 100;
		table.setLayoutData(gd2);
		table.setHeaderVisible(true);
		
		tableViewer = new TableViewer(table);
		
		TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnName = tableViewerColumnName.getColumn();
		tblclmnColumnName.setWidth(250);
		tblclmnColumnName.setText("Query");
		tableViewerColumnName.setLabelProvider(new ColumnIndexLabelProvider(0));
		
		TableViewerColumn tableViewerColumnCdaId = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnCdaId = tableViewerColumnCdaId.getColumn();
		tblclmnColumnCdaId.setWidth(250);
		tblclmnColumnCdaId.setText("CDA ID");
		tableViewerColumnCdaId.setLabelProvider(new ColumnIndexLabelProvider(1));
		
		tableViewer.setContentProvider(new ListContentProvider());
		tableViewer.setInput(rows);
		
		tableViewer.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if ( selection != null ) {
					String[] firstElement = (String[]) selection.getFirstElement();
					if (firstElement != null) {
						dataAdapterDescriptor.getDataAdapter().setDataAccessId(firstElement[0]);
					}
				} else {
					dataAdapterDescriptor.getDataAdapter().setDataAccessId(null);
				}
			}
			
		});
		
		
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
		
		if ( dataAdapter.isPentaho5() ) {
			solutionField.setEnabled(false);
			fileField.setEnabled(false);
		}
		
		if ( !StringUtils.isBlank(dataAdapter.getBaseUrl()) &&  !StringUtils.isBlank(dataAdapter.getPath())) {
			fetchQueries();
		}
	}

	@Override
	protected void bindWidgets(DataAdapter dataAdapter) {
		bindingContext.bindValue(SWTObservables.observeText(serverUrlField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "baseUrl")); 
		bindingContext.bindValue(SWTObservables.observeText(usernameField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "username")); 
		bindingContext.bindValue(SWTObservables.observeText(fileField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "file")); 
		bindingContext.bindValue(SWTObservables.observeText(solutionField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "solution")); 
		bindingContext.bindValue(SWTObservables.observeText(pathField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "path")); 
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
	
	private void fetchQueries() {
		final PentahoCdaDataAdapter dataAdapter = dataAdapterDescriptor.getDataAdapter(); 
		
		if ( StringUtils.isBlank(dataAdapter.getBaseUrl()) ) {
			MessageDialog.open(MessageDialog.ERROR, getShell(), "Error", "The server url cannot be empty ", SWT.SHEET);
			serverUrlField.setFocus();
			return;
		}		
		
		ProgressMonitorDialog dialog= new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, new CheckedRunnableWithProgress() {
				
				GetMethod httpCall;

				@Override
				protected void runOperations(IProgressMonitor monitor) {
					try {
						DataAdapterService daService = DataAdapterServiceUtil.getInstance(new ParameterContributorContext( getJrContext(), null, null)).getService(dataAdapter);
						Map<String, Object> parameters = new HashMap<String, Object>();
						daService.contributeParameters(parameters);
						PentahoCdaConnection connection =  (PentahoCdaConnection) parameters.get(JRParameter.REPORT_CONNECTION);
						TypedTableModel table = connection.fetchData("listQueries",  new HashMap<String, String>());
						rows.clear();
						
						int selectedRow = -1;
						
						for ( int i = 0; i < table.getRowCount(); i++ ) {
							String id = (String)table.getValueAt(i, 0);
							String name = (String)table.getValueAt(i, 1);
							rows.add(new String[]{id, name});
							
							if ( dataAdapter.getDataAccessId() != null ) {
								if ( id.equals(dataAdapter.getDataAccessId())) {
									selectedRow = i;
								}
							}
						}
						final int sRow = selectedRow;
						UIUtils.getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								if ( tableViewer != null) {
									tableViewer.refresh();
									setTableSelection(sRow);
								} 
							}
						});
						
					}catch (Exception e1) {
						UIUtils.showError(e1);
					} finally {
						monitor.done();
						
					}
												
				}
				
				@Override
				protected void abortOperationInvoked() throws InterruptedException {
					if ( httpCall != null) {
						httpCall.abort();
					}
					super.abortOperationInvoked();
				}
				
			});
		}  catch (InvocationTargetException e1) {
			UIUtils.showError(e1.getCause());
		} catch (InterruptedException e1) {
			UIUtils.showError(e1);
		} 
	}
	
	private void setTableSelection(int index) {
		
		if ( index == -1) {
			table.setSelection(index);
			return;
		}

		if (rows != null && rows.size() > 0) {

			if (index == 0) {
				table.setSelection(index);
			} else if ((0 < index) && (index < rows.size() - 1)) {
				table.setSelection(index - 1);
			} else {
				table.setSelection(rows.size() - 1);
			}
		}
	}
}
