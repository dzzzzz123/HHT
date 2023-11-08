package ext.classification.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;

public class ClassificationNumber {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("numberConfig.properties");

	/**
	 * 根据部件的分类执行不同的新编号生产规则后更新编号
	 * 
	 * @param part
	 */
	public static String process(WTPart part) {
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
				PartUtil.changePartNumber(part, newNumber);
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
		String Brand = pUtil.getValueByKey(part, "iba.internal.Brand");
		String Producer = pUtil.getValueByKey(part, "iba.internal.Producer");
		String ProductModel = pUtil.getValueByKey(part, "iba.internal.ProductModel");
		// 添加前导00000000
		ProductModel = CommonUtil.addLead0(ProductModel, 7);
		return classInternalName + Brand + Producer + ProductModel;
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
}