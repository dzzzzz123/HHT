package ext.srm.pdf;

public class PartInfo {

	private String number;
	private String version;
	private String path;

	public PartInfo(String number, String version, String path) {
		super();
		this.number = number;
		this.version = version;
		this.path = path;
	}

	@Override
	public String toString() {
		return "PartInfo [number=" + number + ", version=" + version + ", path=" + path + "]";
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
