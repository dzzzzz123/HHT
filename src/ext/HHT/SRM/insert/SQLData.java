package ext.HHT.SRM.insert;

public class SQLData {
	private String name;
	private String docNumber;
	private String partNumber;
	private String supplier;
	private String department;
	private String docType;
	private String version;
	private String filePath;

	public SQLData() {
		super();
	}

	public SQLData(String name, String docNumber, String partNumber, String supplier, String department, String docType,
			String version, String filePath) {
		super();
		this.name = name;
		this.docNumber = docNumber;
		this.partNumber = partNumber;
		this.supplier = supplier;
		this.department = department;
		this.docType = docType;
		this.version = version;
		this.filePath = filePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	@Override
	public String toString() {
		return "SQLData [name=" + name + ", docNumber=" + docNumber + ", partNumber=" + partNumber + ", supplier="
				+ supplier + ", department=" + department + ", docType=" + docType + ", version=" + version
				+ ", filePath=" + filePath + "]";
	}

}
