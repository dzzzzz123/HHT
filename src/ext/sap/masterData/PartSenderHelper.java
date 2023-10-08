package ext.sap.masterData;

import java.util.ArrayList;
import java.util.List;

import ext.ait.util.PersistenceUtil;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;

public class PartSenderHelper {

	/**
	 * 发送物料主数据到SAP的主方法
	 * 
	 * @param pbo
	 * @return
	 * @throws Exception
	 */
	public static List<String> sendParts2SAP(WTObject pbo) throws Exception {
		List<WTPart> list = getPartsByPbo(pbo);
		List<String> msg = new ArrayList<>();
		list.forEach(part -> {
			SendSAPPartEntity entity = SendSAPService.SendSAPPart(part);
			String json = SendSAPService.entityToJson(entity);
			System.out.println("json" + json);
			String result = SendSAPService.sendPartSAP(json);
			System.out.println("result" + result);
			msg.add(SendSAPService.getResultFromJson(result));
		});
		return msg;
	}

	public static List<WTPart> getPartsByPbo(WTObject pbo) throws Exception {
		List<WTPart> list = new ArrayList<>();
		if (pbo instanceof WTPart) {
			WTPart wtPart = (WTPart) pbo;
			if (PersistenceUtil.isCheckOut(wtPart)) {
				throw new Exception("该部件是检出状态");
			}
			list.add(wtPart);
		} else if (pbo instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) pbo;
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart wtPart = (WTPart) obj;
					list.add(wtPart);
				}
			}
		} else if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 co = (WTChangeOrder2) pbo;
			QueryResult qr = ChangeHelper2.service.getChangeablesAfter(co);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart wtPart = (WTPart) obj;
					list.add(wtPart);
				}
			}
		} else {
			System.out.println("传入的不是部件！");
		}
		return list;
	}
}