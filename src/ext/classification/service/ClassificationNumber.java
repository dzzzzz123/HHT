package ext.classification.service;

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
import wt.part.WTPart;
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

		if (typeName.equals(endItemTypeName)) {
			String newNumber = getNewNumberForEndItem(part, classInternalName);
			// 对部件输出的新编码进行校验
			if (newNumber.length() == 15) {
				newNumber = part.getSource().toString().equals(buy) ? "6" + newNumber.substring(1) : newNumber;
				try {
					PartUtil.changePartNumber(part, newNumber);
				} catch (Exception e) {
					e.printStackTrace();
					return oldNumber + " 所生成的新编码不符合规范,且新编号为 " + newNumber + "\r\n 请检查是否存在相同的编号";
				}
			} else {
				return oldNumber + " 所生成的新编码不符合规范,且新编号为 " + newNumber + "\r\n";
			}
		} else {
			// 校验物料编号是否是已生成的编号
			if (oldNumber.startsWith(classInternalName) && oldNumber.endsWith(suffix)) {
				return "物料编号已经是系统生成的编号\r\n";
			} else {
				PartUtil.changePartNumber(part, getNewNumberForPart(part, classInternalName));
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
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}
}