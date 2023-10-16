package ext.requirement.create;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContainerData {
	@JsonProperty("Container")
	private Container container;

	public ContainerData(Container container) {
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public String toString() {
		return "ContainerData [container=" + container.toString() + "]";
	}

}

class Container {
	@JsonProperty("Context")
	private String context;

	@JsonProperty("Folder")
	private String folder;

	public Container(String context, String folder) {
		this.context = context;
		this.folder = folder;
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

	@Override
	public String toString() {
		return "Container [context=" + context + ", folder=" + folder + "]";
	}

}
