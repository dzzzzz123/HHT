package ext.HHT.BOMDiff;

public class Entity {

	private String id;
	private String type;
	private String replaceGroup;
	private String parentPartNumber;
	private String partNumberA;
	private String partNumberB;
	private String partDescA;
	private String partDescB;
	private String partAmountA;
	private String partAmountB;
	private String versionA;
	private String versionB;

	public Entity() {
		super();
	}

	public Entity(String id, String type, String replaceGroup, String parentPartNumber, String partNumberA,
			String partNumberB, String partDescA, String partDescB, String partAmountA, String partAmountB,
			String versionA, String versionB) {
		super();
		this.id = id;
		this.type = type;
		this.replaceGroup = replaceGroup;
		this.parentPartNumber = parentPartNumber;
		this.partNumberA = partNumberA;
		this.partNumberB = partNumberB;
		this.partDescA = partDescA;
		this.partDescB = partDescB;
		this.partAmountA = partAmountA;
		this.partAmountB = partAmountB;
		this.versionA = versionA;
		this.versionB = versionB;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReplaceGroup() {
		return replaceGroup;
	}

	public void setReplaceGroup(String replaceGroup) {
		this.replaceGroup = replaceGroup;
	}

	public String getParentPartNumber() {
		return parentPartNumber;
	}

	public void setParentPartNumber(String parentPartNumber) {
		this.parentPartNumber = parentPartNumber;
	}

	public String getPartNumberA() {
		return partNumberA;
	}

	public void setPartNumberA(String partNumberA) {
		this.partNumberA = partNumberA;
	}

	public String getPartNumberB() {
		return partNumberB;
	}

	public void setPartNumberB(String partNumberB) {
		this.partNumberB = partNumberB;
	}

	public String getPartDescA() {
		return partDescA;
	}

	public void setPartDescA(String partDescA) {
		this.partDescA = partDescA;
	}

	public String getPartDescB() {
		return partDescB;
	}

	public void setPartDescB(String partDescB) {
		this.partDescB = partDescB;
	}

	public String getPartAmountA() {
		return partAmountA;
	}

	public void setPartAmountA(String partAmountA) {
		this.partAmountA = partAmountA;
	}

	public String getPartAmountB() {
		return partAmountB;
	}

	public void setPartAmountB(String partAmountB) {
		this.partAmountB = partAmountB;
	}

	public String getVersionA() {
		return versionA;
	}

	public void setVersionA(String versionA) {
		this.versionA = versionA;
	}

	public String getVersionB() {
		return versionB;
	}

	public void setVersionB(String versionB) {
		this.versionB = versionB;
	}

	@Override
	public String toString() {
		return "Entity [id=" + id + ", type=" + type + ", replaceGroup=" + replaceGroup + ", parentPartNumber="
				+ parentPartNumber + ", partNumberA=" + partNumberA + ", partNumberB=" + partNumberB + ", partDescA="
				+ partDescA + ", partDescB=" + partDescB + ", partAmountA=" + partAmountA + ", partAmountB="
				+ partAmountB + ", versionA=" + versionA + ", versionB=" + versionB + "]";
	}

}
