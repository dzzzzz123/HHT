package ext.oa.entity;

import java.io.Serializable;

/**
 * 10:14 OA代办实体
 * 
 **/
public class OAWaitingProcessingEntity implements Serializable {

	private String syscode;
	private String flowid;
	private String requestname;
	private String workflowname;
	private String nodename;
	private String pcurl;
	private String appurl;
	private String creator;
	private String createdatetime;
	private String receiver;
	private String receivedatetime;
	private String isremark;
	private String viewtype;
	private String receivets;

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

	public String getRequestname() {
		return requestname;
	}

	public void setRequestname(String requestname) {
		this.requestname = requestname;
	}

	public String getWorkflowname() {
		return workflowname;
	}

	public void setWorkflowname(String workflowname) {
		this.workflowname = workflowname;
	}

	public String getNodename() {
		return nodename;
	}

	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	public String getPcurl() {
		return pcurl;
	}

	public void setPcurl(String pcurl) {
		this.pcurl = pcurl;
	}

	public String getAppurl() {
		return appurl;
	}

	public void setAppurl(String appurl) {
		this.appurl = appurl;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreatedatetime() {
		return createdatetime;
	}

	public void setCreatedatetime(String createdatetime) {
		this.createdatetime = createdatetime;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceivedatetime() {
		return receivedatetime;
	}

	public void setReceivedatetime(String receivedatetime) {
		this.receivedatetime = receivedatetime;
	}

	public String getIsremark() {
		return isremark;
	}

	public void setIsremark(String isremark) {
		this.isremark = isremark;
	}

	public String getViewtype() {
		return viewtype;
	}

	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}

	public String getReceivets() {
		return receivets;
	}

	public void setReceivets(String receivets) {
		this.receivets = receivets;
	}

	@Override
	public String toString() {
		return "OAWaitingProcessingEntity [syscode=" + syscode + ", flowid=" + flowid + ", requestname=" + requestname
				+ ", workflowname=" + workflowname + ", nodename=" + nodename + ", pcurl=" + pcurl + ", appurl="
				+ appurl + ", creator=" + creator + ", createdatetime=" + createdatetime + ", receiver=" + receiver
				+ ", receivedatetime=" + receivedatetime + ", isremark=" + isremark + ", viewtype=" + viewtype
				+ ", receivets=" + receivets + "]";
	}

}
