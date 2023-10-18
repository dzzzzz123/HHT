package ext.requirement.insert;

public class Requirement {

	private String name;
	private String context;
	private String folder;
	private String description;
	private String HHT_Priority;
	private String HHT_ReqCategory;
	private String HHT_ReqBelong;
	private String HHT_ReqGroup;
	private String HHT_ReqSource;
	private String HHT_ipdReq;
	private String HHT_CustomerComment;
	private String HHT_CustomerRole;

	public Requirement() {
		super();
	}

	public Requirement(String name, String context, String folder, String description, String hHT_Priority,
			String hHT_ReqCategory, String hHT_ReqBelong, String hHT_ReqGroup, String hHT_ReqSource, String hHT_ipdReq,
			String hHT_CustomerComment, String hHT_CustomerRole) {
		super();
		this.name = name;
		this.context = context;
		this.folder = folder;
		this.description = description;
		HHT_Priority = hHT_Priority;
		HHT_ReqCategory = hHT_ReqCategory;
		HHT_ReqBelong = hHT_ReqBelong;
		HHT_ReqGroup = hHT_ReqGroup;
		HHT_ReqSource = hHT_ReqSource;
		HHT_ipdReq = hHT_ipdReq;
		HHT_CustomerComment = hHT_CustomerComment;
		HHT_CustomerRole = hHT_CustomerRole;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHHT_Priority() {
		return HHT_Priority;
	}

	public void setHHT_Priority(String hHT_Priority) {
		HHT_Priority = hHT_Priority;
	}

	public String getHHT_ReqCategory() {
		return HHT_ReqCategory;
	}

	public void setHHT_ReqCategory(String hHT_ReqCategory) {
		HHT_ReqCategory = hHT_ReqCategory;
	}

	public String getHHT_ReqBelong() {
		return HHT_ReqBelong;
	}

	public void setHHT_ReqBelong(String hHT_ReqBelong) {
		HHT_ReqBelong = hHT_ReqBelong;
	}

	public String getHHT_ReqGroup() {
		return HHT_ReqGroup;
	}

	public void setHHT_ReqGroup(String hHT_ReqGroup) {
		HHT_ReqGroup = hHT_ReqGroup;
	}

	public String getHHT_ReqSource() {
		return HHT_ReqSource;
	}

	public void setHHT_ReqSource(String hHT_ReqSource) {
		HHT_ReqSource = hHT_ReqSource;
	}

	public String getHHT_ipdReq() {
		return HHT_ipdReq;
	}

	public void setHHT_ipdReq(String hHT_ipdReq) {
		HHT_ipdReq = hHT_ipdReq;
	}

	public String getHHT_CustomerComment() {
		return HHT_CustomerComment;
	}

	public void setHHT_CustomerComment(String hHT_CustomerComment) {
		HHT_CustomerComment = hHT_CustomerComment;
	}

	public String getHHT_CustomerRole() {
		return HHT_CustomerRole;
	}

	public void setHHT_CustomerRole(String hHT_CustomerRole) {
		HHT_CustomerRole = hHT_CustomerRole;
	}

	@Override
	public String toString() {
		return "Requirement [name=" + name + ", context=" + context + ", folder=" + folder + ", description="
				+ description + ", HHT_Priority=" + HHT_Priority + ", HHT_ReqCategory=" + HHT_ReqCategory
				+ ", HHT_ReqBelong=" + HHT_ReqBelong + ", HHT_ReqGroup=" + HHT_ReqGroup + ", HHT_ReqSource="
				+ HHT_ReqSource + ", HHT_ipdReq=" + HHT_ipdReq + ", HHT_CustomerComment=" + HHT_CustomerComment
				+ ", HHT_CustomerRole=" + HHT_CustomerRole + "]";
	}
}
