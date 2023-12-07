package ext.HHT;

import ext.ait.util.PropertiesUtil;
import wt.iba.value.IBAHolder;

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

	// part.duplicateCheck
	public static String getHHT_LongtDescription() {
		return properties.getValueByKey("iba.internal.HHT_LongtDescription");
	}

	public static String getIBA_HHT_Bonded() {
		return properties.getValueByKey("iba.paramMap.HHT_Bonded");
	}

	public static String getIBA_NonbondedNumber() {
		return properties.getValueByKey("iba.paramMap.NonbondedNumber");
	}

	public static String getIBA_HHT_Classification() {
		return properties.getValueByKey("iba.paramMap.HHT_Classification");
	}

	public static String getHHT_Classification() {
		return properties.getValueByKey("iba.internal.HHT_Classification");
	}

	public static String getHHT_Classification(IBAHolder ibaHolder) {
		return properties.getValueByKey(ibaHolder, "iba.internal.HHT_Classification");
	}

	public static String getHHT_Factory(IBAHolder ibaHolder) {
		return properties.getValueByKey(ibaHolder, "iba.internal.HHT_Factory");
	}

	public static String getHHT_ProductNumber(IBAHolder ibaHolder) {
		return properties.getValueByKey(ibaHolder, "iba.internal.HHT_ProductNumber");
	}

	public static String getModelSpecifications(IBAHolder ibaHolder) {
		return properties.getValueByKey(ibaHolder, "iba.internal.ModelSpecifications");
	}

	public static String getBuy() {
		return properties.getValueByKey("source.buy");
	}

	public static String getFinishLibrary() {
		return properties.getValueByKey("finish.library.name");
	}

	public static String getElectricalLibrary() {
		return properties.getValueByKey("electrical.library.name");
	}

	public static String getStructureLibrary() {
		return properties.getValueByKey("structure.library.name");
	}

	public static String getPackagingLibrary() {
		return properties.getValueByKey("packaging.library.name");
	}

	public static void setHHT_MaterialGroup(IBAHolder ibaHolder, String str) {
		properties.setValueByKey(ibaHolder, "iba.internal.HHT_MaterialGroup", str);
	}

	public static void setHHT_ProductNumber(IBAHolder ibaHolder, String str) {
		properties.setValueByKey(ibaHolder, "iba.internal.HHT_ProductNumber", str);
	}

	public static void setHHT_ProjectNum(IBAHolder ibaHolder, String str) {
		properties.setValueByKey(ibaHolder, "iba.internal.HHT_ProjectNum", str);
	}
}
