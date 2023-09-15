package ext.sap.project;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import ext.ait.util.PropertiesUtil;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.projmgmt.admin.Project2;
import wt.services.StandardManager;
import wt.util.WTException;

public class SendProject2SAP extends StandardManager {
	private static final long serialVersionUID = 1L;
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	/**
	 * 向SAP发送BOM数据的主方法
	 * 
	 * @param WTObject obj
	 */
	public static void sendProjectList2SAP(WTObject obj) {
		List<Project2> list = processProjectList(obj);
		list.stream().map(SendProject2SAP::getProjectEntity).forEach(SendProject2SAPService::sendProject2SAP);
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
				QueryResult qr;
				qr = ChangeHelper2.service.getChangeablesAfter(co);
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

	/**
	 * 从WTPart中获取需要的数据并组装为BOMEntity
	 * 
	 * @param WTPart part
	 * @return BOMEntity
	 */
	public static ProjectEntity getProjectEntity(Project2 project) {
		ProjectEntity projectEntity = new ProjectEntity();
		projectEntity.setProjectNumber(project.getProjectNumber());
		projectEntity.setProjectName(project.getName());
		projectEntity.setProjectCategory(project.getCategory().getDisplay());
		projectEntity.setProjectOwner(project.getOwner().getName());
		projectEntity.setProjectCreateStamp(project.getCreateTimestamp().toString());
		Timestamp endTime = project.getEstimatedEndDate();
		if (endTime == null) {
			projectEntity.setProjectEndStamp("无结束日期");
		} else {
			projectEntity.setProjectEndStamp(endTime.toString());
		}
		projectEntity.setFactoryCode(project.getBusinessUnit());
		projectEntity.setDeleteFlag(properties.getValueByKey(project, "iba.internal.deleteFlag"));
		projectEntity.setProjectDescription(project.getDescription());
		projectEntity.setFinishFlag(project.getContainerTeamManagedInfo().getState().getDisplay());
		return projectEntity;
	}
}
