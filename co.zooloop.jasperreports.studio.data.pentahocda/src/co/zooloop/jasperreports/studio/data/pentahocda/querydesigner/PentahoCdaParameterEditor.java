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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TypedListener;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.utils.jobs.CheckedRunnableWithProgress;

import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import co.zooloop.jasperreports.query.PentahoCdaParameter;
import co.zooloop.jasperreports.studio.data.pentahocda.ListContentProvider;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignVariable;

import org.eclipse.swt.widgets.Event;

public class PentahoCdaParameterEditor extends Composite {

	TableViewer tableViewer;
	Table table;
	PentahoCdaConnection connection;
	
	Gson gson;

	
	
	List<PentahoCdaParameter> rows = new ArrayList<PentahoCdaParameter>();
	
	private class ColumnIndexLabelProvider extends ColumnLabelProvider {
		private int columnIndex;

		private ColumnIndexLabelProvider(int columnIndex) {
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

	public PentahoCdaParameterEditor(Composite parent, int style) {
		super(parent, style);

		table = new Table(this, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd2 = new GridData(GridData.FILL_BOTH);
		//gd2.heightHint = 100;
		table.setLayoutData(gd2);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		

		tableViewer = new TableViewer(table);
		
		TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnName = tableViewerColumnName.getColumn();
		tblclmnColumnName.setWidth(250);
		tblclmnColumnName.setText("CDA Parameter");
		tableViewerColumnName.setLabelProvider(new ColumnIndexLabelProvider(0));
		//tableViewerColumnName.setEditingSupport(editingSupport);

		TableViewerColumn tableViewerColumnCdaId = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnCdaId = tableViewerColumnCdaId.getColumn();
		tblclmnColumnCdaId.setWidth(350);
		tblclmnColumnCdaId.setText("Value");
		tableViewerColumnCdaId.setLabelProvider(new ColumnIndexLabelProvider(1));
		tableViewerColumnCdaId.setEditingSupport(new JRExpressionParameterEditingSupport(tableViewer, this));
		tableViewer.setContentProvider(new ListContentProvider());
		tableViewer.setInput(rows);
		
		GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

	}

	public void setText(String text) {
		if ( !StringUtils.isBlank(text )) {
			try {
				List<PentahoCdaParameter> _rows =  gson.fromJson(text, new TypeToken<ArrayList<PentahoCdaParameter>>() {}.getType());
				if ( _rows != null) {
					rows.clear();
					rows.addAll(_rows);
					if ( tableViewer != null) {
						tableViewer.refresh();
					}
				}
			} catch(Exception ex) {
				
			}
		}
	}

	public String getText() {
		return gson.toJson(rows);
	}
	
	public String getClassFromType (String type) {
		if ( type.equals("Date")) {
			return java.util.Date.class.getName();
		}
		
		return String.class.getName();
	}

	public void setConnection(final PentahoCdaConnection connection) {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, new CheckedRunnableWithProgress() {

				@Override
				protected void runOperations(IProgressMonitor monitor) {
					try {
						Map<String, String> extraParameters = new HashMap<String, String>();
						extraParameters.put("dataAccessId", connection.getDataAccessId());

						TypedTableModel table = connection.fetchData("listParameters", extraParameters);
						Map<String, PentahoCdaParameter> params = new HashMap<String, PentahoCdaParameter>();
						for  (PentahoCdaParameter param: rows) {
							params.put(param.getName(), param);
						}

						for (int i = 0; i < table.getRowCount(); i++) {
							String name = (String) table.getValueAt(i, 0);
							String type = (String) table.getValueAt(i, 1);
							if (!params.containsKey(name)) {
								PentahoCdaParameter parameter = new PentahoCdaParameter(name, null);
								parameter.setValueClassName(getClassFromType(type));
								rows.add(parameter);
							} else {
								params.get(name).setValueClassName(getClassFromType(type));
							}
						}
						
						UIUtils.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								if ( tableViewer != null) {
									tableViewer.refresh();
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
	
	public void notifyModified () {
		Event event = new Event();
		notifyListeners(SWT.Modify, event);
	}
	
	public void addModifyListener(ModifyListener modifyListener) {
		checkWidget();
		if (modifyListener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		addListener(SWT.Modify, new TypedListener(modifyListener));
	}
	
	

}
