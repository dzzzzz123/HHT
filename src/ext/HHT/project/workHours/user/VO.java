package ext.HHT.project.workHours.user;

public class VO {

	private String UserName;
	private String UserFullName;
	private String DoneEffort;
	private String BeforeTime;
	private String AfterTime;

	public VO() {
		super();
	}

	public VO(String userName, String userFullName, String doneEffort, String beforeTime, String afterTime) {
		super();
		UserName = userName;
		UserFullName = userFullName;
		DoneEffort = doneEffort;
		BeforeTime = beforeTime;
		AfterTime = afterTime;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserFullName() {
		return UserFullName;
	}

	public void setUserFullName(String userFullName) {
		UserFullName = userFullName;
	}

	public String getDoneEffort() {
		return DoneEffort;
	}

	public void setDoneEffort(String doneEffort) {
		DoneEffort = doneEffort;
	}

	public String getBeforeTime() {
		return BeforeTime;
	}

	public void setBeforeTime(String beforeTime) {
		BeforeTime = beforeTime;
	}

	public String getAfterTime() {
		return AfterTime;
	}

	public void setAfterTime(String afterTime) {
		AfterTime = afterTime;
	}

	@Override
	public String toString() {
		return "VO [UserName=" + UserName + ", UserFullName=" + UserFullName + ", DoneEffort=" + DoneEffort
				+ ", BeforeTime=" + BeforeTime + ", AfterTime=" + AfterTime + "]";
	}

}
