package ext.classification.service;

import ext.ait.util.IBAUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;
import wt.util.WTException;

/**
 * 根据部件的分类属性的内部名称来获取新的物料描述并设置值
 * 
 * @author dz
 *
 */
public class ClassificationDescription {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("descriptionConfig.properties");

	public static void process(WTPart part) {
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String partten = pUtil.getValueByKey(classInternalName);
		String newDescription = Util.processPartten(partten, part);
		try {
			IBAUtil ibaUtil = new IBAUtil(part);
			ibaUtil.setIBAAttribute4AllType(part, pUtil.getValueByKey("iba.internal.HHT_LongtDescription"),
					newDescription);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
