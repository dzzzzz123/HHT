package ext.classification.service;

import ext.ait.util.IBAUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;
import wt.util.WTException;

public class ClassificationDescription {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	public static void process(WTPart part) {
		String newDescription = getNewDescriptionByClassification(part);
		changeDescription(part, newDescription);
	}

	private static String getNewDescriptionByClassification(WTPart part) {
		String newDescription = pUtil.getValueByKey(part, "iba.internal.HHT_LongtDescription");

		return newDescription;
	}

	private static void changeDescription(WTPart part, String newDescription) {
		try {
			IBAUtil ibaUtil = new IBAUtil(part);
			ibaUtil.setIBAAttribute4AllType(part, "String", newDescription);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
