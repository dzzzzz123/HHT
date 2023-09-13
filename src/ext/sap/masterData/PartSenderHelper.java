package ext.sap.masterData;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import ext.ait.util.PersistenceUtil;
import ext.sap.masterData.entity.SendSAPResult;
import ext.sap.masterData.service.SendSAPService;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.services.StandardManager;

public class PartSenderHelper extends StandardManager {

	private static final long serialVersionUID = 1L;

	/**
	 * 工作流中发送part到SAP
	 * 
	 */
	public static void sendPartsSAP(WTObject pbo) {

		try {
			sendPartsSAPMethodItem(pbo);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 工作流中发送part到SAP
	 * 
	 * 
	 * @throws Exception
	 */
	public static void sendPartsSAP2(WTObject pbo) throws Exception {

		sendPartsSAPMethodItem(pbo);

	}

	private static void sendPartsSAPMethodItem(WTObject pbo) throws Exception {
		List<SendSAPResult> list = new ArrayList();
		Gson gson = new Gson();
		if (pbo instanceof WTPart) {
			WTPart wtPart = (WTPart) pbo;
			if (PersistenceUtil.isCheckOut(wtPart)) {
				System.out.println("检出状态");
				throw new Exception("该部件是检出状态");
			}
			System.out.println("检入状态");
			SendSAPService.SendSAPPart(wtPart);
		} else {

			System.out.println("进入工作流发送到SAP");

			if (pbo instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) pbo;
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);

				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTPart) {
						WTPart wtPart = (WTPart) obj;
						String json = SendSAPService.SendSAPPart(wtPart);
						SendSAPResult sendSAPResult = gson.fromJson(json, SendSAPResult.class);
						list.add(sendSAPResult);
					}
				}
			}

			if (pbo instanceof WTChangeOrder2) {
				WTChangeOrder2 co = (WTChangeOrder2) pbo;
				QueryResult qr = ChangeHelper2.service.getChangeablesAfter(co);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTPart) {
						WTPart wtPart = (WTPart) obj;
						String json = SendSAPService.SendSAPPart(wtPart);
						SendSAPResult sendSAPResult = gson.fromJson(json, SendSAPResult.class);
						list.add(sendSAPResult);
					}
				}
			}

		}
	}

}
