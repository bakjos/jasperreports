package co.zooloop.jasperreports;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JsonExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleJsonExporterOutput;
import net.sf.jasperreports.web.WebReportContext;
import net.sf.jasperreports.web.servlets.AbstractServlet;
import net.sf.jasperreports.web.servlets.JasperPrintAccessor;
import net.sf.jasperreports.web.servlets.ReportExecutionStatus;
import net.sf.jasperreports.web.servlets.ReportPageStatus;
import net.sf.jasperreports.web.servlets.SimpleJasperPrintAccessor;
import net.sf.jasperreports.web.util.JacksonUtil;
import net.sf.jasperreports.web.util.ReportExecutionHyperlinkProducerFactory;
import net.sf.jasperreports.web.util.VelocityUtil;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import net.sf.jasperreports.web.util.WebUtil;
import pt.webdetails.cpf.SimpleContentGenerator;

public class JasperContentGenerator extends SimpleContentGenerator {
	
	
	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE_HEADER= "net/sf/jasperreports/web/servlets/resources/templates/HeaderTemplate.vm";
	private static final String TEMPLATE_BETWEEN_PAGES= "net/sf/jasperreports/web/servlets/resources/templates/BetweenPagesTemplate.vm";
	private static final String TEMPLATE_FOOTER= "net/sf/jasperreports/web/servlets/resources/templates/FooterTemplate.vm";
	
	private static final String TEMPLATE_HEADER_NOPAGES = "net/sf/jasperreports/web/servlets/resources/templates/HeaderTemplateNoPages.vm";
	private static final String TEMPLATE_FOOTER_NOPAGES = "net/sf/jasperreports/web/servlets/resources/templates/FooterTemplateNoPages.vm";
	
	protected static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";

	@Override
	public String getPluginName() {
		return "jasper";
	}
	
	
	
