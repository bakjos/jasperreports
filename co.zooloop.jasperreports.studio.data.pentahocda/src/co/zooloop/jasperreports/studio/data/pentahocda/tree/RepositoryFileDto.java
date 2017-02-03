package co.zooloop.jasperreports.studio.data.pentahocda.tree;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class RepositoryFileDto {

	String name;
	String id;
	Date createdDate;
	String creatorId;
	Date lastModifiedDate;
	long fileSize;
	boolean folder;
	String path;
	boolean hidden;
	boolean aclNode;
	boolean versioned;
	String versionId;
	boolean locked;
	String lockOwner;
	String lockMessage;
	Date lockDate;
	String owner;
	String ownerTenantPath;
	Boolean versioningEnabled;
	Boolean versionCommentEnabled;
	int ownerType = -1;
	String title;
	String description;
	String locale;
	String originalParentFolderPath;
	Date deletedDate;
	List<LocaleMapDto> localePropertiesMapEntries;
	RepositoryFileAclDto repositoryFileAclDto;

	public RepositoryFileDto() {
	}

	public String getOwnerTenantPath() {
		return this.ownerTenantPath;
	}

	public void setOwnerTenantPath(String ownerTenantPath) {
		this.ownerTenantPath = ownerTenantPath;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public boolean isFolder() {
		return this.folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isAclNode() {
		return this.aclNode;
	}

	public void setAclNode(boolean aclNode) {
		this.aclNode = aclNode;
	}

	public boolean isVersioned() {
		return this.versioned;
	}

	public void setVersioned(boolean versioned) {
		this.versioned = versioned;
	}

	public String getVersionId() {
		return this.versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getLockOwner() {
		return this.lockOwner;
	}

	public void setLockOwner(String lockOwner) {
		this.lockOwner = lockOwner;
	}

	public String getLockMessage() {
		return this.lockMessage;
	}

	public void setLockMessage(String lockMessage) {
		this.lockMessage = lockMessage;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getLockDate() {
		return this.lockDate;
	}

	public void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
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

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocale() {
		return this.locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getOriginalParentFolderPath() {
		return this.originalParentFolderPath;
	}

	public void setOriginalParentFolderPath(String originalParentFolderPath) {
		this.originalParentFolderPath = originalParentFolderPath;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getDeletedDate() {
		return this.deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	public RepositoryFileAclDto getRepositoryFileAclDto() {
		return this.repositoryFileAclDto;
	}

	public void setRepositoryFileAclDto(RepositoryFileAclDto repositoryFileAclDto) {
		this.repositoryFileAclDto = repositoryFileAclDto;
	}

	public List<LocaleMapDto> getLocalePropertiesMapEntries() {
		return this.localePropertiesMapEntries;
	}

	public void setLocalePropertiesMapEntries(List<LocaleMapDto> localePropertiesMapEntries) {
		this.localePropertiesMapEntries = localePropertiesMapEntries;
	}

	public String toString() {
		return "RepositoryFileDto [id=" + this.id + ", name=" + this.name + ", path=" + this.path + ", folder="
				+ this.folder + ", size=" + this.fileSize + ", createdDate=" + this.createdDate + ", creatorId="
				+ this.creatorId + ", deletedDate=" + this.deletedDate + ", description=" + this.description
				+ ", hidden=" + this.hidden + ", aclNode=" + this.aclNode + ", lastModifiedDate="
				+ this.lastModifiedDate + ", locale=" + this.locale + ", lockDate=" + this.lockDate + ", lockMessage="
				+ this.lockMessage + ", lockOwner=" + this.lockOwner + ", locked=" + this.locked
				+ ", originalParentFolderPath=" + this.originalParentFolderPath + ", owner=" + this.owner
				+ ", ownerType=" + this.ownerType + ", title=" + this.title + ", localePropertiesMapEntries="
				+ this.localePropertiesMapEntries + ", versionId=" + this.versionId + ", versioned=" + this.versioned
				+ ", versioningEnabled=" + this.versioningEnabled + ", versionCommentEnabled="
				+ this.versionCommentEnabled + ", hasAcl=" + (this.repositoryFileAclDto != null) + "]";
	}

	public Boolean getVersioningEnabled() {
		return this.versioningEnabled;
	}

	public void setVersioningEnabled(Boolean versioningEnabled) {
		this.versioningEnabled = versioningEnabled;
	}

	public Boolean getVersionCommentEnabled() {
		return this.versionCommentEnabled;
	}

	public void setVersionCommentEnabled(Boolean versionCommentEnabled) {
		this.versionCommentEnabled = versionCommentEnabled;
	}

}
