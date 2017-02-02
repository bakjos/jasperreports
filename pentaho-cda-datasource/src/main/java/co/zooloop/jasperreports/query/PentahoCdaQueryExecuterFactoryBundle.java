package co.zooloop.jasperreports.query;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRSingletonCache;

public class PentahoCdaQueryExecuterFactoryBundle implements JRQueryExecuterFactoryBundle {
    private static final JRSingletonCache<QueryExecuterFactory> cache = new JRSingletonCache(QueryExecuterFactory.class);
    private static final PentahoCdaQueryExecuterFactoryBundle instance = new PentahoCdaQueryExecuterFactoryBundle();
    private static final String[] languages = new String[]{"PentahoCdaQuery"};

    private PentahoCdaQueryExecuterFactoryBundle() {
    }

    public static PentahoCdaQueryExecuterFactoryBundle getInstance() {
        return instance;
    }

    public String[] getLanguages() {
        return languages;
    }

    public QueryExecuterFactory getQueryExecuterFactory(String language) throws JRException {
        return "PentahoCdaQuery".equals(language)?(QueryExecuterFactory)cache.getCachedInstance(PentahoCdaQueryExecuterFactory.class.getName()):null;
    }
}
