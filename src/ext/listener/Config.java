package ext.listener;

import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;

public class Config {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	public static String getValueByKey(String key) {
		return properties.getValueByKey(key);
	}

	public static String getHHT_Classification(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Classification");
	}
}
