package ext.requirement.insert;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OfferingRequirement {
	@JsonProperty("@odata.type")
	private String odataType;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Number")
	private String number;

	@JsonProperty("HHTRequirementDescription")
	private String description;

	@JsonProperty("Context@odata.bind")
	private String contextOdataBind;

	@JsonProperty("Folder@odata.bind")
	private String folderOdataBind;

	public OfferingRequirement() {

	}

	public OfferingRequirement(String odataType, String name, String number, String description,
			String contextOdataBind, String folderOdataBind) {
		super();
		this.odataType = odataType;
		this.name = name;
		this.number = number;
		this.description = description;
		this.contextOdataBind = contextOdataBind;
		this.folderOdataBind = folderOdataBind;
	}

	public String getOdataType() {
		return odataType;
	}

	public void setOdataType(String odataType) {
		this.odataType = odataType;
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

	public String getContextOdataBind() {
		return contextOdataBind;
	}

	public void setContextOdataBind(String contextOdataBind) {
		this.contextOdataBind = contextOdataBind;
	}

	public String getFolderOdataBind() {
		return folderOdataBind;
	}

	public void setFolderOdataBind(String folderOdataBind) {
		this.folderOdataBind = folderOdataBind;
	}

	@Override
	public String toString() {
		return "OfferingRequirement [odataType=" + odataType + ", name=" + name + ", number=" + number
				+ ", description=" + description + ", contextOdataBind=" + contextOdataBind + ", folderOdataBind="
				+ folderOdataBind + "]";
	}

}
