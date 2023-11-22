package ext.HHT.SRM;

import ext.ait.util.PropertiesUtil;

public class Config {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	public static String getValue(String key) {
		return properties.getValueByKey(key);
	}

	public static String getGrant_type() {
		return properties.getValueByKey("srm.token.grant_type");
	}

	public static String getScope() {
		return properties.getValueByKey("srm.token.scope");
	}

	public static String getClient_id() {
		return properties.getValueByKey("srm.token.client_id");
	}

	public static String getClient_secret() {
		return properties.getValueByKey("srm.token.client_secret");
	}

	public static String getBucketName() {
		return properties.getValueByKey("srm.multipart.bucketName");
	}

	public static String getDirectory() {
		return properties.getValueByKey("srm.multipart.directory");
	}

	public static String getApplicationCode() {
		return properties.getValueByKey("srm.acknowledgment.applicationCode");
	}

	public static String getApplicationGroupCode() {
		return properties.getValueByKey("srm.acknowledgment.applicationGroupCode");
	}

	public static String getExternalSystemCode() {
		return properties.getValueByKey("srm.acknowledgment.externalSystemCode");
	}

	public static String getInterfaceCode() {
		return properties.getValueByKey("srm.acknowledgment.interfaceCode");
	}

	public static String getAcknowLedgmentTypeName() {
		return properties.getValueByKey("windchill.acknowledgment.typeName");
	}

	public static String getAcknowLedgmentDocType() {
		return properties.getValueByKey("windchill.acknowledgment.docType");
	}

	public static String getTokenUrl() {
		return properties.getValueByKey("srm.token.url");
	}

	public static String getAcknowLedgmentUrl() {
		return properties.getValueByKey("srm.acknowledgment.url");
	}

	public static String getUUidUrl() {
		return properties.getValueByKey("srm.uuid.url");
	}

	public static String getMultipartUrl() {
		return properties.getValueByKey("srm.multipart.url");
	}

}
