package ext.HHT.project.workHours.project;

public class Result {

	private String HHT_ActivityName;
	private String HHT_UserName;
	private String HHT_StandardHours;
	private String HHT_ActualHours;
	private String HHT_PercentWorkComplete;

	public Result() {
		super();
	}

	public Result(String hHT_ActivityName, String hHT_UserName, String hHT_StandardHours, String hHT_ActualHours,
			String hHT_PercentWorkComplete) {
		super();
		HHT_ActivityName = hHT_ActivityName;
		HHT_UserName = hHT_UserName;
		HHT_StandardHours = hHT_StandardHours;
		HHT_ActualHours = hHT_ActualHours;
		HHT_PercentWorkComplete = hHT_PercentWorkComplete;
	}

	public String getHHT_ActivityName() {
		return HHT_ActivityName;
	}

	public void setHHT_ActivityName(String hHT_ActivityName) {
		HHT_ActivityName = hHT_ActivityName;
	}

	public String getHHT_UserName() {
		return HHT_UserName;
	}

	public void setHHT_UserName(String hHT_UserName) {
		HHT_UserName = hHT_UserName;
	}

	public String getHHT_StandardHours() {
		return HHT_StandardHours;
	}

	public void setHHT_StandardHours(String hHT_StandardHours) {
		HHT_StandardHours = hHT_StandardHours;
	}

	public String getHHT_ActualHours() {
		return HHT_ActualHours;
	}

	public void setHHT_ActualHours(String hHT_ActualHours) {
		HHT_ActualHours = hHT_ActualHours;
	}

	public String getHHT_PercentWorkComplete() {
		return HHT_PercentWorkComplete;
	}

	public void setHHT_PercentWorkComplete(String hHT_PercentWorkComplete) {
		HHT_PercentWorkComplete = hHT_PercentWorkComplete;
	}

	@Override
	public String toString() {
		return "Result [HHT_ActivityName=" + HHT_ActivityName + ", HHT_UserName=" + HHT_UserName
				+ ", HHT_StandardHours=" + HHT_StandardHours + ", HHT_ActualHours=" + HHT_ActualHours
				+ ", HHT_PercentWorkComplete=" + HHT_PercentWorkComplete + "]";
	}

}