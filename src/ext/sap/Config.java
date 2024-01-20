package ext.sap;

import org.apache.commons.lang3.StringUtils;

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

	public static String getHHT_LongtDescription(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_LongtDescription");
	}

	public static String getHHT_Bonded(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Bonded");
	}

	public static String getNonbondedNumber(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.NonbondedNumber");
	}

	public static String getHHT_GrossWeight(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_GrossWeight");
	}

	public static String getHHT_NetWeight(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_NetWeight");
	}

	public static String getHHT_WeightUnit(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_WeightUnit");
	}

	public static String getHHT_Traffic(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Traffic");
	}

	public static String getHHT_VolumeUnit(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_VolumeUnit");
	}

	public static String getHHT_Length(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Length");
	}

	public static String getHHT_Width(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Width");
	}

	public static String getHHT_Height(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Height");
	}

	public static String getHHT_SizeUnits(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_SizeUnits");
	}

	public static String getHHT_ClassificationCode(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ClassificationCode");
	}

	public static String getHHT_ProductLineNumber(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ProductLineNumber");
	}

	public static String getHHT_ProductLineName(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ProductLineName");
	}

	public static String getHHT_ProductNumber(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ProductNumber");
	}

	public static String getHHT_Productdescription(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Productdescription");
	}

	public static String getHHT_ModelSpecification(IBAHolder part) {
		String HHT_ModelSpecification = properties.getValueByKey(part, "iba.internal.HHT_ModelSpecification");
		String ModelSpecifications = properties.getValueByKey(part, "iba.internal.ModelSpecifications");
		if (StringUtils.isNotBlank(ModelSpecifications)) {
			return ModelSpecifications;
		} else if (StringUtils.isNotBlank(HHT_ModelSpecification)) {
			return HHT_ModelSpecification;
		} else {
			return "";
		}
	}

	public static String getHHT_CommodityName(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_CommodityName");
	}

	public static String getHHT_Brand(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Brand");
	}

	public static String getHHT_Year(IBAHolder part) {
		String HHT_Year = properties.getValueByKey(part, "iba.internal.HHT_Year");
		String Time = properties.getValueByKey(part, "iba.internal.Time");
		if (StringUtils.isNotBlank(Time)) {
			return Time;
		} else if (StringUtils.isNotBlank(HHT_Year)) {
			return HHT_Year;
		} else {
			return "";
		}
	}

	public static String getLargeScreenSize(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.LargeScreenSize");
	}

	public static String getHHT_FinishedSeries(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_FinishedSeries");
	}

	public static String getHHT_Industry(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Industry");
	}

	public static String getHHT_ProductDevelopmentType(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_ProductDevelopmentType");
	}

	public static String getHHT_CustomizedProductIdentifier(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_CustomizedProductIdentifier");
	}

	public static String getHHT_SupplierSku(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_SupplierSku");
	}

	public static String getDefaultTraceCode(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.DefaultTraceCode");
	}

	public static String getHHT_Factory(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Factory");
	}

	public static String getHHT_Price(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Price");
	}

	public static String getHHT_PriceUnit(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_PriceUnit");
	}

	public static String getHHT_INValue(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_INValue");
	}

	public static String getHHT_PDOwnership(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_PDOwnership");
	}

	public static String getHHT_SerialNumber(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_SerialNumber");
	}

	public static void setHHT_Price(IBAHolder part, String IBAValue) {
		properties.setValueByKey(part, "iba.internal.HHT_Price", IBAValue);
	}

	public static void setHHT_PriceUnit(IBAHolder part, String IBAValue) {
		properties.setValueByKey(part, "iba.internal.HHT_PriceUnit", IBAValue);
	}

	public static void setHHT_Brand(IBAHolder part, String IBAValue) {
		properties.setValueByKey(part, "iba.internal.HHT_Brand", IBAValue);
	}

	public static String getSourceBuy() {
		return properties.getValueByKey("source.buy");
	}

	public static String getJsonVar() {
		return properties.getValueByKey("masterData.json.var");
	}

	public static String getBOMJsonVar() {
		return properties.getValueByKey("BOM.json.var");
	}

	public static String getHHT_ProjectNum(IBAHolder project) {
		return properties.getValueByKey(project, "iba.internal.HHT_ProjectNum");
	}

	public static String getHHT_SapMark(IBAHolder part) {
		return properties.getValueByKey(part, "iba.internal.HHT_SapMark");
	}

	public static void setHHT_SapMark(WTPart part, String IBAValue) {
		properties.setValueByKey(part, "iba.internal.HHT_SapMark", IBAValue);
	}

}
