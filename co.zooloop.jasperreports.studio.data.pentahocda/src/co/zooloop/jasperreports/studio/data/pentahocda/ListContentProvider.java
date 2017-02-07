package co.zooloop.jasperreports.studio.data.pentahocda;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public class ListContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement != null && inputElement instanceof List)
			return ((List<?>) inputElement).toArray();
		return new Object[0];
	}
	
}
