package co.zooloop.jasperreports.query;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRCompilationUnit;
import net.sf.jasperreports.engine.design.JRJdtCompiler;
import net.sf.jasperreports.engine.fill.JREvaluator;

public class PentahoCdaQueryCompiler  extends JRJdtCompiler {

	public PentahoCdaQueryCompiler(JasperReportsContext jasperReportsContext) {
		super(jasperReportsContext);
	}
	
	public String compileUnits(JRCompilationUnit[] units, String classpath ) throws JRException {
		return compileUnits(units, classpath, null);
	}
	
	public JREvaluator loadEvaluatorFinal(Serializable compileData, String className) throws JRException {
		return super.loadEvaluator(compileData, className);
	}

}
