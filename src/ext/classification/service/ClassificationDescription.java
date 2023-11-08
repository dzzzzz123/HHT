package ext.classification.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ext.HHT.part.duplicateCheck.DuplicateCheckHelper;
import ext.ait.util.PartUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import wt.fc.WTObject;
import wt.part.WTPart;

/**
 * 根据部件的分类属性的内部名称来获取新的物料描述并设置值
 * 
 * @author dz
 *
 */
public class ClassificationDescription {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("descriptionConfig.properties");

	public static String process(WTPart part) {
		String result = "";
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String pattern = pUtil.getValueByKey(classInternalName);
		if (StringUtils.isBlank(pattern)) {
			return "当前分类 " + classInternalName + " 在配置文件中不存在!\r\n";
		}
		String newDescription = Util.processPartten(pattern, part);
		pUtil.setValueByKey(part, "iba.internal.HHT_LongtDescription", newDescription);
		return result;
	}

	/**
	 * 在没有part对象的时候生成物料长描述
	 * 
	 * @param map            参数列表
	 * @param classification 分类内部名称
	 * @return 生成的物料长描述
	 */
	public static String process(Map<String, Object> map, String classification) {
		String result = "";
		String pattern = pUtil.getValueByKey(classification);
		if (StringUtils.isBlank(pattern)) {
			return "当前分类 " + classification + " 在配置文件中不存在!\r\n";
		}
		List<String> parttens = Util.extractParttens(pattern);
		Set<String> keySet = map.keySet(); // 获取键集合一次

		for (String str1 : parttens) {
			if (str1.startsWith("[")) {
				result += str1.substring(1);
			} else {
				String flag = "";
				for (String str2 : keySet) {
					if (str2.contains(str1) && !str2.endsWith("old")) {
						Object value = map.get(str2);
						flag = value instanceof String[] ? ((String[]) value)[0] : value.toString();
						break;
					}
				}
				result += flag.length() > 0 ? flag : "";
			}
		}
		return result;
	}

	public static String process(WTObject obj) {
		List<WTPart> partList = PartUtil.getPartList(obj);
		String result = "";
		for (WTPart part : partList) {
			String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
			String pattern = pUtil.getValueByKey(classInternalName);
			if (StringUtils.isBlank(pattern)) {
				return "当前分类 " + classInternalName + " 在配置文件中不存在!\r\n";
			}
			String newDescription = Util.processPartten(pattern, part);
			Map<String, String> map = DuplicateCheckHelper.getDescriptionByClass(classInternalName);
			for (String key : map.keySet()) {
				if (key.equals(newDescription)) {
					String oid = map.get(key);
					WTPart samePart = (WTPart) PersistenceUtil.oid2Object(oid);
					result += part.getNumber() + "与当前物料系统中编号为 " + samePart.getNumber() + " 的物料属性值相同！\r\n";
				}
			}
		}
		return result;
	}
}
