package ext.plm.change;

import java.util.List;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.QueryResult;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

/**
 * ECA 操作生成更改通知单
 * 
 */
public class CustECAProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> paramList) throws WTException {
		WTPrincipal principal = null;
		try { 
			NmOid primaryOid = nmcommandbean.getActionOid();
			WTChangeActivity2 eca = null;
			if (primaryOid.getRefObject() instanceof WTChangeActivity2) {
				eca = (WTChangeActivity2) primaryOid.getRefObject();
			}
			if (eca == null) {
				throw new Exception("当前对象不存在，无法操作！");
			}
			ext.plm.change.ChangeUtil.createECAFile(eca);
			
			WTChangeRequest2 ecr = null;
			QueryResult changeOrder = ChangeHelper2.service.getChangeOrder( eca );
			if (changeOrder.hasMoreElements()) {
				QueryResult changeRequest = ChangeHelper2.service.getChangeRequest( ( WTChangeOrder2 )changeOrder.nextElement() );
				if (changeRequest.hasMoreElements()) {
					ecr = ( WTChangeRequest2 )changeRequest.nextElement();
				}
			}
			if(ecr!=null){
				ext.plm.change.ChangeUtil.copyECAFile2ECR(eca, ecr);
			}
		}catch (Exception e) {
			e.printStackTrace();
			FormResult result = new FormResult(FormProcessingStatus.FAILURE);
			result.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE,SessionHelper.getLocale(),null,null,new String[]{e.getMessage()}));
			return result;
		} finally {
			if (principal != null) {
				try {
					SessionServerHelper.manager.setAccessEnforced(true);
					SessionHelper.manager.setPrincipal(principal.getName());
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		result.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
		result.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS,SessionHelper.getLocale(),null,null,new String[]{"新建成功！"}));
		return result;
	}

}
