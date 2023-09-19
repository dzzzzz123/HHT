package ext.classification.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

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
	public static void process(WTPart part) {
		String typeName = PersistenceUtil.getSubTypeInternal(part);
		String endItemTypeName = pUtil.getValueByKey("subType.internal.endItem");
		String newNumber = "";
		if (typeName.equals(endItemTypeName)) {
			newNumber = getNewNumberForEndItem(part);
		} else {
			newNumber = getNewNumberForPart(part);
		}
		System.out.println("oldNumber" + part.getNumber());
		System.out.println("newNumber" + newNumber);
		PartUtil.changePartNumber(part, newNumber);
	}

	/**
	 * 生产成品部件的新编号 新编号=分类内部名称+品牌+产地+生产型号
	 * 
	 * @param part
	 * @return String 新编号
	 */
	private static String getNewNumberForEndItem(WTPart part) {
		String oldNumber = part.getNumber();
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
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
		String newNumber = classInternalName + Brand + Producer + ProductModel;

		if (newNumber.length() == 15) {
			return newNumber;
		}
		System.out.println("生成的新编码不符合规范为：" + newNumber);
		return oldNumber;
	}

	/**
	 * 生产部件的新编号 新编号=分类内部名称+流水序列号 流水序列号=同分类对应的现存最大值+1
	 * 
	 * @param part
	 * @return String 新编号
	 */
	private static String getNewNumberForPart(WTPart part) {
		String classInternalName = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String prefix = pUtil.getValueByKey("temporary.number.prefix");
		String suffix = pUtil.getValueByKey("formal.number.suffix");
		String oldNumber = part.getNumber();
		int serialNumber = 0;
		String serialNumberStr = "";
		String newNumber = "";
		if (oldNumber.startsWith(prefix)) {
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
			newNumber = classInternalName + serialNumberStr + suffix;
			return newNumber;
		} else {
			return oldNumber;
		}
	}
}