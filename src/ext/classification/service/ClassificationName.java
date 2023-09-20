package ext.classification.service;

import ext.ait.util.PartUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;

/**
 * 根据部件的分类属性的内部名称来获取新的物料名称并设置值
 * 
 * @author dz
 *
 */
public class ClassificationName {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("nameConfig.properties");

	public static void process(WTPart part) {
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String partten = pUtil.getValueByKey(classInternalName);
		String newName = Util.processPartten(partten, part);
		PartUtil.changePartName(part, newName);
	}
}
