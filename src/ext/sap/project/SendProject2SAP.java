package ext.sap.project;

import java.util.ArrayList;
import java.util.List;

import ext.ait.util.CommonUtil;
import wt.fc.WTObject;
import wt.projmgmt.admin.Project2;
import wt.util.WTException;

public class SendProject2SAP {

	/**
	 * 向SAP发送BOM数据的主方法
	 * 
	 * @param WTObject obj
	 */
	public static List<String> sendProjectList2SAP(WTObject obj) {
		List<Project2> list = CommonUtil.getListFromPBO(obj, Project2.class);
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
			String result = SendProject2SAPService.sendProject2SAPUseUrl(json);
			msg.add(SendProject2SAPService.getResultFromJson(result));
		}
		return msg;
	}
}
