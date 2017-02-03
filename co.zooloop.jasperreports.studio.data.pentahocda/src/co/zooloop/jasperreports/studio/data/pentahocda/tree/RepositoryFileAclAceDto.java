package co.zooloop.jasperreports.studio.data.pentahocda.tree;


import java.io.Serializable;
import java.util.List;

public class RepositoryFileAclAceDto implements Serializable {
    
    String recipient;
    String tenantPath;
    boolean modifiable = true;
    int recipientType = -1;
    List<Integer> permissions;

    public RepositoryFileAclAceDto() {
    }

    public String getTenantPath() {
        return this.tenantPath;
    }

    public void setTenantPath(String tenantPath) {
        this.tenantPath = tenantPath;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getRecipientType() {
        return this.recipientType;
    }

    public void setRecipientType(int recipientType) {
        this.recipientType = recipientType;
    }

    public List<Integer> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<Integer> permissions) {
        this.permissions = permissions;
    }

    public boolean isModifiable() {
        return this.modifiable;
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    public String toString() {
        return "RepositoryFileAclAceDto [recipient=" + this.recipient + ", recipientType=" + this.recipientType + ", permissions=" + this.permissions + ", modifiable=" + this.modifiable + "]";
    }
}
