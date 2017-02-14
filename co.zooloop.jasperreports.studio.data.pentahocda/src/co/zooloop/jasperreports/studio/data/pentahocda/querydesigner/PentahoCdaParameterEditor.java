package co.zooloop.jasperreports.studio.data.pentahocda.querydesigner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.utils.jobs.CheckedRunnableWithProgress;

import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapter;
import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import co.zooloop.jasperreports.query.PentahoCdaParameter;
import co.zooloop.jasperreports.query.PentahoCdaQueryDefinition;
import co.zooloop.jasperreports.studio.data.pentahocda.ListContentProvider;
import co.zooloop.jasperreports.studio.data.pentahocda.PentahoFileBrowserDialog;
import co.zooloop.jasperreports.studio.data.pentahocda.messages.Messages;
import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.DataAdapterServiceUtil;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignVariable;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

public class PentahoCdaParameterEditor extends Composite {

	TableViewer paramsTableViewer;
	Table paramsTable;
	PentahoCdaConnection connection;
	Gson gson;
	PentahoCdaQueryDefinition queryDefinition = new PentahoCdaQueryDefinition();
	
	private Text solutionField;
	private Text pathField;
	private Text fileField;
	private Button browsePath;
	
	private int nextRowSelection = -1;
	private List<String[]> rows = new ArrayList<String[]>();
	
	protected DataBindingContext bindingContext;
	
	private class ParamsColumnIndexLabelProvider extends ColumnLabelProvider {
		private int columnIndex;

		private ParamsColumnIndexLabelProvider(int columnIndex) {
			this.columnIndex = columnIndex;
		}

		@Override
		public String getText(Object element) {
			PentahoCdaParameter param = (PentahoCdaParameter)element;
			switch(columnIndex) {
			case 0:
				return param.getName();
			case 1:
				if ( param.getValue() != null) {
					return param.getValue().toString();
				}
				break;
			}
			return null;
		}
	}
	
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

