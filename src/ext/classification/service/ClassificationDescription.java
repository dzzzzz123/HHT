package ext.classification.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ext.ait.util.ClassificationUtil;
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

	public static String process(WTPart part) {
		String result = "";
		String classInternalName = ClassificationUtil.getClassificationInternal(part,
				pUtil.getValueByKey("iba.internal.HHT_Classification"));
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
		String partten = pUtil.getValueByKey(classification);
		List<String> parttens = Util.extractParttens(partten);
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
}
