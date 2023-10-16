package ext.requirement.insert;

public class Requirement {
	private String number;
	private String name;
	private String context;
	private String folder;
	private String description;

	public Requirement() {
		super();
	}

	public Requirement(String number, String name, String context, String folder, String description) {
		super();
		this.number = number;
		this.name = name;
		this.context = context;
		this.folder = folder;
		this.description = description;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	@Override
	public String toString() {
		return "Requirement [number=" + number + ", name=" + name + ", context=" + context + ", folder=" + folder
				+ ", description=" + description + "]";
	}

}
