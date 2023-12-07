package ext.HHT.part.duplicateCheck.service;

import java.util.List;

import ext.HHT.Config;
import ext.ait.util.ClassificationUtil;
import wt.part.WTPart;

public class MaterialNumberService {

	public static String process(WTPart part) {
		String result = "";
		if (Config.getHHT_Classification(part).startsWith("5")) {
			String HHT_Factory = Config.getHHT_Factory(part);
			String ModelSpecifications = Config.getModelSpecifications(part);
			if (!HHT_Factory.equals("D")) {
				List<WTPart> list = ClassificationUtil.getClassPart(Config.getHHT_Classification(),
						Config.getHHT_Classification(part));
				for (WTPart wtPart : list) {
					String HHT_Factory2 = Config.getHHT_ProductNumber(wtPart);
					String ModelSpecifications2 = Config.getModelSpecifications(wtPart);
					if (HHT_Factory2.equals("D") && ModelSpecifications.equals(ModelSpecifications2)) {
						Config.setHHT_ProductNumber(part, HHT_Factory2);
						break;
					}
				}
			}
		}
		return result;
	}

}
