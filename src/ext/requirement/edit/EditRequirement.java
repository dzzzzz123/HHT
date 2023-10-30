package ext.requirement.edit;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EditRequirement {
	@JsonProperty("ID")
	private String ID;
	@JsonProperty("name")
	private String Name;
	@JsonProperty("description")
	private String Description;
	@JsonProperty("hhtpriority")
	private String HHTPriority;
	@JsonProperty("hhtreqCategory")
	private String HHTReqCategory;
	@JsonProperty("hhtreqBelong")
	private String HHTReqBelong;
	@JsonProperty("hhtreqGroup")
	private String HHTReqGroup;
	@JsonProperty("hhtreqSource")
	private String HHTReqSource;
	@JsonProperty("hhtipdReq")
	private String HHTipdReq;
	@JsonProperty("hhtcustomerComment")
	private String HHTCustomerComment;
	@JsonProperty("hhtcustomerRole")
	private String HHTCustomerRole;

	public EditRequirement() {
		super();
	}

	public EditRequirement(String iD, String name, String description, String hHTPriority, String hHTReqCategory,
			String hHTReqBelong, String hHTReqGroup, String hHTReqSource, String hHTipdReq, String hHTCustomerComment,
			String hHTCustomerRole) {
		super();
		ID = iD;
		Name = name;
		Description = description;
		HHTPriority = hHTPriority;
		HHTReqCategory = hHTReqCategory;
		HHTReqBelong = hHTReqBelong;
		HHTReqGroup = hHTReqGroup;
		HHTReqSource = hHTReqSource;
		HHTipdReq = hHTipdReq;
		HHTCustomerComment = hHTCustomerComment;
		HHTCustomerRole = hHTCustomerRole;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getHHTPriority() {
		return HHTPriority;
	}

	public void setHHTPriority(String hHTPriority) {
		HHTPriority = hHTPriority;
	}

	public String getHHTReqCategory() {
		return HHTReqCategory;
	}

	public void setHHTReqCategory(String hHTReqCategory) {
		HHTReqCategory = hHTReqCategory;
	}

	public String getHHTReqBelong() {
		return HHTReqBelong;
	}

	public void setHHTReqBelong(String hHTReqBelong) {
		HHTReqBelong = hHTReqBelong;
	}

	public String getHHTReqGroup() {
		return HHTReqGroup;
	}

	public void setHHTReqGroup(String hHTReqGroup) {
		HHTReqGroup = hHTReqGroup;
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

	public String getHHTCustomerComment() {
		return HHTCustomerComment;
	}

	public void setHHTCustomerComment(String hHTCustomerComment) {
		HHTCustomerComment = hHTCustomerComment;
	}

	public String getHHTCustomerRole() {
		return HHTCustomerRole;
	}

	public void setHHTCustomerRole(String hHTCustomerRole) {
		HHTCustomerRole = hHTCustomerRole;
	}

	@Override
	public String toString() {
		return "EditRequirement [ID=" + ID + ", Name=" + Name + ", Description=" + Description + ", HHTPriority="
				+ HHTPriority + ", HHTReqCategory=" + HHTReqCategory + ", HHTReqBelong=" + HHTReqBelong
				+ ", HHTReqGroup=" + HHTReqGroup + ", HHTReqSource=" + HHTReqSource + ", HHTipdReq=" + HHTipdReq
				+ ", HHTCustomerComment=" + HHTCustomerComment + ", HHTCustomerRole=" + HHTCustomerRole + "]";
	}

}
