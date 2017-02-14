package co.zooloop.jasperreports;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.UnifiedRepositoryException;
import org.pentaho.platform.api.repository2.unified.data.node.NodeRepositoryFileData;
import org.pentaho.platform.api.repository2.unified.data.simple.SimpleRepositoryFileData;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.UUIDUtil;

import co.zooloop.jasperreports.connection.PentahoCdaConnection;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import pt.webdetails.cpf.SimpleContentGenerator;

public class JasperContentGenerator extends SimpleContentGenerator {

	@Override
	public String getPluginName() {
		return "jasper";
	}

	@Override
	public void createContent() throws Exception {
		final String id = UUIDUtil.getUUIDAsString();
		String path = null;

		setInstanceId(id);
		IUnifiedRepository unifiedRepository = PentahoSystem.get(IUnifiedRepository.class, null);
		final IParameterProvider requestParams = getRequestParameters();
		final IParameterProvider pathParams = getPathParameters();
		if (requestParams != null && requestParams.getStringParameter("path", null) != null) {
			path = requestParams.getStringParameter("path", ""); //$NON-NLS-1$ //$NON-NLS-2$
		} else if (pathParams != null && pathParams.getStringParameter("path", null) != null) {
			path = pathParams.getStringParameter("path", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		path = URLDecoder.decode(path, "UTF-8");

		RepositoryFile prptFile = unifiedRepository.getFile(path);
		InputStream is = getInputStream(unifiedRepository, prptFile);
		HttpServletRequest request = this.getRequest();
		HttpServletResponse response = this.getResponse();
		
		
		JasperReport jasReport = (JasperReport) JRLoader.loadObject(is);
		Map<String, Object> parameters = new HashMap<String, Object>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasReport, parameters, new PentahoCdaConnection(null, null, true, null));
        
        HtmlExporter exporter = new HtmlExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(response.getOutputStream()));
		exporter.exportReport();
	}

	protected InputStream getInputStream(IUnifiedRepository repo, RepositoryFile file) throws UnsupportedEncodingException {
		
		try {
			SimpleRepositoryFileData data = repo.getDataForRead(file.getId(), SimpleRepositoryFileData.class);
			return data.getInputStream();
		} catch ( UnifiedRepositoryException ure ) {
            NodeRepositoryFileData data = repo.getDataForRead( file.getId(), NodeRepositoryFileData.class );
            String path = file.getPath();
            return new ByteArrayInputStream( data.getNode().toString().getBytes( "UTF8" ) );
		}
	}
}