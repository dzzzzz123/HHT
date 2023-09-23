package ext.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ext.classification.service.ClassificationDescription;
import ext.classification.service.ClassificationName;
import ext.classification.service.ClassificationNumber;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.services.StandardManager;
import wt.util.WTException;

public class ClassificationHelper extends StandardManager {

	private static final long serialVersionUID = 1L;

	/**
	 * 根据分类对物料名称/编号/长描述进行修改的主方法
	 * 
	 * @param obj
	 */
	public static List<String> classify(WTObject obj, String type) {
		List<WTPart> list = getPartList(obj);
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

	/**
	 * 从工作流中获取物料列表
	 * 
	 * @param obj
	 * @return List<WTPart>
	 */
	private static List<WTPart> getPartList(WTObject obj) {
		List<WTPart> list = new ArrayList<>();
		try {
			if (obj instanceof WTPart) {
				list.add((WTPart) obj);
			} else if (obj instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) obj;
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (object instanceof WTPart) {
						list.add((WTPart) object);
					}
				}
			} else if (obj instanceof WTChangeOrder2) {
				WTChangeOrder2 co = (WTChangeOrder2) obj;
				QueryResult qr = ChangeHelper2.service.getChangeablesAfter(co);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (object instanceof WTPart) {
						list.add((WTPart) object);
					}
				}
			} else {
				System.out.println("不是部件，无法修改其名称/编号/描述");
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}
}
