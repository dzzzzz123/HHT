package ext.HHT.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.IBAUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import ext.signature.PropertiesHelper;
import wt.change2.ChangeActivity2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeReview;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.util.WTException;

public class VerifyPartHelper implements RemoteAccess {

//	private static String configPartAttribute = "Part_IBA_Attributes";
//	private static String configPartBasicAttribute = "Part_Basic_Attributes";

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	private static String defaultPartType = "Default_Part_Type";

	/**
	 * 工作流表达式入口
	 * 
	 * @throws WTException
	 * @throws MaturityException
	 */
	public static void verifyPart(WTObject obj) throws Exception {
		System.out.println("开始校验WTPart属性空值");
		String result = null;
		if (obj instanceof PromotionNotice) {
			QueryResult qr = MaturityHelper.service.getPromotionTargets((PromotionNotice) obj);
			result = findNullAttributeResult(qr, "升级请求");
		} else if (obj instanceof ChangeActivity2) {
			QueryResult qr = ChangeHelper2.service.getChangeablesAfter((ChangeActivity2) obj);
			result = findNullAttributeResult(qr, "更改活动");
		} else if (obj instanceof WTChangeReview) {
			QueryResult qr = ChangeHelper2.service.getChangeables((WTChangeReview) obj);
			result = findNullAttributeResult(qr, "更改审阅");
		}
		System.out.println(result);
		if (StringUtils.isNotBlank(result)) {
			throw new WTException(result);

		}
	}

	/**
	 * 通过流程对象查询空值
	 * 
	 * @param qr
	 * @param workflowType
	 * @return
	 * @throws Exception
	 */
	private static String findNullAttributeResult(QueryResult qr, String workflowType) throws Exception {
		Map<String, List<String>> nullAttributesMap = new HashMap<>();
		if (qr == null)
			return null;
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				List<String> nullAttributes = findNullAttributeByPart(part);
				if (nullAttributes.isEmpty())
					continue;
				nullAttributesMap.put(part.getNumber(), nullAttributes);
			}
		}
		return returnResult(nullAttributesMap, workflowType);
	}

	/**
	 * 通过part查询空值
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	private static List<String> findNullAttributeByPart(WTPart part) throws Exception {
		IBAUtil ibaUtil = new IBAUtil((IBAHolder) part);

		String partType = PersistenceUtil.getSubTypeInternal(part);
		List<String> nullAttributes = new ArrayList<>();
		String attrs = pUtil.getValueByKey(partType);
		if (StringUtils.isBlank(attrs)) {
			String defaultType = pUtil.getValueByKey(defaultPartType);
			attrs = PropertiesHelper.getStrFromProperties(defaultType);
		}

		String[] attrArray = attrs.split(",");
		for (String ibaName : attrArray) {
			if (StringUtils.isBlank(ibaUtil.getIBAValue(ibaName))) {
				nullAttributes.add(ibaName);
			}
		}
		return nullAttributes;
	}

	/**
	 * 将查询结果包装为字符串返回
	 * 
	 * @param nullAttributesMap
	 * @param workflowType
	 * @return
	 */
	private static String returnResult(Map<String, List<String>> nullAttributesMap, String workflowType) {
		if (nullAttributesMap.isEmpty()) {
			return null;
		}
		StringBuilder result = new StringBuilder("部件空值属性 " + workflowType + "校验如下：  \n");
		nullAttributesMap.forEach((key, value) -> {
			StringBuilder attrList = new StringBuilder();
			value.forEach((attribute) -> {
				attrList.append(attribute + ",");
			});
			result.append("部件编号" + key + "：" + attrList.substring(0, attrList.length() - 1) + "；\n");
		});
		result.append("属性值为空，请修改后再提交任务！");

		return result.toString();

	}
}
