package ext.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ext.ait.util.CommonUtil;
import ext.classification.service.ClassificationDescription;
import ext.classification.service.ClassificationName;
import ext.classification.service.ClassificationNumber;
import wt.fc.WTObject;
import wt.part.WTPart;

public class ClassificationHelper {

	/**
	 * 根据分类对物料名称/编号/长描述进行修改的主方法
	 * 
	 * @param obj
	 */
	public static List<String> classify(WTObject obj, String type) {
		List<WTPart> list = CommonUtil.getListFromPBO(obj, WTPart.class);
		List<String> result = new ArrayList<>();
		switch (type) {
		case "number":
			result = list.stream().map(ClassificationNumber::process)
					.filter(processedString -> !processedString.isEmpty()).collect(Collectors.toList());
			break;
		case "name":
			result = list.stream().map(ClassificationName::process)
					.filter(processedString -> !processedString.isEmpty()).collect(Collectors.toList());
			break;
		case "description":
			result = list.stream().map(ClassificationDescription::process)
					.filter(processedString -> !processedString.isEmpty()).collect(Collectors.toList());
			break;
		default:
			List<String> desResult = list.stream().map(ClassificationDescription::process)
					.filter(processedString -> !processedString.isEmpty()).collect(Collectors.toList());
			List<String> nameResult = list.stream().map(ClassificationName::process)
					.filter(processedString -> !processedString.isEmpty()).collect(Collectors.toList());
			List<String> numberResult = list.stream().map(ClassificationNumber::process)
					.filter(processedString -> !processedString.isEmpty()).collect(Collectors.toList());
			result.addAll(desResult);
			result.addAll(nameResult);
			result.addAll(numberResult);
		}
		return result;
	}

}
