package ext.requirement.info;

public class InfoRequirement {

	private String name;
	private String number;
	private String description;
	private String HHTReqBelong;
	private String HHTReqCategory;
	private String HHTPriority;
	private String HHTReqSource;
	private String HHTReqGroup;
	private String HHTipdReq;
	private String HHTCustomerRole;
	private String HHTCustomerComment;

	public InfoRequirement() {
		super();
	}

	public InfoRequirement(String name, String number, String description, String hHTReqBelong, String hHTReqCategory,
			String hHTPriority, String hHTReqSource, String hHTReqGroup, String hHTipdReq, String hHTCustomerRole,
			String hHTCustomerComment) {
		super();
		this.name = name;
		this.number = number;
		this.description = description;
		HHTReqBelong = hHTReqBelong;
		HHTReqCategory = hHTReqCategory;
		HHTPriority = hHTPriority;
		HHTReqSource = hHTReqSource;
		HHTReqGroup = hHTReqGroup;
		HHTipdReq = hHTipdReq;
		HHTCustomerRole = hHTCustomerRole;
		HHTCustomerComment = hHTCustomerComment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHHTReqBelong() {
		return HHTReqBelong;
	}

	public void setHHTReqBelong(String hHTReqBelong) {
		HHTReqBelong = hHTReqBelong;
	}

	public String getHHTReqCategory() {
		return HHTReqCategory;
	}

	public void setHHTReqCategory(String hHTReqCategory) {
		HHTReqCategory = hHTReqCategory;
	}

	public String getHHTPriority() {
		return HHTPriority;
	}

	public void setHHTPriority(String hHTPriority) {
		HHTPriority = hHTPriority;
	}

	public String getHHTReqSource() {
		return HHTReqSource;
	}

	public void setHHTReqSource(String hHTReqSource) {
		HHTReqSource = hHTReqSource;
	}

	public String getHHTipdReq() {
		return HHTipdReq;
	}

	public void setHHTipdReq(String hHTipdReq) {
		HHTipdReq = hHTipdReq;
	}

	public String getHHTCustomerRole() {
		return HHTCustomerRole;
	}

	public void setHHTCustomerRole(String hHTCustomerRole) {
		HHTCustomerRole = hHTCustomerRole;
	}

	public String getHHTReqGroup() {
		return HHTReqGroup;
	}

	public void setHHTReqGroup(String hHTReqGroup) {
		HHTReqGroup = hHTReqGroup;
	}

	public String getHHTCustomerComment() {
		return HHTCustomerComment;
	}

	public void setHHTCustomerComment(String hHTCustomerComment) {
		HHTCustomerComment = hHTCustomerComment;
	}

	@Override
	public String toString() {
		return "InfoRequirement [name=" + name + ", number=" + number + ", description=" + description
				+ ", HHTReqBelong=" + HHTReqBelong + ", HHTReqCategory=" + HHTReqCategory + ", HHTPriority="
				+ HHTPriority + ", HHTReqSource=" + HHTReqSource + ", HHTReqGroup=" + HHTReqGroup + ", HHTipdReq="
				+ HHTipdReq + ", HHTCustomerRole=" + HHTCustomerRole + ", HHTCustomerComment=" + HHTCustomerComment
				+ "]";
	}

}
