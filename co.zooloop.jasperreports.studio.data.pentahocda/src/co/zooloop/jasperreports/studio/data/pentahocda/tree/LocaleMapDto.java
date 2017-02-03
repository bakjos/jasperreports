package co.zooloop.jasperreports.studio.data.pentahocda.tree;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocaleMapDto implements Serializable {
    
    private String locale;
    private List<StringKeyStringValueDto> properties;

    public LocaleMapDto() {
    }

    public LocaleMapDto(String locale, List<StringKeyStringValueDto> properties) {
        this.locale = locale;
        this.properties = properties;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<StringKeyStringValueDto> getProperties() {
        return this.properties;
    }

    public void setProperties(List<StringKeyStringValueDto> properties) {
        this.properties = properties;
    }

    public String toString() {
        return "LocaleMapDto [locale=" + this.locale + ", properties=" + this.properties + "]";
    }

    public boolean equals(Object obj) {
        return obj != null && obj instanceof StringKeyStringValueDto && this.strEquals(this.locale, ((LocaleMapDto)obj).getLocale()) && this.strEquals(this.properties.toString(), ((LocaleMapDto)obj).getProperties().toString());
    }

    public int hashCode() {
        return (this.locale == null?1:this.locale.hashCode()) * (this.properties == null?-1:this.properties.hashCode());
    }

    private boolean strEquals(String s1, String s2) {
        return s1 == s2 || s1 != null && s1.equals(s2);
    }
}
