package ext.classification.service;

import org.apache.commons.lang3.StringUtils;

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

	public static String process(WTPart part) {
		String result = "";
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String pattern = pUtil.getValueByKey(classInternalName);
		if (StringUtils.isBlank(pattern)) {
			return "当前分类 " + classInternalName + " 在配置文件中不存在!\r\n";
		}
		String newName = Util.processPartten(pattern, part);
		PartUtil.changePartName(part, newName);
		return result;
	}
}
