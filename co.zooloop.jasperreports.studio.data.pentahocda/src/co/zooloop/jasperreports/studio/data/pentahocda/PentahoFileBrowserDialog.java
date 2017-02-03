package co.zooloop.jasperreports.studio.data.pentahocda;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.ibm.icu.util.StringTokenizer;
import com.jaspersoft.studio.utils.jobs.CheckedRunnableWithProgress;

import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import co.zooloop.jasperreports.studio.data.pentahocda.tree.RepositoryFileDto;
import co.zooloop.jasperreports.studio.data.pentahocda.tree.RepositoryFileTreeDto;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;

public class PentahoFileBrowserDialog extends TrayDialog {
	
	private DataBindingContext bindingContext;
	private Text fileName;
	protected ComboViewer comboLocation;
	private TableViewer tableViewer;
	private Table table;
	private TableViewerColumn tableViewerColumnTitle;
	private TableViewerColumn tableViewerColumnName;
	private TableViewerColumn tableViewerColumnDate;
	private TableViewerColumn tableViewerColumnDesc;
	
	private String currentPath;
	private String tmpFileName;
	
	protected List<String> locations;
	
	PentahoCdaConnection connection;
	
	private boolean loadPathSuccess;
	
	// The data model
	private java.util.List<RepositoryFileTreeDto> rows;
	
	private String selectedPath;
	
