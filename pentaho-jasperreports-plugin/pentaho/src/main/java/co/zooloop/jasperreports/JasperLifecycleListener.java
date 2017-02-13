package co.zooloop.jasperreports;

import org.pentaho.platform.api.engine.IPlatformReadyListener;

import pt.webdetails.cpf.PentahoPluginEnvironment;
import pt.webdetails.cpf.PluginEnvironment;
import pt.webdetails.cpf.SimpleLifeCycleListener;

public class JasperLifecycleListener extends SimpleLifeCycleListener implements IPlatformReadyListener {

	@Override
	public PluginEnvironment getEnvironment() {
		 return PentahoPluginEnvironment.getInstance();
	}


}
