package ext.HHT.part.duplicateCheck;

import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;

public class Config {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

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

	public static String getHHT_Classification(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Classification");
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

	public static void setHHT_MaterialGroup(WTPart part, String str) {
		properties.setValueByKey(part, "iba.internal.HHT_MaterialGroup", str);
	}
}
