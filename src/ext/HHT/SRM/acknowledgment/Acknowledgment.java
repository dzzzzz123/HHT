package ext.HHT.SRM.acknowledgment;

import java.util.ArrayList;

public class Acknowledgment {
	private Header header;
	private ArrayList<Body> body;

	public Acknowledgment() {
		super();
	}

	public Acknowledgment(Header header, ArrayList<Body> body) {
		super();
		this.header = header;
		this.body = body;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public ArrayList<Body> getBody() {
		return body;
	}

	public void setBody(ArrayList<Body> body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "Acknowledgment [header=" + header.toString() + ", body=" + body.toString() + "]";
	}

	class Header {
		private String applicationCode;
		private String applicationGroupCode;
		private String batchNum;
		private String externalSystemCode;
		private String interfaceCode;

		public Header() {
			super();
		}

		public Header(String applicationCode, String applicationGroupCode, String batchNum, String externalSystemCode,
				String interfaceCode) {
			super();
			this.applicationCode = applicationCode;
			this.applicationGroupCode = applicationGroupCode;
			this.batchNum = batchNum;
			this.externalSystemCode = externalSystemCode;
			this.interfaceCode = interfaceCode;
		}

		public String getApplicationCode() {
			return applicationCode;
		}

		public void setApplicationCode(String applicationCode) {
			this.applicationCode = applicationCode;
		}

		public String getApplicationGroupCode() {
			return applicationGroupCode;
		}

		public void setApplicationGroupCode(String applicationGroupCode) {
			this.applicationGroupCode = applicationGroupCode;
		}

		public String getBatchNum() {
			return batchNum;
		}

		public void setBatchNum(String batchNum) {
			this.batchNum = batchNum;
		}

		public String getExternalSystemCode() {
			return externalSystemCode;
		}

		public void setExternalSystemCode(String externalSystemCode) {
			this.externalSystemCode = externalSystemCode;
		}

		public String getInterfaceCode() {
			return interfaceCode;
		}

		public void setInterfaceCode(String interfaceCode) {
			this.interfaceCode = interfaceCode;
		}

		@Override
		public String toString() {
			return "Header [applicationCode=" + applicationCode + ", applicationGroupCode=" + applicationGroupCode
					+ ", batchNum=" + batchNum + ", externalSystemCode=" + externalSystemCode + ", interfaceCode="
					+ interfaceCode + "]";
		}
	}

	class Body {
		private String itemCode;
		private String itemName;
		private String itemVersion;
		private String supplierCompanyCode;
		private String supplierCompanyName;
		private String attachmentUuid;
		private String attachmentVersion;

		public Body() {
			super();
		}

		public Body(String itemCode, String itemName, String itemVersion, String supplierCompanyCode,
				String supplierCompanyName, String attachmentUuid, String attachmentVersion) {
			super();
			this.itemCode = itemCode;
			this.itemName = itemName;
			this.itemVersion = itemVersion;
			this.supplierCompanyCode = supplierCompanyCode;
			this.supplierCompanyName = supplierCompanyName;
			this.attachmentUuid = attachmentUuid;
			this.attachmentVersion = attachmentVersion;
		}

		public String getItemCode() {
			return itemCode;
		}

		public void setItemCode(String itemCode) {
			this.itemCode = itemCode;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public String getItemVersion() {
			return itemVersion;
		}

		public void setItemVersion(String itemVersion) {
			this.itemVersion = itemVersion;
		}

		public String getSupplierCompanyCode() {
			return supplierCompanyCode;
		}

		public void setSupplierCompanyCode(String supplierCompanyCode) {
			this.supplierCompanyCode = supplierCompanyCode;
		}

		public String getSupplierCompanyName() {
			return supplierCompanyName;
		}

		public void setSupplierCompanyName(String supplierCompanyName) {
			this.supplierCompanyName = supplierCompanyName;
		}

		public String getAttachmentUuid() {
			return attachmentUuid;
		}

		public void setAttachmentUuid(String attachmentUuid) {
			this.attachmentUuid = attachmentUuid;
		}

		public String getAttachmentVersion() {
			return attachmentVersion;
		}

		public void setAttachmentVersion(String attachmentVersion) {
			this.attachmentVersion = attachmentVersion;
		}

		@Override
		public String toString() {
			return "Body [itemCode=" + itemCode + ", itemName=" + itemName + ", itemVersion=" + itemVersion
					+ ", supplierCompanyCode=" + supplierCompanyCode + ", supplierCompanyName=" + supplierCompanyName
					+ ", attachmentUuid=" + attachmentUuid + ", attachmentVersion=" + attachmentVersion + "]";
		}
	}
}