	protected InputStream getInputStream() throws UnsupportedEncodingException {
		
		String path = null;
		IUnifiedRepository unifiedRepository = PentahoSystem.get(IUnifiedRepository.class, null);
		final IParameterProvider requestParams = getRequestParameters();
		final IParameterProvider pathParams = getPathParameters();
		if (requestParams != null && requestParams.getStringParameter("path", null) != null) {
			path = requestParams.getStringParameter("path", ""); //$NON-NLS-1$ //$NON-NLS-2$
		} else if (pathParams != null && pathParams.getStringParameter("path", null) != null) {
			path = pathParams.getStringParameter("path", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		path = URLDecoder.decode(path, "UTF-8");

		RepositoryFile jasperFile = unifiedRepository.getFile(path);
		return getInputStream(unifiedRepository, jasperFile);
	}

	@Override
	public void createContent() throws Exception {
		HttpServletResponse response = this.getResponse();
		try {
			final String id = UUIDUtil.getUUIDAsString();
			setInstanceId(id);
			InputStream is = getInputStream();
			HttpServletRequest request = this.getRequest();
			JasperReport jasReport = (JasperReport) JRLoader.loadObject(is);
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

	protected InputStream getInputStream(IUnifiedRepository repo, RepositoryFile file) throws UnsupportedEncodingException {
		
		try {
			SimpleRepositoryFileData data = repo.getDataForRead(file.getId(), SimpleRepositoryFileData.class);
			return data.getInputStream();
		} catch ( UnifiedRepositoryException ure ) {
            NodeRepositoryFileData data = repo.getDataForRead( file.getId(), NodeRepositoryFileData.class );
            return new ByteArrayInputStream( data.getNode().toString().getBytes( "UTF8" ) );
		}
	}
	
	protected void renderReport(JasperReport jasReport, HttpServletRequest request,HttpServletResponse response ) throws Exception {
		
		final IParameterProvider requestParams = getRequestParameters();
		
		PentahoCdaConnection pentahoCdaConnection = new PentahoCdaConnection(null, null, true, null);
		Map<String, Object> parameters = new HashMap<String, Object>();
		
	
		Iterator<String> namesIt =  requestParams.getParameterNames();
		while (namesIt.hasNext()) {
			String name = namesIt.next();
			parameters.put(name, requestParams.getParameter(name));
		}
		
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasReport, parameters, pentahoCdaConnection);
        HtmlExporter exporter = new HtmlExporter();
        
        response.setContentType(HTML_CONTENT_TYPE);
        
        WebReportContext webReportContext = WebReportContext.getInstance(request, true);
		
		/*exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(response.getOutputStream()));
		exporter.exportReport();*/
        
        PrintWriter writer = response.getWriter();
        
        JasperPrintAccessor jasperPrintAccessor = new SimpleJasperPrintAccessor(jasperPrint);
        ReportExecutionStatus reportStatus = jasperPrintAccessor.getReportStatus();
        
    	boolean hasPages = jasperPrintAccessor.pageStatus(0, null).pageExists();
		
//		JRXhtmlExporter exporter = new JRXhtmlExporter(getJasperReportsContext());
		//HtmlExporter exporter = new HtmlExporter(getJasperReportsContext());

		SimpleHtmlExporterConfiguration exporterConfig = new SimpleHtmlExporterConfiguration();
		SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();

		ReportPageStatus pageStatus;
		if (hasPages)
		{
			String reportPage = request.getParameter(WebUtil.REQUEST_PARAMETER_PAGE);
			int pageIdx = reportPage == null ? 0 : Integer.parseInt(reportPage);
			String pageTimestamp = request.getParameter(WebUtil.REQUEST_PARAMETER_PAGE_TIMESTAMP);
			Long timestamp = pageTimestamp == null ? null : Long.valueOf(pageTimestamp);
			
			pageStatus = jasperPrintAccessor.pageStatus(pageIdx, timestamp);
			
			if (!pageStatus.pageExists())
			{
				throw 
					new JRRuntimeException(AbstractServlet.EXCEPTION_MESSAGE_KEY_PAGE_NOT_FOUND, new Object[]{pageIdx});
			}
			
			reportConfig.setPageIndex(pageIdx);
		}
		else
		{
			pageStatus = ReportPageStatus.PAGE_FINAL;
		}
		
		// set report status on response header
		LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
		WebUtil webUtil = WebUtil.getInstance(DefaultJasperReportsContext.getInstance());
		boolean isComponentMetadataEmbedded = webUtil.isComponentMetadataEmbedded();
		result.put("reportStatus", reportStatus.getStatus().toString().toLowerCase());
		result.put("totalPages", reportStatus.getTotalPageCount());
		result.put("partialPageCount", reportStatus.getCurrentPageCount());
		result.put("pageFinal", pageStatus.isPageFinal());
		result.put("isComponentMetadataEmbedded", isComponentMetadataEmbedded);
		if (!pageStatus.isPageFinal()) {
			result.put("pageTimestamp", String.valueOf(pageStatus.getTimestamp()));
		}
		response.setHeader("jasperreports-report-status", JacksonUtil.getInstance(DefaultJasperReportsContext.getInstance()).getJsonString(result));
		
		exporter.setReportContext(webReportContext);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrintAccessor.getJasperPrint()));

		SimpleHtmlExporterOutput htmlOutput = new SimpleHtmlExporterOutput(writer);

		String applicationDomain = (String)webReportContext.getParameterValue(WebReportContext.REQUEST_PARAMETER_APPLICATION_DOMAIN);
		if (applicationDomain == null) {
			applicationDomain = request.getContextPath();
		}

		String resourcesPath = applicationDomain + webUtil.getResourcesPath() + "?" + WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID + "=" + webReportContext.getId();
		htmlOutput.setImageHandler(new WebHtmlResourceHandler(resourcesPath + "&image={0}"));
		htmlOutput.setResourceHandler(new WebHtmlResourceHandler(resourcesPath + "/{0}"));
		exporter.setExporterOutput(htmlOutput);

		exporterConfig.setHtmlHeader(getHeader(request,hasPages, pageStatus));
		exporterConfig.setBetweenPagesHtml(getBetweenPages(request));
		exporterConfig.setHtmlFooter(getFooter(request,  hasPages, pageStatus, isComponentMetadataEmbedded));

		reportConfig.setHyperlinkProducerFactory(
			ReportExecutionHyperlinkProducerFactory.getInstance(DefaultJasperReportsContext.getInstance(), request)
			);
		
		exporter.setConfiguration(exporterConfig);
		exporter.setConfiguration(reportConfig);
		
		exporter.exportReport();

		// embed component JSON metadata into report's HTML output
		if (isComponentMetadataEmbedded) {
			JsonExporter jsonExporter = new JsonExporter(DefaultJasperReportsContext.getInstance());
			StringWriter sw = new StringWriter();

			jsonExporter.setReportContext(webReportContext);
			jsonExporter.setExporterInput(new SimpleExporterInput(jasperPrintAccessor.getJasperPrint()));
			SimpleJsonExporterOutput jsonOutput = new SimpleJsonExporterOutput(sw);
			jsonOutput.setFontHandler(new WebHtmlResourceHandler(resourcesPath + "&font={0}"));
			jsonExporter.setExporterOutput(jsonOutput);
			jsonExporter.exportReport();

			String serializedJson = sw.getBuffer().toString();
			writer.write("<span id=\"reportComponents\" style=\"display:none\">" + serializedJson.replaceAll("\\s","") + "</span></div>");
		}
	       	
	}
	
	protected String getHeader(HttpServletRequest request, boolean hasPages, 
			ReportPageStatus pageStatus)
	{
		Map<String, Object> contextMap = new HashMap<String, Object>();
		if (hasPages) 
		{
			return VelocityUtil.processTemplate(TEMPLATE_HEADER, contextMap);
		} else 
		{
			return VelocityUtil.processTemplate(TEMPLATE_HEADER_NOPAGES, contextMap);
		}
	}
	
	protected String getBetweenPages(HttpServletRequest request) 
	{
		return VelocityUtil.processTemplate(TEMPLATE_BETWEEN_PAGES, new HashMap<String, Object>());
	}
	
	/**
	 * 
	 */
	protected String getFooter(HttpServletRequest request, boolean hasPages, 
			ReportPageStatus pageStatus, boolean isComponentMetadataEmbedded)
	{
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put("isComponentMetadataEmbedded", isComponentMetadataEmbedded);
		if (hasPages) {
			return VelocityUtil.processTemplate(TEMPLATE_FOOTER, contextMap);
		} else 
		{
			return VelocityUtil.processTemplate(TEMPLATE_FOOTER_NOPAGES, contextMap);
		}
	}
}