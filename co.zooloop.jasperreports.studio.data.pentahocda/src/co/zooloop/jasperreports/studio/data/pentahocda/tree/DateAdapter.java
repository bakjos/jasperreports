package co.zooloop.jasperreports.studio.data.pentahocda.tree;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateAdapter extends XmlAdapter<String, Date> {
    private static final String DF = "MM/dd/yyyy HH:mm:ss";
    private static final Log log = LogFactory.getLog(DateAdapter.class);

    public DateAdapter() {
    }

    public Date unmarshal(String v) throws Exception {
        return new Date(Long.valueOf(v).longValue());
    }

    public String marshal(Date v) throws Exception {
        return String.valueOf(v.getTime());
    }
}
