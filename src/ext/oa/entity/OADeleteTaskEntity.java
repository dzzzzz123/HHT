package ext.oa.entity;

import java.io.Serializable;

public class OADeleteTaskEntity implements Serializable{
	
	private String syscode;
	private String flowid;
	private String userid;
	public String getSyscode() {
		return syscode;
	}
	public void setSyscode(String syscode) {
		this.syscode = syscode;
	}
	public String getFlowid() {
		return flowid;
	}
	public void setFlowid(String flowid) {
		this.flowid = flowid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	@Override
	public String toString() {
		return "OADeleteTaskEntity [syscode=" + syscode + ", flowid=" + flowid + ", userid=" + userid + "]";
	}
	
	
	

}
