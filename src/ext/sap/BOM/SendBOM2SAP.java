package ext.sap.BOM;

import java.util.ArrayList;
import java.util.List;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import wt.fc.WTObject;
import wt.part.WTPart;

public class SendBOM2SAP {

	/**
	 * 向SAP发送BOM数据的主方法
	 * 
	 * @param WTObject obj
	 */
	public static List<String> sendListBOM2SAP(WTObject obj) {
		List<WTPart> list = CommonUtil.getListFromPBO(obj, WTPart.class);
		List<WTPart> listFiltered = new ArrayList<>();
		List<String> msg = new ArrayList<>();
		// 过滤部件，判断是否为BOM
		list.forEach(part -> {
			List<WTPart> BOMList = PartUtil.getBomByPart(part);
			if (BOMList.size() > 0) {
				listFiltered.add(part);
			}
		});
		// 从部件中获取BOM实体类，并逐个发送给SAP
		listFiltered.forEach(part -> {
			BOMEntity bomEntity = SendBOM2SAPService.getBOMEntity(part);
			String json = SendBOM2SAPService.getJsonByEntity(bomEntity);
			String result = SendBOM2SAPService.SendBOM2SAPUseUrl(json);
			msg.add(SendBOM2SAPService.getResultFromJson(result));
		});
		return msg;
	}
}
