package co.zooloop.jasperreports;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.platform.util.UUIDUtil;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class JrXmlContentGenerator extends JasperContentGenerator {
	

	private static final long serialVersionUID = 1L;

	@Override
	public String getPluginName() {
		return "jrxml";
	}

	@Override
	public void createContent() throws Exception {
		HttpServletResponse response = this.getResponse();
		try {
			final String id = UUIDUtil.getUUIDAsString();
			setInstanceId(id);
			InputStream is = getInputStream();
			HttpServletRequest request = this.getRequest();
			JasperDesign jasDesign = JRXmlLoader.load(is);
			JasperReport jasReport = JasperCompileManager.compileReport(jasDesign);
			renderReport(jasReport, request, response);
		} catch(Exception ex) {
			String message = ex.getMessage();
			Throwable t = ex.getCause();
			while (t != null) {
				message = t.getMessage();
				t = t.getCause();
			}
			response.sendError(409, "Error loading report " + message);
		}
		
	}
	

}
