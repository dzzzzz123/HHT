package ext.classification.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import ext.ait.util.ClassificationUtil;
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
		String result = "";
		String prefix = pUtil.getValueByKey("temporary.number.prefix");
		String typeName = PersistenceUtil.getSubTypeInternal(part);
		String endItemTypeName = pUtil.getValueByKey("subType.internal.endItem");
		String oldNumber = part.getNumber();
		String newNumber = "";
		if (typeName.equals(endItemTypeName)) {
			newNumber = getNewNumberForEndItem(part);
			// 对部件输出的新编码进行校验
			if (newNumber.length() != 15) {
				newNumber = oldNumber;
				result = oldNumber + " 所生成的新编码不符合规范为 " + newNumber;
			}
		} else {
			// 对成品部件原编码的前缀进行判断
			if (oldNumber.startsWith(prefix)) {
				newNumber = getNewNumberForPart(part);
			} else {
				result = oldNumber + " 的前缀不符合规范";
			}
		}
		PartUtil.changePartNumber(part, newNumber);
		return result;
	}

	/**
	 * 生产成品部件的新编号 新编号=分类内部名称+品牌+产地+生产型号
	 * 
	 * @param part
	 * @return String 新编号
	 */
	private static String getNewNumberForEndItem(WTPart part) {
		String classInternalName = ClassificationUtil.getClassificationInternal(part,
				pUtil.getValueByKey("iba.internal.HHT_Classification"));
		String Brand = pUtil.getValueByKey(part, "iba.internal.Brand");
		String Producer = pUtil.getValueByKey(part, "iba.internal.Producer");
		String ProductModel = pUtil.getValueByKey(part, "iba.internal.ProductModel");
		// 添加前导0000
		if (ProductModel.length() < 7) {
			StringBuilder paddedProductModel = new StringBuilder(ProductModel);
			while (paddedProductModel.length() < 7) {
				paddedProductModel.insert(0, '0'); // 在前面添加零
			}
			ProductModel = paddedProductModel.toString();
		}

		return classInternalName + Brand + Producer + ProductModel;
	}

	/**
	 * 生产部件的新编号 新编号=分类内部名称+流水序列号 流水序列号=同分类对应的现存最大值+1
	 * 
	 * @param part
	 * @return String 新编号
	 */
	private static String getNewNumberForPart(WTPart part) {
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String suffix = pUtil.getValueByKey("formal.number.suffix");
		int serialNumber = 0;
		String serialNumberStr = "";
		List<String> numbers = Util.getPartNumbersByPrefix(classInternalName);

		// 流示获取最大值
		Optional<String> maxSerialNumber = numbers.stream().max(String::compareTo);

		if (maxSerialNumber.isPresent()) {
			serialNumber = Integer.parseInt(maxSerialNumber.get()) + 1;
		} else {
			serialNumber = 1;
		}
		// 添加前导0000
		DecimalFormat decimalFormat = new DecimalFormat("0000");
		serialNumberStr = decimalFormat.format(serialNumber);
		return classInternalName + serialNumberStr + suffix;
	}
}