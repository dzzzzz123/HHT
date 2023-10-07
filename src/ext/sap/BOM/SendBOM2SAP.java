package ext.sap.BOM;

import java.util.ArrayList;
import java.util.List;

import ext.ait.util.PartUtil;
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

public class SendBOM2SAP extends StandardManager {

	private static final long serialVersionUID = 1L;

	/**
	 * 向SAP发送BOM数据的主方法
	 * 
	 * @param WTObject obj
	 */
	public static void sendListBOM2SAP(WTObject obj) {
		List<WTPart> list = processWTPartList(obj);
		List<WTPart> listFiltered = new ArrayList<>();
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
			System.out.println(json);
			String result = SendBOM2SAPService.SendBOM2SAPUseUrl(json);
			System.out.println(result);
		});
	}

	/**
	 * 将传入的WTObject解析为方便处理的List<WTPart>
	 * 
	 * @param WTObject obj
	 * @return List<WTPart>
	 */
	private static List<WTPart> processWTPartList(WTObject obj) {
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
				QueryResult qr;
				qr = ChangeHelper2.service.getChangeablesAfter(co);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (object instanceof WTPart) {
						list.add((WTPart) object);
					}
				}
			} else {
				System.out.println("不是BOM，无法发送给SAP");
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

}
