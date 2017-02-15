package co.zooloop.jasperreports.studio.data.pentahocda.querydesigner;

import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.jaspersoft.studio.editor.expression.ExpressionEditorSupportUtil;
import com.jaspersoft.studio.property.descriptor.expression.JRExpressionCellEditor;

import co.zooloop.jasperreports.query.PentahoCdaParameter;
import net.sf.jasperreports.engine.design.JRDesignExpression;

public class JRExpressionParameterEditingSupport extends EditingSupport 
{
    //private ComboBoxCellEditor cellEditor;
	//private TextCellEditor cellEditor;
	
	private JRExpressionCellEditor cellEditor;
	
	private  ColumnViewer viewer; 
	
	private PentahoCdaParameterEditor editor;
	

    public JRExpressionParameterEditingSupport(ColumnViewer viewer, PentahoCdaParameterEditor editor) {
        super(viewer);
        this.viewer = viewer;
        this.editor = editor;
        //cellEditor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), new String[]{"Y", "N"});
        //cellEditor = new TextCellEditor(((TableViewer)viewer).getTable());
        cellEditor = new JRExpressionCellEditor(((TableViewer)viewer).getTable(), ExpressionEditorSupportUtil
        		.getReportExtendedExpressionContext());
    }
    protected CellEditor getCellEditor(Object element) {
        return cellEditor;
    }
    protected boolean canEdit(Object element) {
        return true;
    }
    protected Object getValue(Object element) {
    	PentahoCdaParameter param = (PentahoCdaParameter)element;
    	Object value =  param.getValue();
    	JRDesignExpression expression = new JRDesignExpression();    	
    	
    	if ( value != null) {
    		expression.setText(value.toString());
    	}
    	
    	return expression;
    }
    protected void setValue(Object element, Object value) 
    {
    	PentahoCdaParameter param = (PentahoCdaParameter)element;
    	if ( value instanceof JRDesignExpression) {
    		JRDesignExpression expression = (JRDesignExpression)value;
    		param.setValue(expression.getText());
    	} else {
    		param.setValue(value.toString());
    	}
    	viewer.update(element, null);
    	editor.notifyModified();
    }
}