package co.zooloop.jasperreports.studio.data.pentahocda;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;


import com.jaspersoft.studio.data.messages.Messages;

public class PentahoFileBrowserDialog extends TrayDialog {
	
	private DataBindingContext bindingContext;
	private Text fileName;
	protected ComboViewer comboLocation;
	private TableViewer tableViewer;
	
	private Table table;
	
	protected List<String> locations;
	
	public PentahoFileBrowserDialog(Shell parentShell) {
		super(parentShell);
		locations = new ArrayList<String>();
		locations.add("/");
		locations.add("/home");
		locations.add("/home/admin");
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
		gd.widthHint = 600;
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

		/*tableViewer.setContentProvider(new XLSContentProvider());
		tableViewer.setInput(rows);*/

		
		TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnName = tableViewerColumnName.getColumn();
		tblclmnColumnName.setWidth(140);
		tblclmnColumnName.setText("Title");
		

		TableViewerColumn tableViewerColumnIndex = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnIndex = tableViewerColumnIndex.getColumn();
		tblclmnColumnIndex.setWidth(140);
		tblclmnColumnIndex.setText("FileName");
		
		
		TableViewerColumn tableViewerColumnDate = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnDate = tableViewerColumnDate.getColumn();
		tblclmnColumnDate.setWidth(140);
		tblclmnColumnDate.setText("Date");
		
		TableViewerColumn tableViewerColumnDesc = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnDesc = tableViewerColumnDesc.getColumn();
		tblclmnColumnDesc.setWidth(140);
		tblclmnColumnDesc.setText("Description");
			
		
		
		
		bindingContext.bindList(SWTObservables.observeItems(combo), PojoObservables.observeList(this, "locations"));
		
		
		
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
		super.okPressed();
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}
	
	
	
	
}
