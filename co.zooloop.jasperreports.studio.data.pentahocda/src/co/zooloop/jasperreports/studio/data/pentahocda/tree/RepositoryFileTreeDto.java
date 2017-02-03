package co.zooloop.jasperreports.studio.data.pentahocda.tree;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RepositoryFileTreeDto implements Serializable {
    
	
    RepositoryFileDto file;
    List<RepositoryFileTreeDto> children;

    public RepositoryFileTreeDto() {
    }

    public RepositoryFileDto getFile() {
        return this.file;
    }

    public void setFile(RepositoryFileDto file) {
        this.file = file;
    }

    public List<RepositoryFileTreeDto> getChildren() {
        return this.children;
    }

    public void setChildren(List<RepositoryFileTreeDto> children) {
        this.children = children;
    }

    public String toString() {
        return "RepositoryFileTreeDto [file=" + this.file + ", children=" + this.children + "]";
    }

    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(this.children == null) {
            this.children = Collections.emptyList();
        }

    }
}
