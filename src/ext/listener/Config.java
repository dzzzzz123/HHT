package ext.listener;

import ext.ait.util.PropertiesUtil;
import wt.doc.WTDocument;
import wt.part.WTPart;

public class Config {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	public static String getValueByKey(String key) {
		return properties.getValueByKey(key);
	}

	public static String getHHT_Classification(WTPart part) {
		return properties.getValueByKey(part, "iba.internal.HHT_Classification");
	}

	public static String getHHT_PartNumber(WTDocument document) {
		return properties.getValueByKey(document, "iba.internal.HHT_PartNumber");
	}

	public static String getLFName() {
		return properties.getValueByKey("lifeCycle.templateName");
	}

	public static String getORGID() {
		return properties.getValueByKey("ORG.IDA2A2");
	}

	public static String getPESType() {
		return properties.getValueByKey("PurchasedEnd.SubType");
	}

	public static String getBuy() {
		return properties.getValueByKey("source.buy");
	}

}
