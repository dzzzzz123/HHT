package ext.HHT.project.TrackHours.entity;

public class DoneEffortVO {

	private String PlanActivityName;
	private String UserName;
	private String PreviousDoneEffort;
	private String PreviousPercentWorkComplete;
	private String CurrentDoneEffort;
	private String CurrentPercentWorkComplete;
	private String CurrentTime;

	public DoneEffortVO() {
		super();
	}

	public DoneEffortVO(String planActivityName, String userName, String previousDoneEffort,
			String previousPercentWorkComplete, String currentDoneEffort, String currentPercentWorkComplete,
			String currentTime) {
		super();
		PlanActivityName = planActivityName;
		UserName = userName;
		PreviousDoneEffort = previousDoneEffort;
		PreviousPercentWorkComplete = previousPercentWorkComplete;
		CurrentDoneEffort = currentDoneEffort;
		CurrentPercentWorkComplete = currentPercentWorkComplete;
		CurrentTime = currentTime;
	}

	public String getPlanActivityName() {
		return PlanActivityName;
	}

	public void setPlanActivityName(String planActivityName) {
		PlanActivityName = planActivityName;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPreviousDoneEffort() {
		return PreviousDoneEffort;
	}

	public void setPreviousDoneEffort(String previousDoneEffort) {
		PreviousDoneEffort = previousDoneEffort;
	}

	public String getPreviousPercentWorkComplete() {
		return PreviousPercentWorkComplete;
	}

	public void setPreviousPercentWorkComplete(String previousPercentWorkComplete) {
		PreviousPercentWorkComplete = previousPercentWorkComplete;
	}

	public String getCurrentDoneEffort() {
		return CurrentDoneEffort;
	}

	public void setCurrentDoneEffort(String currentDoneEffort) {
		CurrentDoneEffort = currentDoneEffort;
	}

	public String getCurrentPercentWorkComplete() {
		return CurrentPercentWorkComplete;
	}

	public void setCurrentPercentWorkComplete(String currentPercentWorkComplete) {
		CurrentPercentWorkComplete = currentPercentWorkComplete;
	}

	public String getCurrentTime() {
		return CurrentTime;
	}

	public void setCurrentTime(String currentTime) {
		CurrentTime = currentTime;
	}

	@Override
	public String toString() {
		return "DoneEffortVO [PlanActivityName=" + PlanActivityName + ", UserName=" + UserName + ", PreviousDoneEffort="
				+ PreviousDoneEffort + ", PreviousPercentWorkComplete=" + PreviousPercentWorkComplete
				+ ", CurrentDoneEffort=" + CurrentDoneEffort + ", CurrentPercentWorkComplete="
				+ CurrentPercentWorkComplete + ", CurrentTime=" + CurrentTime + "]";
	}

}