	public PentahoCdaParameterEditor(Composite parent, int style) {
		super(parent, style);
		
		bindingContext = new DataBindingContext();
		
		queryDefinition.setParameters(new ArrayList<PentahoCdaParameter>());
		
		Composite group = new Composite(this, SWT.NONE);
		group.setLayoutData( new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout(6, false));
		
		createLabel(group, Messages.PentahoCdaDataAdapterComposite_labelPath, 1);
		pathField = new Text(group, SWT.BORDER);
		pathField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		ModifyListener notifyListener =  new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				bindingContext.updateModels();
				notifyModified();
			}
		};
		
		pathField.addModifyListener(notifyListener);
		
		browsePath = new Button(group, SWT.NONE);
		browsePath.setText("Browse");
		browsePath.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 1));
		
		browsePath.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if ( connection == null ||  StringUtils.isBlank(connection.getBaseUrl())) {
					MessageDialog.open(MessageDialog.ERROR, getShell(), "Error", "The pentaho connection has not been configured properly", SWT.SHEET);
					return;
				}

				try {
					PentahoFileBrowserDialog dialog = new PentahoFileBrowserDialog(Display.getDefault().getActiveShell(), connection);
					String path = "/";
					String fileName = null;
					if ( !StringUtils.isBlank(queryDefinition.getPath())  ) {
						StringTokenizer st = new StringTokenizer(queryDefinition.getPath(), "/");
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
							if ( !dialog.getSelectedPath().equals(queryDefinition.getPath())) {
								pathField.setText(dialog.getSelectedPath());
								bindingContext.updateModels();
								fetchQueries();
								loadParameters();
							}
							
						}
					}
					
					connection.close();
				} catch(Exception ex) {
					UIUtils.showErrorDialog("Error loading the connection", ex);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		createLabel(group,Messages.PentahoCdaDataAdapterComposite_labelSolution, 1);
		solutionField = new Text(group, SWT.BORDER);
		solutionField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		solutionField.addModifyListener(notifyListener);
		
	
		createLabel(group,Messages.PentahoCdaDataAdapterComposite_labelFile, 1);
		fileField = new Text(group, SWT.BORDER);
		fileField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		fileField.addModifyListener(notifyListener);
		
		createLabel(group,Messages.PentahoCdaDataAdapterComposite_labelQuery, 5);
		
		Button fetchButton = new Button(group, SWT.NONE);
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
		
	
		table = new Table(group, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		//GridData gd2 = new GridData(GridData.FILL_BOTH);
		GridData gd2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1);
		gd2.heightHint = 50;
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
						if (  !firstElement[0].equals(queryDefinition.getDataAccessId())  ) {
							queryDefinition.setDataAccessId(firstElement[0]);
							loadParameters();
							notifyModified();
						}
					}
				} else {
					queryDefinition.setDataAccessId(null);
				}
			}
			
		});
		
		createLabel(group, "Parameters");
		
		paramsTable = new Table(group, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		//GridData gd2 = new GridData(GridData.FILL_BOTH);
		gd2 = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1);
		gd2.heightHint = 80;
		paramsTable.setLayoutData(gd2);
		paramsTable.setHeaderVisible(true);
		paramsTable.setLinesVisible(true);

		paramsTableViewer = new TableViewer(paramsTable);
		
		TableViewerColumn tableViewerColumnParameter = new TableViewerColumn(paramsTableViewer, SWT.NONE);
		TableColumn tblclmnColumnParam = tableViewerColumnParameter.getColumn();
		tblclmnColumnParam.setWidth(250);
		tblclmnColumnParam.setText("CDA Parameter");
		tableViewerColumnParameter.setLabelProvider(new ParamsColumnIndexLabelProvider(0));
		//tableViewerColumnName.setEditingSupport(editingSupport);

		TableViewerColumn tableViewerColumnValue = new TableViewerColumn(paramsTableViewer, SWT.NONE);
		TableColumn tblclmnColumnCdaValue = tableViewerColumnValue.getColumn();
		tblclmnColumnCdaValue.setWidth(350);
		tblclmnColumnCdaValue.setText("Value");
		tableViewerColumnValue.setLabelProvider(new ParamsColumnIndexLabelProvider(1));
		tableViewerColumnValue.setEditingSupport(new JRExpressionParameterEditingSupport(paramsTableViewer, this));
		paramsTableViewer.setContentProvider(new ListContentProvider());
		paramsTableViewer.setInput(queryDefinition.getParameters());
		
		GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
        
        bindWidgets();

	}
	
	private void createLabel(Composite parent, String text) {
		createLabel(parent, text, 6);
	}

	private void createLabel(Composite parent, String text, int cells) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, cells, 1));
	}

	public void setText(String text) {
		if ( !StringUtils.isBlank(text )) {
			try {
				
				PentahoCdaQueryDefinition queryDef = gson.fromJson(text, PentahoCdaQueryDefinition.class);
				if ( queryDef != null) {
					queryDefinition.setDataAccessId(queryDef.getDataAccessId());
					if ( fileField != null )  fileField.setText(queryDef.getFile());
					if ( pathField != null )  pathField.setText(queryDef.getPath());
					if ( solutionField != null )  solutionField.setText(queryDef.getSolution());
					
					queryDefinition.setSolution(queryDef.getSolution());
					queryDefinition.getParameters().clear();
					queryDefinition.getParameters().addAll(queryDef.getParameters());
					bindingContext.updateModels();
					if ( paramsTableViewer != null) {
						paramsTableViewer.refresh();
					}
					
					if ( connection != null) {
						fetchQueries();
						loadParameters();
					}
				}
			} catch(Exception ex) {
				
			}
		}
	}

	public String getText() {
		return gson.toJson(queryDefinition);
	}
	
	public String getClassFromType (String type) {
		if ( type.equals("Date")) {
			return java.util.Date.class.getName();
		}
		
		return String.class.getName();
	}
	
	protected void loadParameters() {
		if ( connection != null && !StringUtils.isBlank(queryDefinition.getDataAccessId())) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
			try {
				dialog.run(true, true, new CheckedRunnableWithProgress() {
	
					@Override
					protected void runOperations(IProgressMonitor monitor) {
						try {
							Map<String, String> extraParameters = new HashMap<String, String>();
							
							TypedTableModel modelTable = connection.fetchData(queryDefinition, "listParameters", extraParameters);
							Map<String, PentahoCdaParameter> params = new HashMap<String, PentahoCdaParameter>();
							List<PentahoCdaParameter> rows =  queryDefinition.getParameters();
							for  (PentahoCdaParameter param: rows) {
								params.put(param.getName(), param);
							}
							Set<String> loadedParameters = new HashSet<String>();
							
							for (int i = 0; i < modelTable.getRowCount(); i++) {
								String name = (String) modelTable.getValueAt(i, 0);
								String type = (String) modelTable.getValueAt(i, 1);
								loadedParameters.add(name);
								if (!params.containsKey(name)) {
									PentahoCdaParameter parameter = new PentahoCdaParameter(name, null);
									parameter.setValueClassName(getClassFromType(type));
									rows.add(parameter);
								} else {
									params.get(name).setValueClassName(getClassFromType(type));
								}
							}
							
							for ( Map.Entry<String, PentahoCdaParameter> entry : params.entrySet() ) {
								if ( !loadedParameters.contains(entry.getKey())) {
									rows.remove(entry.getValue());
								}
							}
							
							UIUtils.getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									if ( paramsTableViewer != null) {
										paramsTableViewer.refresh();
									} 
								}
							});
	
						} catch (Exception e1) {
							UIUtils.showError(e1);
						} finally {
							monitor.done();
	
						}
	
					}
	
				});
			} catch (InvocationTargetException e1) {
				UIUtils.showError(e1.getCause());
			} catch (InterruptedException e1) {
				UIUtils.showError(e1);
			}
		}
	}

	public void setConnection(final PentahoCdaConnection connection) {
		this.connection = connection;
		this.solutionField.setEnabled(!connection.isSugarMode());
		this.fileField.setEnabled(!connection.isSugarMode());
		fetchQueries();
		loadParameters();
		
	}
	
	
	protected void bindWidgets() {
		bindingContext.bindValue(SWTObservables.observeText(fileField, SWT.Modify), PojoObservables.observeValue(queryDefinition, "file")); 
		bindingContext.bindValue(SWTObservables.observeText(solutionField, SWT.Modify), PojoObservables.observeValue(queryDefinition, "solution")); 
		bindingContext.bindValue(SWTObservables.observeText(pathField, SWT.Modify), PojoObservables.observeValue(queryDefinition, "path"));
	}
	
	public void notifyModified () {
		Event event = new Event();
		notifyListeners(SWT.Modify, event);
	}
	
	public void addModifyListener(ModifyListener modifyListener) {
		checkWidget();
		if (modifyListener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		addListener(SWT.Modify, new TypedListener(modifyListener));
	}
	
	private void fetchQueries() {
		if ( connection == null ||  StringUtils.isBlank(connection.getBaseUrl())) {
			MessageDialog.open(MessageDialog.ERROR, getShell(), "Error", "The pentaho connection has not been configured properly", SWT.SHEET);
			return;
		}	
		
		if ( StringUtils.isBlank(queryDefinition.getPath()) )  {
			return;
		}
		
		ProgressMonitorDialog dialog= new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, new CheckedRunnableWithProgress() {
				
				GetMethod httpCall;

				@Override
				protected void runOperations(IProgressMonitor monitor) {
					try {
						TypedTableModel table = connection.fetchData(queryDefinition, "listQueries",  new HashMap<String, String>());
						rows.clear();
						
						int selectedRow = -1;
						
						for ( int i = 0; i < table.getRowCount(); i++ ) {
							String id = (String)table.getValueAt(i, 0);
							String name = (String)table.getValueAt(i, 1);
							rows.add(new String[]{id, name});
							
							if ( queryDefinition.getDataAccessId() != null ) {
								if ( id.equals(queryDefinition.getDataAccessId())) {
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
