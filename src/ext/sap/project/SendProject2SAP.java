package ext.sap.project;

import java.util.ArrayList;
import java.util.List;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.projmgmt.admin.Project2;
import wt.util.WTException;

public class SendProject2SAP {

	/**
	 * 向SAP发送BOM数据的主方法
	 * 
	 * @param WTObject obj
	 */
	public static List<String> sendProjectList2SAP(WTObject obj) {
		List<Project2> list = processProjectList(obj);
		List<String> msg = new ArrayList<>();
		for (Project2 project : list) {
			ProjectEntity projectEntity = null;
			try {
				projectEntity = SendProject2SAPService.getProjectEntity(project);
			} catch (WTException e) {
				msg.add("预估结束日期未输入，请输入！");
				return msg;
			}
			String json = SendProject2SAPService.getJsonByEntity(projectEntity);
			System.out.println(json);
			String result = SendProject2SAPService.sendProject2SAPUseUrl(json);
			System.out.println(result);
			msg.add(SendProject2SAPService.getResultFromJson(result));
		}
		return msg;
	}

	/**
	 * 将传入的WTObject解析为方便处理的List<Project2>
	 * 
	 * @param obj
	 * @return List<Project2>
	 */
	private static List<Project2> processProjectList(WTObject obj) {
		List<Project2> list = new ArrayList<>();
		try {
			if (obj instanceof Project2) {
				list.add((Project2) obj);
			} else if (obj instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) obj;
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (object instanceof Project2) {
						list.add((Project2) object);
					}
				}
			} else if (obj instanceof WTChangeOrder2) {
				WTChangeOrder2 co = (WTChangeOrder2) obj;
				QueryResult qr = ChangeHelper2.service.getChangeablesAfter(co);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (object instanceof Project2) {
						list.add((Project2) object);
					}
				}
			} else {
				System.out.println("不是项目，无法发送给SAP");
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

}
