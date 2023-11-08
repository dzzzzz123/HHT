package ext.sap.masterData;

import java.util.ArrayList;
import java.util.List;

import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;

public class PartSenderHelper {

	/**
	 * 发送物料主数据到SAP的主方法
	 * 
	 * @param pbo
	 * @return
	 * @throws Exception
	 */
	public static List<String> sendParts2SAP(WTObject pbo) throws Exception {
		List<WTPart> list = CommonUtil.getListFromPBO(pbo, WTPart.class);
		List<String> msg = new ArrayList<>();
		for (WTPart part : list) {
			if (PersistenceUtil.isCheckOut(part)) {
				throw new WTException("该部件是检出状态");
			}
			SendSAPPartEntity entity = SendSAPService.SendSAPPart(part);
			String json = SendSAPService.entityToJson(entity);
			String result = SendSAPService.sendPartSAP(json);
			String SAPResult = SendSAPService.getResultFromJson(result);
			if (SAPResult.length() > 0) {
				msg.add(part.getNumber() + "未发送成功，错误信息为： " + SAPResult);
			}
		}
		return msg;
	}
}