package ext.classification.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.CommonUtil;
import ext.ait.util.IBAUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ClassificationNumber {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("numberConfig.properties");

	/**
	 * 根据部件的分类执行不同的新编号生产规则后更新编号
	 * 
	 * @param part
	 */
	public static String process(WTPart part) {
		if (part == null) {
			return "";
		}
		String suffix = pUtil.getValueByKey("formal.number.suffix");
		String endItemTypeName = pUtil.getValueByKey("subType.internal.endItem");
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String buy = pUtil.getValueByKey("source.buy");
		String oldNumber = part.getNumber();
		String typeName = PersistenceUtil.getSubTypeInternal(part);
		String newNumber = "";
		WTPrincipal currentUser = null;

		if (typeName.equals(endItemTypeName)) {
			newNumber = getNewNumberForEndItem(part, classInternalName);
			// 对部件输出的新编码进行校验
			if (newNumber.length() == 15) {
				newNumber = part.getSource().toString().equals(buy) ? "6" + newNumber.substring(1) : newNumber;
			} else {
				return oldNumber + " 所生成的新编码不符合规范,且新编号为 " + newNumber + "\r\n";
			}
		} else {
			// 校验物料编号是否是已生成的编号
			int oldNumberLen = oldNumber.length();
			if (oldNumber.startsWith(classInternalName) && StringUtils.isAlpha(oldNumber.substring(oldNumberLen - 1))
					&& oldNumberLen == 10) {
				return "物料编号已经是系统生成的编号\r\n";
			} else {
				newNumber = getNewNumberForPart(part, classInternalName);
			}
		}
		// 更新部件编号代码
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			if (PartUtil.getWTPartByNumber(newNumber) != null) {
				return oldNumber + " 所生成的新编码不符合规范,且新编号为 " + newNumber + "\r\n 请检查是否存在相同的编号";
			}
			PartUtil.changePartNumber(part, newNumber);
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
	 * 生产成品部件的新编号 新编号=分类内部名称+品牌+产地+生产型号
	 * 
	 * @param part
	 * @return String 新编号
	 */
	private static String getNewNumberForEndItem(WTPart part, String classInternalName) {
		String pattern = pUtil.getValueByKey(classInternalName);
		pattern = StringUtils.isBlank(pattern) ? pUtil.getValueByKey("common.pattern") : pattern;
		return processParttern(pattern, part);
	}

	/**
	 * 生产部件的新编号 新编号=分类内部名称+流水序列号 流水序列号=同分类对应的现存最大值+1
	 * 
	 * @param part
	 * @return String 新编号
	 */
	private static String getNewNumberForPart(WTPart part, String classInternalName) {
		String suffix = pUtil.getValueByKey("formal.number.suffix");
		String maxNumberStr = "";
		List<String> numbers = Util.getPartNumbersByPrefix(classInternalName + "%");
		if (numbers.isEmpty()) {
			// 例如，返回一个默认值
			return classInternalName + "0001" + suffix;
		}
		numbers = cleanPartNumbers(numbers);
		// 使用内部类比较器来比较字符串
		Comparator<String> comparator = (s1, s2) -> {
			int num1 = Integer.parseInt(s1.replaceAll("\\D", ""));
			int num2 = Integer.parseInt(s2.replaceAll("\\D", ""));
			return Integer.compare(num1, num2);
		};
		// 使用自定义的比较器来查找最大值
		maxNumberStr = Collections.max(numbers, comparator);

		maxNumberStr = maxNumberStr.substring(maxNumberStr.length() - 5, maxNumberStr.length() - 1);
		maxNumberStr = maxNumberStr.isEmpty() ? "0" : String.valueOf(Integer.parseInt(maxNumberStr) + 1);
		maxNumberStr = String.format("%04d", Integer.parseInt(maxNumberStr));
		return classInternalName + maxNumberStr + suffix;
	}

	public static String processParttern(String pattern, WTPart part) {
		String result = "";
		try {
			String[] numberPattern = pattern.split(",");
			IBAUtil ibaUtil = new IBAUtil(part);
			Hashtable hashtable = ibaUtil.getAllIBAValues();
			Set set = hashtable.keySet();
			for (String word : numberPattern) {
				if (set.contains(word)) {
					String temp = ibaUtil.getIBAValue(word);
					if (word.equals("ProductModel") || word.equals("HHT_EncodingModel")) {
						temp = CommonUtil.addLead0(temp, 15 - result.length());
					}
					result += temp;
				} else {
					result += "";
				}
			}
			// 去除出现的 无
			while (result.contains("无")) {
				result = result.replace("无", "");
			}
			result = StringUtils.removeStart(result, "无");
			result = StringUtils.removeEnd(result, "无");
			// 去除掉多余的属性分隔符____
			while (result.contains("__")) {
				result = result.replace("__", "_");
			}
			result = StringUtils.removeStart(result, "_");
			result = StringUtils.removeEnd(result, "_");
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 对获取到指定分类的数据进行数据清理的方法
	 * 
	 * @param numbers
	 * @return
	 */
	private static List<String> cleanPartNumbers(List<String> numbers) {
		List<String> cleanedNumbers = new ArrayList<>();
		for (String number : numbers) {
			if (number.length() == 10 && StringUtils.isNumeric(number.substring(0, 9))) {
				cleanedNumbers.add(number);
			}
		}
		return cleanedNumbers;
	}
}