package ext.plm.work;

import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.work.NmWorkItemCommands;

import wt.change2.WTChangeActivity2;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

public class CustNmWorkItemCommands extends NmWorkItemCommands {

	public static FormResult save(NmCommandBean nmCommandBean) throws WTException {
		completeOrSave(nmCommandBean);
		FormResult formResult = NmWorkItemCommands.save(nmCommandBean);
		return formResult;
	}

	public static FormResult complete(NmCommandBean nmCommandBean) throws WTException {
		System.out.println("333333333");
		FormResult formResult = new FormResult();
		completeOrSave(nmCommandBean);
		System.out.println("44444444");
		formResult = NmWorkItemCommands.complete(nmCommandBean);
		return formResult;
	}

	/**
	 * 保存or完成任务时候，保存ECA更改单内容
	 * 
	 * @param nmCommandBean
	 * @param formResult
	 * @throws WTException
	 */
	private static void completeOrSave(NmCommandBean nmCommandBean) throws WTException {
		WTChangeActivity2 eca = null;
		NmOid nmoid = nmCommandBean.getActionOid();
		Object obj = nmoid.getRef();
		if (obj instanceof WorkItem) {
			WorkItem workitem = (WorkItem) obj;
			System.out.println(">>>>>>>>>>>>>>>>completeOrSave workitem：" + workitem);
			Object pbo = workitem.getPrimaryBusinessObject().getObject();
			if (pbo instanceof WTChangeActivity2) {
				eca = (WTChangeActivity2) pbo;
			}
		}
		if (eca != null) {
			ext.plm.change.ChangeUtil.saveECAMess(nmCommandBean, eca);
		}
	}

}
