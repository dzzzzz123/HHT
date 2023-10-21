package ext.requirement.insert;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OfferingRequirement {
	private String odataType;

	private String contextOdataBind;

	private String folderOdataBind;

	private String name;

	private String HHTReqBelong;

	private Map<String, String> HHTReqCategory;

	private String HHTPriority;

	private String HHTReqSource;

	private String HHTipdReq;

	private String HHTCustomerRole;

	private String HHTCustomerComment;

	public OfferingRequirement() {

	}

	public OfferingRequirement(String odataType, String contextOdataBind, String folderOdataBind, String name,
			String hHTDescription, String hHTReqBelong, Map<String, String> hHTReqCategory, String hHTPriority,
			String hHTReqSource, String hHTipdReq, String hHTCustomerRole, String hHTCustomerComment) {
		super();
		this.odataType = odataType;
		this.contextOdataBind = contextOdataBind;
		this.folderOdataBind = folderOdataBind;
		this.name = name;
		HHTReqBelong = hHTReqBelong;
		HHTReqCategory = hHTReqCategory;
		HHTPriority = hHTPriority;
		HHTReqSource = hHTReqSource;
		HHTipdReq = hHTipdReq;
		HHTCustomerRole = hHTCustomerRole;
		HHTCustomerComment = hHTCustomerComment;
	}

	@JsonProperty("@odata.type")
	public String getOdataType() {
		return odataType;
	}

	public void setOdataType(String odataType) {
		this.odataType = odataType;
	}

	@JsonProperty("Context@odata.bind")
	public String getContextOdataBind() {
		return contextOdataBind;
	}

	public void setContextOdataBind(String contextOdataBind) {
		this.contextOdataBind = contextOdataBind;
	}

	@JsonProperty("Folder@odata.bind")
	public String getFolderOdataBind() {
		return folderOdataBind;
	}

	public void setFolderOdataBind(String folderOdataBind) {
		this.folderOdataBind = folderOdataBind;
	}

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("HHTReqBelong")
	public String getHHTReqBelong() {
		return HHTReqBelong;
	}

	public void setHHTReqBelong(String hHTReqBelong) {
		HHTReqBelong = hHTReqBelong;
	}

	@JsonProperty("HHTReqCategory")
	public Map<String, String> getHHTReqCategory() {
		return HHTReqCategory;
	}

	public void setHHTReqCategory(Map<String, String> hHTReqCategory) {
		HHTReqCategory = hHTReqCategory;
	}

	@JsonProperty("HHTPriority")
	public String getHHTPriority() {
		return HHTPriority;
	}

	public void setHHTPriority(String hHTPriority) {
		HHTPriority = hHTPriority;
	}

	@JsonProperty("HHTReqSource")
	public String getHHTReqSource() {
		return HHTReqSource;
	}

	public void setHHTReqSource(String hHTReqSource) {
		HHTReqSource = hHTReqSource;
	}

	@JsonProperty("HHTipdReq")
	public String getHHTipdReq() {
		return HHTipdReq;
	}

	public void setHHTipdReq(String hHTipdReq) {
		HHTipdReq = hHTipdReq;
	}

	@JsonProperty("HHTCustomerRole")
	public String getHHTCustomerRole() {
		return HHTCustomerRole;
	}

	public void setHHTCustomerRole(String hHTCustomerRole) {
		HHTCustomerRole = hHTCustomerRole;
	}

	@JsonProperty("HHTCustomerComment")
	public String getHHTCustomerComment() {
		return HHTCustomerComment;
	}

	public void setHHTCustomerComment(String hHTCustomerComment) {
		HHTCustomerComment = hHTCustomerComment;
	}

	@Override
	public String toString() {
		return "OfferingRequirement [odataType=" + odataType + ", contextOdataBind=" + contextOdataBind
				+ ", folderOdataBind=" + folderOdataBind + ", name=" + name + ", HHTReqBelong=" + HHTReqBelong
				+ ", HHTReqCategory=" + HHTReqCategory + ", HHTPriority=" + HHTPriority + ", HHTReqSource="
				+ HHTReqSource + ", HHTipdReq=" + HHTipdReq + ", HHTCustomerRole=" + HHTCustomerRole
				+ ", HHTCustomerComment=" + HHTCustomerComment + "]";
	}
}
