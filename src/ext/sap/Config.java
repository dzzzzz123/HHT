package ext.sap;

import ext.ait.util.ClassificationUtil;
import ext.ait.util.PropertiesUtil;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;

public class Config {

	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	public static String getValue(String key) {
		return properties.getValueByKey(key);
	}

	public static String getUsername() {
		return properties.getValueByKey("sap.username");
	}

	public static String getPassword() {
		return properties.getValueByKey("sap.password");
	}

	public static String getSupplyUrl() {
		return properties.getValueByKey("sap.supply.url");
	}

	public static String getProjectUrl() {
		return properties.getValueByKey("sap.project.url");
	}

	public static String getMasterDataUrl() {
		return properties.getValueByKey("sap.masterData.url");
	}

	public static String getBOMUrl() {
		return properties.getValueByKey("sap.bom.url");
	}

	public static String getHHT_BasicQuantity(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_BasicQuantity");
	}

	public static String getHHT_SubstituteGroup(WTPartUsageLink link) {
		return properties.getValueByKey(link, "iba.internal.HHT_SubstituteGroup");
	}

	public static String getHHT_Priority(WTPartUsageLink link) {
		return properties.getValueByKey(link, "iba.internal.HHT_Priority");
	}

	public static String getHHT_Strategies(WTPartUsageLink link) {
		return properties.getValueByKey(link, "iba.internal.HHT_Strategies");
	}

	public static String getHHT_UsagePossibility(WTPartUsageLink link) {
		return properties.getValueByKey(link, "iba.internal.HHT_UsagePossibility");
	}

	public static String getHHT_MatchGroup(WTPartUsageLink link) {
		return properties.getValueByKey(link, "iba.internal.HHT_MatchGroup");
	}

	public static String getHHT_Classification(WTPart part) {
		return ClassificationUtil.getClassificationInternal(part,
				properties.getValueByKey("iba.internal.HHT_Classification"));
	}

	public static String getHHT_LongtDescription(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_LongtDescription");
	}

	public static String getHHT_Bonded(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Bonded");
	}

	public static String getNonbondedNumber(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.NonbondedNumber");
	}

	public static String getHHT_GrossWeight(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_GrossWeight");
	}

	public static String getHHT_NetWeight(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_NetWeight");
	}

	public static String getHHT_WeightUnit(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_WeightUnit");
	}

	public static String getHHT_Traffic(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Traffic");
	}

	public static String getHHT_VolumeUnit(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_VolumeUnit");
	}

	public static String getHHT_Length(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Length");
	}

	public static String getHHT_Width(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Width");
	}

	public static String getHHT_Height(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Height");
	}

	public static String getHHT_SizeUnits(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_SizeUnits");
	}

	public static String getHHT_ClassificationCode(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ClassificationCode");
	}

	public static String getHHT_ProductLineNumber(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ProductLineNumber");
	}

	public static String getHHT_ProductLineName(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ProductLineName");
	}

	public static String getHHT_ProductNumber(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ProductNumber");
	}

	public static String getHHT_Productdescription(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Productdescription");
	}

	public static String getHHT_ModelSpecification(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ModelSpecification");
	}

	public static String getHHT_CommodityName(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_CommodityName");
	}

	public static String getHHT_Brand(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Brand");
	}

	public static String getHHT_Year(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Year");
	}

	public static String getLargeScreenSize(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.LargeScreenSize");
	}

	public static String getHHT_FinishedSeries(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_FinishedSeries");
	}

	public static String getHHT_Industry(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Industry");
	}

	public static String getHHT_ProductDevelopmentType(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ProductDevelopmentType");
	}

	public static String getHHT_CustomizedProductIdentifier(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_CustomizedProductIdentifier");
	}

	public static String getHHT_SupplierSku(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_SupplierSku");
	}

	public static String getDefaultTraceCode(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.DefaultTraceCode");
	}

	public static String getHHT_Factory(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Factory");
	}

	public static String getHHT_Price(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Price");
	}

	public static String getHHT_PriceUnit(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_PriceUnit");
	}

	public static String getHHT_INValue(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_INValue");
	}

	public static String getHHT_SerialNumber(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_SerialNumber");
	}

	public static String getHHT_ProjectNum(IBAHolder ibaHolder) {
		return properties.getValueByKey(ibaHolder, "iba.internal.HHT_ProjectNum");
	}

	public static void setHHT_Price(WTPart part, String IBAValue) {
		properties.setValueByKey(part, "iba.internal.HHT_Price", IBAValue);
	}

	public static void setHHT_PriceUnit(WTPart part, String IBAValue) {
		properties.setValueByKey(part, "iba.internal.HHT_PriceUnit", IBAValue);
	}

	public static String getSourceBuy() {
		return properties.getValueByKey("source.buy");
	}

	public static String getJsonVar() {
		return properties.getValueByKey("masterData.json.var");
	}

}
