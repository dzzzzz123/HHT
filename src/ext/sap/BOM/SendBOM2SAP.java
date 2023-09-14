package ext.sap.BOM;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;

import ext.ait.util.VersionUtil;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
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
		List<WTPart> listFiltered = filterBOM(list);
		listFiltered.stream().map(SendBOM2SAP::getBOMEntity).forEach(SendBOM2SAPService::SendBOM2SAP);
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

	/**
	 * 筛选出BOM来进行处理
	 * 
	 * @param List<WTPart> list
	 * @return List<WTPart>
	 */
	private static List<WTPart> filterBOM(List<WTPart> list) {
		return list.stream().filter(Util::verifyBOM).collect(Collectors.toList());
	}

	/**
	 * 从WTPart中获取需要的数据并组装为BOMEntity
	 * 
	 * @param WTPart part
	 * @return BOMEntity
	 */
	public static BOMEntity getBOMEntity(WTPart part) {
		BOMEntity bom = new BOMEntity();
		String stlan = Util.getStlan(part);
		List<BOMBodyEntity> body = Util.getBodyEntitiesByBOM(part);
		bom.setNumber(part.getNumber());
		bom.setName(part.getName());
		bom.setVersion(VersionUtil.getVersion(part));
		bom.setFactory(part.getViewName());
		String ECNnum = "";
		try {
			WTCollection conllection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(part);
			for (Object object : conllection) {
				if (object instanceof WTChangeOrder2) {
					WTChangeOrder2 wtChangeOrder2 = (WTChangeOrder2) object;
					ECNnum += wtChangeOrder2.getNumber();
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		bom.setECNNumber(ECNnum);
		bom.setStlan(stlan);
		bom.setBOMBody(body);
		return bom;
	}
}
