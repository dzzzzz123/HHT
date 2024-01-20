package ext.classification.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ext.HHT.part.duplicateCheck.service.DuplicateCheckService;
import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import wt.fc.WTObject;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
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
		WTPrincipal currentUser = null;
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String pattern = pUtil.getValueByKey(classInternalName);
		if (StringUtils.isBlank(pattern)) {
			return "当前分类 " + classInternalName + " 在配置文件中不存在!\r\n";
		}
		String newDescription = Util.processPartten(pattern, part);
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			pUtil.setValueByKey(part, "iba.internal.HHT_LongtDescription", newDescription);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (currentUser != null) {
				try {
					SessionHelper.manager.setPrincipal(currentUser.getName());
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
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
		// 去除出现的 无
		char[] charArray = result.toCharArray();
		Set<Integer> removeIndex = new HashSet<>();
		int charArrayLen = charArray.length;
		char wu = '无';
		if (charArrayLen > 2) {
			for (int i = 0; i < charArrayLen; i++) {
				char currentChar = charArray[i];
				boolean flag = wu == currentChar;
				if (flag) {
					if (i == 0) {
						if (!checkHan(charArray[i + 1])) {
							removeIndex.add(i);
						}
					} else if (i == charArrayLen - 1) {
						if (!checkHan(charArray[i - 1])) {
							removeIndex.add(i);
						}
					} else {
						if (!checkHan(charArray[i - 1]) && !checkHan(charArray[i + 1])) {
							removeIndex.add(i);
						}
					}
				}
			}
		}
		result = removeList(charArray, removeIndex);
		// 去除掉多余的属性分隔符____
		while (result.contains("__")) {
			result = result.replace("__", "_");
		}
		result = StringUtils.removeStart(result, "_");
		result = StringUtils.removeEnd(result, "_");
		return result;
	}

	public static String process(WTObject obj) {
		List<WTPart> partList = CommonUtil.getListFromPBO(obj, WTPart.class);
		for (WTPart part : partList) {
			String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
			String pattern = pUtil.getValueByKey(classInternalName);
			if (StringUtils.isBlank(pattern)) {
				return "当前分类 " + classInternalName + " 在配置文件中不存在!\r\n";
			}
			String newDescription = Util.processPartten(pattern, part);
			Map<String, String> map = DuplicateCheckService.getDescriptionByClass(classInternalName);
			for (String key : map.keySet()) {
				if (key.equals(newDescription)) {
					String oid = map.get(key);
					WTPart samePart = (WTPart) PersistenceUtil.oid2Object(oid);
					return part.getNumber() + "与当前物料系统中编号为 " + samePart.getNumber() + " 的物料属性值相同！\r\n";
				}
			}
		}
		return "";
	}

	public static boolean checkHan(char c) {
		return Character.toString(c).matches("[\\u4e00-\\u9fa5a-zA-Z]");
	}

	public static String removeList(char[] array1, Set<Integer> removeIndex) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < array1.length; i++) {
			// 判断当前索引是否在数字数组中
			if (!removeIndex.contains(i)) {
				result.append(array1[i]);
			}
		}
		return result.toString();
	}
}
