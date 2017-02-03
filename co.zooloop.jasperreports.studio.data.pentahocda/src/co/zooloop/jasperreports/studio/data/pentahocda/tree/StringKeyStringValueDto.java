package co.zooloop.jasperreports.studio.data.pentahocda.tree;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StringKeyStringValueDto implements Serializable {
    
    private String key;
    private String value;

    public StringKeyStringValueDto() {
    }

    public StringKeyStringValueDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString() {
        return "StringKeyStringValueDto [key=" + this.key + ", value=" + this.value + "]";
    }

    public boolean equals(Object obj) {
        return obj != null && obj instanceof StringKeyStringValueDto && this.strEquals(this.key, ((StringKeyStringValueDto)obj).getKey()) && this.strEquals(this.value, ((StringKeyStringValueDto)obj).getValue());
    }

    public int hashCode() {
        return (this.key == null?1:this.key.hashCode()) * (this.value == null?-1:this.value.hashCode());
    }

    private boolean strEquals(String s1, String s2) {
        return s1 == s2 || s1 != null && s1.equals(s2);
    }
}
