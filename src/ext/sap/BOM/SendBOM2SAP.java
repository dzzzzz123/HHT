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
		List<WTPart> listIncludeBOM = new ArrayList<>();
		List<WTPart> listFiltered = new ArrayList<>();
		List<String> msg = new ArrayList<>();
		// 将BOM和下层BOM都放入其中
		listIncludeBOM.addAll(list);
		list.forEach(part -> {
			listIncludeBOM.addAll(PartUtil.getAllBomByPart(part));
		});
		// 过滤部件，判断是否为BOM
		listIncludeBOM.stream().filter(SendBOM2SAP::checkBOM).forEach(listFiltered::add);
		// 从部件中获取BOM实体类，并逐个发送给SAP
		listFiltered.forEach(part -> {
			BOMEntity bomEntity = SendBOM2SAPService.getBOMEntity(part);
			String json = SendBOM2SAPService.getJsonByEntity(bomEntity);
			String result = SendBOM2SAPService.SendBOM2SAPUseUrl(json);
			msg.add(SendBOM2SAPService.getResultFromJson(result));
		});
		return msg;
	}

	/**
	 * 判断部件是否是BOM
	 * 
	 * @param part
	 * @return
	 */
	public static boolean checkBOM(WTPart part) {
		List<WTPart> BOMList = PartUtil.getBomByPart(part);
		return BOMList.size() > 0;
	}
}
