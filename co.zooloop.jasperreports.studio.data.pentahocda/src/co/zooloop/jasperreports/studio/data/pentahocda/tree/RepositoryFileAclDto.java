package co.zooloop.jasperreports.studio.data.pentahocda.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RepositoryFileAclDto implements Serializable {
    
    List<RepositoryFileAclAceDto> aces = new ArrayList(0);
    String id;
    String owner;
    String tenantPath;
    int ownerType = -1;
    boolean entriesInheriting = true;

    public RepositoryFileAclDto() {
    }

    public List<RepositoryFileAclAceDto> getAces() {
        return this.aces;
    }

    public void setAces(List<RepositoryFileAclAceDto> aces, boolean inheriting) {
        this.entriesInheriting = inheriting;
        this.aces = aces;
    }

    public void setAces(List<RepositoryFileAclAceDto> aces) {
        this.setAces(aces, false);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getOwnerType() {
        return this.ownerType;
    }

    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }

    public boolean isEntriesInheriting() {
        return this.entriesInheriting;
    }

    public void setEntriesInheriting(boolean entriesInheriting) {
        this.entriesInheriting = entriesInheriting;
    }

    public String getTenantPath() {
        return this.tenantPath;
    }

    public void setTenantPath(String tenantPath) {
        this.tenantPath = tenantPath;
    }

    public String toString() {
        return "RepositoryFileAclDto [id=" + this.id + ", entriesInheriting=" + this.entriesInheriting + ", owner=" + this.owner + ", ownerType=" + this.ownerType + ", aces=" + this.aces + "]";
    }
}