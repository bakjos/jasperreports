package co.zooloop.jasperreports;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IFileInfo;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.SolutionFileMetaAdapter;
import org.pentaho.platform.engine.core.solution.FileInfo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class JasperCompiledFileMetaProvider   extends SolutionFileMetaAdapter {

	private static Log logger = LogFactory.getLog(JasperCompiledFileMetaProvider.class);
	
	@Override
	public IFileInfo getFileInfo(ISolutionFile solutionFile, InputStream in) {
		try {
			JasperReport jaspeReport = (JasperReport) JRLoader.loadObject(in);
			IFileInfo info = new FileInfo();
			info.setDisplayType("data");
			info.setTitle(jaspeReport.getName());

			return info;
		} catch (JRException e) {
			if (logger != null) {
				logger.error(getClass().toString(), e);
			}
		}
		return null;
	}

}
