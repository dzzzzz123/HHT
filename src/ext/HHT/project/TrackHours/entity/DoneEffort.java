package ext.HHT.project.TrackHours.entity;

public class DoneEffort {

	private String RESOURCEASSIGNMENTID;
	private String DONEEFFORT;
	private String USERID;
	private String PLANACTIVITYID;
	private String PROJECTID;
	private String TIME;
	private String IDA2A2;

	public DoneEffort() {
		super();
	}

	public DoneEffort(String rESOURCEASSIGNMENTID, String dONEEFFORT, String uSERID, String pLANACTIVITYID,
			String pROJECTID, String tIME, String iDA2A2) {
		super();
		RESOURCEASSIGNMENTID = rESOURCEASSIGNMENTID;
		DONEEFFORT = dONEEFFORT;
		USERID = uSERID;
		PLANACTIVITYID = pLANACTIVITYID;
		PROJECTID = pROJECTID;
		TIME = tIME;
		IDA2A2 = iDA2A2;
	}

	public String getRESOURCEASSIGNMENTID() {
		return RESOURCEASSIGNMENTID;
	}

	public void setRESOURCEASSIGNMENTID(String rESOURCEASSIGNMENTID) {
		RESOURCEASSIGNMENTID = rESOURCEASSIGNMENTID;
	}

	public String getDONEEFFORT() {
		return DONEEFFORT;
	}

	public void setDONEEFFORT(String dONEEFFORT) {
		DONEEFFORT = dONEEFFORT;
	}

	public String getUSERID() {
		return USERID;
	}

	public void setUSERID(String uSERID) {
		USERID = uSERID;
	}

	public String getPLANACTIVITYID() {
		return PLANACTIVITYID;
	}

	public void setPLANACTIVITYID(String pLANACTIVITYID) {
		PLANACTIVITYID = pLANACTIVITYID;
	}

	public String getPROJECTID() {
		return PROJECTID;
	}

	public void setPROJECTID(String pROJECTID) {
		PROJECTID = pROJECTID;
	}

	public String getTIME() {
		return TIME;
	}

	public void setTIME(String tIME) {
		TIME = tIME;
	}

	public String getIDA2A2() {
		return IDA2A2;
	}

	public void setIDA2A2(String iDA2A2) {
		IDA2A2 = iDA2A2;
	}

	@Override
	public String toString() {
		return "DoneEffort [RESOURCEASSIGNMENTID=" + RESOURCEASSIGNMENTID + ", DONEEFFORT=" + DONEEFFORT + ", USERID="
				+ USERID + ", PLANACTIVITYID=" + PLANACTIVITYID + ", PROJECTID=" + PROJECTID + ", TIME=" + TIME
				+ ", IDA2A2=" + IDA2A2 + "]";
	}

}
