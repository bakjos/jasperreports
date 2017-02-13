package co.zooloop.jasperreports;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IFileInfo;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.SolutionFileMetaAdapter;
import org.pentaho.platform.engine.core.solution.FileInfo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class JasperDefinitionFileMetaProvider extends SolutionFileMetaAdapter {

	private static Log logger = LogFactory.getLog(JasperDefinitionFileMetaProvider.class);

	@Override
	public IFileInfo getFileInfo(ISolutionFile solutionFile, InputStream in) {
		try {
			JasperDesign jasDesign = JRXmlLoader.load(in);
			IFileInfo info = new FileInfo();
			info.setDescription(jasDesign.getProperty(jasDesign.PROPERTY_SUMMARY));
			info.setDisplayType("data");
			info.setTitle(jasDesign.getName());

			return info;

		} catch (JRException e) {
			if (logger != null) {
				logger.error(getClass().toString(), e);
			}
		}
		return null;
	}

}
