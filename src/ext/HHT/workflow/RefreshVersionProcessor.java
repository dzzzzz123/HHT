package ext.HHT.workflow;

import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.maturity.PromotionNoticeWorkflowHelper;

import wt.maturity.PromotionNotice;
import wt.session.SessionHelper;
import wt.util.WTException;

public class RefreshVersionProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> params) throws WTException {

		FormResult formresult = null;
		Object obj = nmCommandBean.getPrimaryOid().getRefObject();
		System.out.println("----------start to RefreshVersionProcessor:" + obj);

		try {
			if (obj instanceof PromotionNotice) {
				PromotionNotice promotionNotice = (PromotionNotice) obj;
				PromotionNoticeWorkflowHelper promotionNoticeWorkflowHelper = new PromotionNoticeWorkflowHelper(
						promotionNotice, false);
				promotionNoticeWorkflowHelper.updateWithLatestIterations(promotionNotice);
			}

		} catch (Exception e) {
			FormResult result = new FormResult(FormProcessingStatus.FAILURE);
			result.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null, null,
					new String[] { "刷新失败！", e.getMessage() }));
			return formresult;
		}

		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		result.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "刷新成功！" }));
		return formresult;
	}
}
