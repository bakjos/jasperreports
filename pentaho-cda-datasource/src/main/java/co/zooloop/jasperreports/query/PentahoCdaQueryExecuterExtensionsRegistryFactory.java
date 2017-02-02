package co.zooloop.jasperreports.query;

import java.util.Collections;
import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;

public class PentahoCdaQueryExecuterExtensionsRegistryFactory implements ExtensionsRegistryFactory  {
	
	ExtensionsRegistry defaultExtensionsRegistry = new ExtensionsRegistry() {
		
		@Override
		public <T> List<T> getExtensions(Class<T> extensionType) {
			  return (List<T>) (JRQueryExecuterFactoryBundle.class.equals(extensionType)?Collections.singletonList(PentahoCdaQueryExecuterFactoryBundle.getInstance()):null);
		}
	};

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
		return defaultExtensionsRegistry;
	}

	

}
