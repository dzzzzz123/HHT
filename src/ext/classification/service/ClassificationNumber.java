package ext.classification.service;

import ext.ait.util.PartUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;

public class ClassificationNumber {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	public static void process(WTPart part) {
		String newNumber = getNewNumberByClassification(part);
		PartUtil.changePartNumber(part, newNumber);
	}

	private static String getNewNumberByClassification(WTPart part) {
		String newNumber = part.getNumber();

		return newNumber;
	}

}
