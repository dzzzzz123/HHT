package ext.sap.project;

public class ProjectEntity {
	private String ProjectNumber;
	private String ProjectName;
	private String ProjectCategory;
	private String ProjectOwner;
	private String ProjectCreateStamp;
	private String ProjectEndStamp;
	private String FactoryCode;
	private String ProjectDescription;
	private String FinishFlag;

	public ProjectEntity() {
	}

	public ProjectEntity(String projectNumber, String projectName, String projectCategory, String projectOwner,
			String projectCreateStamp, String projectEndStamp, String factoryCode, String projectDescription,
			String finishFlag) {
		ProjectNumber = projectNumber;
		ProjectName = projectName;
		ProjectCategory = projectCategory;
		ProjectOwner = projectOwner;
		ProjectCreateStamp = projectCreateStamp;
		ProjectEndStamp = projectEndStamp;
		FactoryCode = factoryCode;
		ProjectDescription = projectDescription;
		FinishFlag = finishFlag;
	}

	public String getProjectNumber() {
		return ProjectNumber;
	}

	public void setProjectNumber(String projectNumber) {
		ProjectNumber = projectNumber;
	}

	public String getProjectName() {
		return ProjectName;
	}

	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}

	public String getProjectCategory() {
		return ProjectCategory;
	}

	public void setProjectCategory(String projectCategory) {
		ProjectCategory = projectCategory;
	}

	public String getProjectOwner() {
		return ProjectOwner;
	}

	public void setProjectOwner(String projectOwner) {
		ProjectOwner = projectOwner;
	}

	public String getProjectCreateStamp() {
		return ProjectCreateStamp;
	}

	public void setProjectCreateStamp(String projectCreateStamp) {
		ProjectCreateStamp = projectCreateStamp;
	}

	public String getProjectEndStamp() {
		return ProjectEndStamp;
	}

	public void setProjectEndStamp(String projectEndStamp) {
		ProjectEndStamp = projectEndStamp;
	}

	public String getFactoryCode() {
		return FactoryCode;
	}

	public void setFactoryCode(String factoryCode) {
		FactoryCode = factoryCode;
	}

	public String getProjectDescription() {
		return ProjectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		ProjectDescription = projectDescription;
	}

	public String getFinishFlag() {
		return FinishFlag;
	}

	public void setFinishFlag(String finishFlag) {
		FinishFlag = finishFlag;
	}

	@Override
	public String toString() {
		return "ProjectEntity [ProjectNumber=" + ProjectNumber + ", ProjectName=" + ProjectName + ", ProjectCategory="
				+ ProjectCategory + ", ProjectOwner=" + ProjectOwner + ", ProjectCreateStamp=" + ProjectCreateStamp
				+ ", ProjectEndStamp=" + ProjectEndStamp + ", FactoryCode=" + FactoryCode + ", ProjectDescription="
				+ ProjectDescription + ", FinishFlag=" + FinishFlag + "]";
	}

}
