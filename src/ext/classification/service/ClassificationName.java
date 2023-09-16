package ext.classification.service;

import ext.ait.util.PartUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;

public class ClassificationName {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	public static void process(WTPart part) {
		String newName = getNewNameByClassification(part);
		PartUtil.changePartName(part, newName);
	}

	private static String getNewNameByClassification(WTPart part) {
		String newName = part.getName();

		return newName;
	}
}
