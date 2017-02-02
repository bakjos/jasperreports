/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package co.zooloop.jasperreports.studio.data.pentahocda;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ParameterContributorContext;

import org.eclipse.swt.graphics.Image;

import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapter;
import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapterImpl;
import co.zooloop.jasperreports.adapter.PentahoCdaDataAdapterService;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterFactory;
import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;

import co.zooloop.jasperreports.studio.data.pentahocda.messages.Messages;

/**
 * @author gtoffoli
 * 
 */
public class PentahoCdaDataAdapterFactory implements DataAdapterFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#createDataAdapter()
     */
    public DataAdapterDescriptor createDataAdapter() {
        PentahoCdaDataAdapterDescriptor descriptor = new PentahoCdaDataAdapterDescriptor();
        descriptor.getDataAdapter().setBaseUrl("");
        descriptor.getDataAdapter().setUsername(""); //$NON-NLS-1$
        descriptor.getDataAdapter().setPassword(""); //$NON-NLS-1$
        descriptor.getDataAdapter().setSugarMode(false);
        
        return descriptor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.jaspersoft.studio.data.DataAdapterFactory#getDataAdapterClassName()
     */
    public String getDataAdapterClassName() {
        return PentahoCdaDataAdapterImpl.class.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getDescription()
     */
    public String getLabel() {
        return Messages.PentahoCdaDataAdapterFactory_label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getDescription()
     */
    public String getDescription() {
        return Messages.PentahoCdaDataAdapterFactory_description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getIcon(int)
     */
    public Image getIcon(int size) {
        if (size == 16) {
            return Activator.getDefault().getImage(Activator.ICON_NAME);
        }
        return null;
    }

    public DataAdapterService createDataAdapterService(JasperReportsContext jasperReportsContext, DataAdapter dataAdapter) {
        if (dataAdapter instanceof PentahoCdaDataAdapter)
            return new PentahoCdaDataAdapterService(new ParameterContributorContext(jasperReportsContext, null, null), (PentahoCdaDataAdapter) dataAdapter);
        return null;
    }

	@Override
	public IDataAdapterCreator iReportConverter() {
		return new PentahoCdaDBCreator();
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}
}