	private int rowSel = -1;
	
	
	private class ListContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement != null && inputElement instanceof List)
				return ((List<?>) inputElement).toArray();
			return new Object[0];
		}
		
	}
	
	private class ColumnIndexLabelProvider extends ColumnLabelProvider {
		private int columnIndex;

		private ColumnIndexLabelProvider(int columnIndex) {
			this.columnIndex = columnIndex;
		}

		@Override
		public String getText(Object element) {
			RepositoryFileTreeDto row = (RepositoryFileTreeDto) element;
			
			switch (columnIndex) {
			case 0:
				return row.getFile().getTitle();
			case 1:
				return row.getFile().getName();
				
			case 2:
				Date date = row.getFile().getLastModifiedDate();
				if ( date != null) {
					return date.toString();
				}
			case 3:
				return row.getFile().getDescription();
			}
			
			return null;
			
		}
		
		@Override
		public Image getImage(Object element) {
			if ( columnIndex == 0) {
				 RepositoryFileTreeDto row = (RepositoryFileTreeDto) element;
				 
				 if ( row.getFile().isFolder()) {
					 return Activator.getDefault().getImage(Activator.FOLDER_ICON_NAME);
				 } else {
					 return Activator.getDefault().getImage(Activator.CDA_ICON_NAME);
				 }
			}
			return null;
		}
		
		
	}
	
	public PentahoFileBrowserDialog(Shell parentShell, PentahoCdaConnection connection) {
		super(parentShell);
		this.connection = connection;
		rows = new ArrayList<RepositoryFileTreeDto>();
		locations = new ArrayList<String>(); 
	
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		bindingContext = new DataBindingContext();
		/*if (title != null) {
			shell.setText(title);
		}*/
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		Composite group = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(6, false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 800;
		group.setLayout(layout);
		group.setLayoutData(gd);
		
		createLabel(group, "File Name");
		fileName = new Text(group, SWT.BORDER);
		fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		createLabel(group, "Location");
		comboLocation = new ComboViewer(group, SWT.NONE);
		Combo  combo = comboLocation.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		table = new Table(group, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		//GridData gd2 = new GridData(GridData.FILL_BOTH);
		GridData gd2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1);
		gd2.heightHint = 400;
		table.setLayoutData(gd2);
		table.setHeaderVisible(true);
		
		tableViewer = new TableViewer(table);

			
		tableViewerColumnTitle = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnTitle = tableViewerColumnTitle.getColumn();
		tblclmnColumnTitle.setWidth(192);
		tblclmnColumnTitle.setText("Title");
		tableViewerColumnTitle.setLabelProvider(new ColumnIndexLabelProvider(0));
		
		

		tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnName = tableViewerColumnName.getColumn();
		tblclmnColumnName.setWidth(192);
		tblclmnColumnName.setText("Name");
		
		tableViewerColumnName.setLabelProvider(new ColumnIndexLabelProvider(1));
		
		
		tableViewerColumnDate = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnDate = tableViewerColumnDate.getColumn();
		tblclmnColumnDate.setWidth(192);
		tblclmnColumnDate.setText("Date");
		
		tableViewerColumnDate.setLabelProvider(new ColumnIndexLabelProvider(2));
		
		tableViewerColumnDesc = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnDesc = tableViewerColumnDesc.getColumn();
		tblclmnColumnDesc.setWidth(192);
		tblclmnColumnDesc.setText("Description");
		
		tableViewerColumnDesc.setLabelProvider(new ColumnIndexLabelProvider(3));
		
			
		//bindingContext.bindList(SWTObservables.observeItems(combo), PojoObservables.observeList(this, "locations"));
		
		
		
		tableViewer.setContentProvider(new ListContentProvider());
		tableViewer.setInput(rows);
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				 IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				 RepositoryFileTreeDto firstElement = (RepositoryFileTreeDto) selection.getFirstElement();
				 if ( firstElement.getFile().isFolder()  ) {
					 loadPath(firstElement.getFile().getPath(), null);
				 }
				
			}
		});
		
		
		comboLocation.setContentProvider(new ListContentProvider());
		
		comboLocation.setInput(locations);
		
		if (!locations.isEmpty() && !StringUtils.isBlank(currentPath) ) {
			int pos = locations.indexOf(currentPath);
			combo.select(pos);
		}
		
		tableViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if ( selection != null ) {
					RepositoryFileTreeDto firstElement = (RepositoryFileTreeDto) selection.getFirstElement();
					if ( firstElement != null) {
						if ( !firstElement.getFile().isFolder()  ) {
							fileName.setText( firstElement.getFile().getName() );
							selectedPath = firstElement.getFile().getPath();
						}
					}
				}
			}
		});
		
		comboLocation.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if ( selection != null ) {
					String path = (String)selection.getFirstElement();
					if ( path != null && !currentPath.equals( path )) {
						loadPath(path, null);
					}
				}
				
			}
		});
		
		if (rowSel >= 0) {
			setTableSelection(rowSel);
			if ( tmpFileName != null  ) {
				fileName.setText( tmpFileName );
				selectedPath = currentPath + "/" + tmpFileName;
				tmpFileName = null;
			}
			
		}
		
		
		Dialog.applyDialogFont(container);
		
		
		
		return container;
	}
	
	
	private void createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 6, 1));
	}
	
	@Override
	protected void okPressed() {
		
		if ( !StringUtils.isBlank(selectedPath) ) {
			super.okPressed();	
		} else {
			MessageDialog.open(MessageDialog.ERROR, getShell(), "Error", "There's no selected path", SWT.SHEET);
		}
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}
	
	
	public boolean loadPath(String path, final String fileName) {
		if ( path == null){
			path = "";
		}
		currentPath = path; 
		path = path.replaceAll("/", ":");
		if ( ":".equals(path)) {
			path = "";
		} else {
			path += "/";
		}
		
		locations.clear();
		
		
		
		final String _path = path;
		loadPathSuccess = false;
		ProgressMonitorDialog dialog= new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, new CheckedRunnableWithProgress() {
				
				
				private GetMethod httpCall = null;

				@Override
				protected void runOperations(IProgressMonitor monitor) {
					
					monitor.beginTask("Listing files from server", SWT.INDETERMINATE);
					
					
					try {
						 
						HttpClient client =  connection.getHttpClient();
						final StringBuilder url = new StringBuilder();
						url.append(connection.getBaseUrl());
						url.append("/api/repo/files/" + _path +"tree?depth=1&filter=*&showHidden=false");
						httpCall = new GetMethod(url.toString());
						int status = client.executeMethod(httpCall);
						
						if ( status == 200) {
							
							RepositoryFileTreeDto fileTree =  JAXB.unmarshal(httpCall.getResponseBodyAsStream(), RepositoryFileTreeDto.class);
							rows.clear();
							for ( RepositoryFileTreeDto tree:  fileTree.getChildren()) {
								RepositoryFileDto file = tree.getFile();
								if ( file.isFolder() || file.getName().toLowerCase().endsWith(".cda")) {
									rows.add(tree);
								}
							}
							
							StringTokenizer stringTokenizer = new StringTokenizer(currentPath, "/");
							String current = "/";
							
							while(stringTokenizer.hasMoreTokens())
							{	
								locations.add("" + current);
								current += stringTokenizer.nextToken();
								if ( stringTokenizer.hasMoreTokens() ) {
									current += "/";
								}
							}
							
							
							locations.add(current);
							
							
						
							loadPathSuccess = true;
							final int pos = locations.indexOf(currentPath);
							
							int idx = -1;
							if  ( fileName != null) {
								for ( int i = 0; i < rows.size(); i++ ) {
									RepositoryFileTreeDto tree = rows.get(i);
									RepositoryFileDto file = tree.getFile();
									if ( file.getName().equalsIgnoreCase(fileName)) {
										tmpFileName = file.getName();
										idx = i;
										break;
									}
								}
							}
							
							rowSel = idx;
							UIUtils.getDisplay().asyncExec(new Runnable() {

								@Override
								public void run() {
									if ( tableViewer != null) {
										tableViewer.refresh();
										setTableSelection(rowSel);
									} 
									
									if ( comboLocation != null ) {
										comboLocation.refresh();
										comboLocation.getCombo().select(pos);
									}
								}
							});
						
							
						} else {
							throw new HttpException(httpCall.getStatusLine().toString());
						}
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
			
		} catch (InvocationTargetException e1) {
			UIUtils.showError(e1.getCause());
		} catch (InterruptedException e1) {
			UIUtils.showError(e1);
		} 
		return loadPathSuccess;
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

	public String getSelectedPath() {
		return selectedPath;
	}	
	
	
}
